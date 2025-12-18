package spireQuests.quests;

import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.quests.jackrenoson.TreasureMapQuest;

import java.util.ArrayList;

public interface MarkNodeQuest{
    //public static String id = YOURCLASSHERE.class.getSimpleName(); //id needs to be static and defined per class
    public void MarkNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng);

}
