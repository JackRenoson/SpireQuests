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

import static org.apache.commons.lang3.math.NumberUtils.min;

public class TreasureMapQuest extends AbstractQuest {
    public static final Texture X = TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "X.png"));
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
        targetRoom = getAccessableRestSite();
        ShowMarkedNodesOnMapPatch.ImageField.image.set(targetRoom, X);
    }

    private MapRoomNode getAccessableRestSite() {
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
            validRooms.add(topRests.get(AbstractDungeon.mapRng.random(0, topRests.size()-1)));
        }
        return validRooms.get(AbstractDungeon.mapRng.random(0, validRooms.size()-1));
    }

    @Override
    public void onComplete() {
        AbstractDungeon.rareRelicPool.add(Shovel.ID);
    }
}
