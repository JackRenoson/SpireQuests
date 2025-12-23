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
}
