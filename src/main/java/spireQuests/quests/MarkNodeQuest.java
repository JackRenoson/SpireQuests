package spireQuests.quests;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import java.util.ArrayList;

public interface MarkNodeQuest {

    /**
     * This is called to mark nodes when the quest is picked up.
     * @param map The map of slay the spire, as a list of rows, which are lists of nodes.
     * @param rng the variables used to randomly determine stuff
     */// You should likely need to write this, but not use it yourself.
       // Make sure to use rng for any random selection (not rng()!) and make sure rng is used the same on pickup, and on save and reload.
       // Use ShowMarkedNodesOnMapPatch.MarkNode() in the function to mark nodes.
    void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng);

    /**
     * Automatically generate a new Random based on seed, act number, and quest id.
     * @return new Random for determining stuff randomly
     */// You should likely not need to use this.
       // Override this if your quest has more variables that you want to have influence rng (like TreasureMapQuest)
    default Random rng() {
        return new Random(Settings.seed ^ AbstractDungeon.actNum * 31L ^ ((AbstractQuest) this).id.hashCode());
    }

    /**
     * Field to track if a quest has already marked a node.
     *///generally, you don't need to worry about it.
    @SpirePatch2(clz = AbstractDungeon.class, method = SpirePatch.CLASS)
    public static class MarkedField {
        public static SpireField<Boolean> marked = new SpireField<>(() -> false);
    }

    /**
     * Patches for loading markings at a new act, and after saving and resuming a run.
     *///Again, don't worry about it.
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class MarkNodesOnGetDungeonPatch {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeon(CardCrawlGame __instance) {
            if (!Loader.isModLoaded("actlikeit") && !MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for(Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()){
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }

    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughProgression", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class }, requiredModId = "actlikeit")
    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughSavefile", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class, SaveFile.class }, requiredModId = "actlikeit")
    public static class MarkNodesOnGetDungeonActLikeIt {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeonActLikeIt() {
            if (!MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for (Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()) {
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }

    // When loading a save file, populatePathTaken calls nextRoomTransition, which trigger ENTER_ROOM for quests.
    // We need the markings to be on the map before that.
    // In theory, now that this patch exists we might be able to get rid of the SaveFile versions of the other patches.
    // However, we've left them in place in case there are code paths that still need them, and because we check whether
    // the marking has already been done so it should be safe
    @SpirePatch2(clz = AbstractDungeon.class, method = "populatePathTaken")
    public static class MarkNodesBeforePopulatePathTaken {
        @SpirePrefixPatch
        public static void markNodesBeforePopulatePathTaken() {
            if (!MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for (Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()) {
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }
}
