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
