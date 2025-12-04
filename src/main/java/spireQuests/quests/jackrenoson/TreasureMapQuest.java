package spireQuests.quests.jackrenoson;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.modargo.relics.MulticlassEmblem;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class TreasureMapQuest extends AbstractQuest {
    private static final Texture X = TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "X.png"));
    private MapRoomNode targetRoom;

    public TreasureMapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        needHoverTip = true;
    }

    @Override
    public boolean canSpawn(){
        return !AbstractDungeon.player.hasRelic(Shovel.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.rareRelicPool.remove(Shovel.ID);
        targetRoom = getAccessableNode(r -> r.getRoom() instanceof RestRoom &&
                ShowMarkedNodesOnMapPatch.ImageField.image.get(r) == null);
        ShowMarkedNodesOnMapPatch.ImageField.image.set(targetRoom, X);
    }

    /**
     * Checks all rooms in the next row, saves all rooms that have a reachable room as a parent as a reachable room, and gives a random reachable room that fits the condition.
     * @param condition that the to return room has to hold for
     * @return reachable room for which the condition holds
     */
    private MapRoomNode getAccessableNode(Function<MapRoomNode, Boolean> condition) {
        Collection<MapRoomNode> reachableRooms = new ArrayList<MapRoomNode>();
        ArrayList<MapRoomNode> validRooms = new ArrayList<MapRoomNode>();
        int playerFloor = AbstractDungeon.floorNum%17;
        if(playerFloor == 0) {
            reachableRooms.addAll(AbstractDungeon.map.get(playerFloor));
            playerFloor++;
        } else reachableRooms.add(AbstractDungeon.getCurrMapNode());
        for(int i = playerFloor; i<15;i++) {
            for (MapRoomNode child : AbstractDungeon.map.get(i)) {
                if (!Collections.disjoint(reachableRooms, child.getParents())) {
                    reachableRooms.add(child);
                    if(condition.apply(child)){
                        validRooms.add(child);
                    }
                }
            }
        }
        return validRooms.get(AbstractDungeon.mapRng.random(0, validRooms.size()-1));
    }

    private void checkParents(ArrayList<MapRoomNode> list, ArrayList<MapRoomNode> checkedList, MapRoomNode node, Function<MapRoomNode, Boolean> condition){
        System.out.println("checkParents");
        for(MapRoomNode parent : node.getParents()){
            System.out.println("There are parents");
            if(!checkedList.contains(parent)) {
                System.out.println("Not checked");
                if(condition.apply(parent) && !list.contains(parent)){
                    System.out.println("Conditions apply");
                    list.add(parent);
                }
                checkedList.add(parent);
                checkParents(list, checkedList, parent, condition);
            }
        }
    }

    @Override
    public void onComplete() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }
}
