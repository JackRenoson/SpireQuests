package spireQuests.quests.jackrenoson;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.QuestManager;
import spireQuests.quests.modargo.GatheringExpeditionQuest;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.apache.commons.lang3.math.NumberUtils.min;

public class TreasureMapQuest extends AbstractQuest {
    public static final Texture X = TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "X.png"));

    public TreasureMapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 1)
                .triggerCondition(r -> ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(r, id))
                .add(this);
    }

    @Override
    public boolean canSpawn(){
        return !AbstractDungeon.player.hasRelic(Shovel.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.rareRelicPool.remove(Shovel.ID);
        MapRoomNode targetRoom = getAccessableRestSite();
        ShowMarkedNodesOnMapPatch.ImageField.MarkNode(targetRoom, id, X);
    }

    private static MapRoomNode getAccessableRestSite() {
        Collection<MapRoomNode> reachableRooms = new ArrayList<MapRoomNode>();
        ArrayList<MapRoomNode> validRooms = new ArrayList<MapRoomNode>();
        ArrayList<MapRoomNode> topRests = new ArrayList<MapRoomNode>();
        int playerFloor = AbstractDungeon.floorNum%17;
        if(playerFloor == 0) {
            reachableRooms.addAll(AbstractDungeon.map.get(playerFloor));
            playerFloor++;
        } else reachableRooms.add(AbstractDungeon.getCurrMapNode());
        for(int i = playerFloor;  i<15;i++) {
            for (MapRoomNode child : AbstractDungeon.map.get(i)) {
                if (!Collections.disjoint(reachableRooms, child.getParents())) {
                    reachableRooms.add(child);
                    if(child.getRoom() instanceof RestRoom && ShowMarkedNodesOnMapPatch.ImageField.image.get(child) == null){
                        if(i<14) {
                            validRooms.add(child);
                        } else { // Makes a top Rest Site much less likely to be the marked Rest Site
                            topRests.add(child);
                        }
                    }
                }
            }
            if (!topRests.isEmpty()) {
                validRooms.add(topRests.get(AbstractDungeon.mapRng.random(0, topRests.size() - 1)));
            }
        }
        return validRooms.get(AbstractDungeon.mapRng.random(0, validRooms.size()-1));
    }

    @Override
    public void onComplete() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }

    @Override
    public void onFail() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }

    public static void markRestSiteIfQuestActive() {
        if (CardCrawlGame.isInARun() && QuestManager.quests().stream().anyMatch(q -> q instanceof TreasureMapQuest && !q.isCompleted())) {
            MapRoomNode targetRoom = getAccessableRestSite();
            ShowMarkedNodesOnMapPatch.ImageField.image.set(targetRoom, X);
        }
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class MarkNodesOnGetDungeonPatch {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeon(CardCrawlGame __instance) {
            if (!Loader.isModLoaded("actlikeit")) {
                markRestSiteIfQuestActive();
            }
        }
    }

    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughProgression", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class }, requiredModId = "actlikeit")
    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughSavefile", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class, SaveFile.class }, requiredModId = "actlikeit")
    public static class MarkNodesOnGetDungeonActLikeIt {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeonActLikeIt() {
            markRestSiteIfQuestActive();
        }
    }
}
