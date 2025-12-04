package spireQuests.quests.jackrenoson;

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
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.ArrayList;
import java.util.function.Function;

public class TreasureMapQuest extends AbstractQuest {

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
        MapRoomNode targetRoom = getAccessableNode(r -> r.getRoom() instanceof RestRoom);

    }

    private MapRoomNode getAccessableNode(Function<MapRoomNode, Boolean> condition) {
        ArrayList<MapRoomNode> validRooms = new ArrayList<MapRoomNode>();
        checkParents(validRooms, AbstractDungeon.getCurrMapNode(), condition);
        return validRooms.get(AbstractDungeon.mapRng.random(0, validRooms.size()));
    }

    private void checkParents(ArrayList<MapRoomNode> list, MapRoomNode node, Function<MapRoomNode, Boolean> condition){
        for(MapRoomNode parent : node.getParents()){
            if(condition.apply(parent) && !list.contains(parent)){
                list.add(parent);
            }
            checkParents(list, parent, condition);
        }
    }

    @Override
    public void onComplete() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }
}
