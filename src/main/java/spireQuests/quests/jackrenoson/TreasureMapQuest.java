package spireQuests.quests.jackrenoson;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.modargo.GatheringExpeditionQuest;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.apache.commons.lang3.math.NumberUtils.min;
import static spireQuests.Anniv8Mod.makeID;

public class TreasureMapQuest extends AbstractQuest implements MarkNodeQuest {
    public static final Texture X = TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "X.png"));
    private final MapRoomNode origin;
    public static final String id = makeID(TreasureMapQuest.class.getSimpleName());

    public TreasureMapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        origin = AbstractDungeon.getCurrMapNode();
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
    }

    @Override
    public void onComplete() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }

    @Override
    public void onFail() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }

    @Override
    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {
        ArrayList<MapRoomNode> toBeChecked = new ArrayList<>();
        ArrayList<MapRoomNode> validRooms = new ArrayList<>();
        ArrayList<MapRoomNode> checkedRooms = new ArrayList<>();
        ArrayList<MapRoomNode> topRests = new ArrayList<>();
        toBeChecked.add(origin);
        while(!toBeChecked.isEmpty()) {
            MapRoomNode curr = toBeChecked.remove(0);
            if (curr.y == -1) { //Neow room
                toBeChecked.addAll(map.get(0));
            } else {
                if (!checkedRooms.contains(curr)) {
                    if (curr.y == 14 && curr.getRoom() instanceof RestRoom) {
                        topRests.add(curr);
                    } else {
                        if(curr.getRoom() instanceof RestRoom){
                            validRooms.add(curr);
                        }
                        if (curr.hasEdges()) {
                            for (MapEdge edge : curr.getEdges()) {
                                for (MapRoomNode node : map.get(edge.dstY)) {
                                    if (node.x == edge.dstX) {
                                        toBeChecked.add(node);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    checkedRooms.add(curr);
                }
            }
        }
        if (!topRests.isEmpty()) {
            validRooms.add(topRests.get(rng.random(0, topRests.size() - 1)));
        }
        MapRoomNode targetRoom = validRooms.get(rng.random(0, validRooms.size()-1));
        System.out.println("TARGET:" + targetRoom);
        ShowMarkedNodesOnMapPatch.ImageField.MarkNode(targetRoom, id, X);
    }

    @Override
    public Random rng() {
        return new Random();
    }
}
