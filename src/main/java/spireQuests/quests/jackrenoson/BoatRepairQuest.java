package spireQuests.quests.jackrenoson;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.*;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class BoatRepairQuest extends AbstractQuest implements MarkNodeQuest {
    private int startX, startY;
    public static final String id = makeID(BoatRepairQuest.class.getSimpleName());
    private static final ArrayList<Texture> textures = new ArrayList<>(Arrays.asList(TexLoader.getTexture(Anchor.IMG_DIR), TexLoader.getTexture(HornCleat.IMG_DIR), TexLoader.getTexture(CaptainsWheel.IMG_DIR)));

    public BoatRepairQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        startX = 0;
        startY = -1;
        if(CardCrawlGame.isInARun()) {
            MapRoomNode origin = AbstractDungeon.getCurrMapNode();
            startX = origin.x;
            startY = origin.y;
        }

        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(Anchor.ID))
                .add(this);
        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(HornCleat.ID))
                .add(this);
        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(CaptainsWheel.ID))
                .add(this);

        new TreasureMapQuest.SaveNodeTracker(startX, startY).hide().add(this);
    }

    @Override
    public boolean canSpawn(){
        if(AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) AbstractDungeon.getCurrRoom();
            for(AbstractRelic r : shop.relics) {
                if(r.relicId.equals(Anchor.ID) || r.relicId.equals(HornCleat.ID) || r.relicId.equals(CaptainsWheel.ID)) return false;
            }
        }
        return !AbstractDungeon.player.hasRelic(Anchor.ID) || AbstractDungeon.player.hasRelic(HornCleat.ID) || AbstractDungeon.player.hasRelic(CaptainsWheel.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.commonRelicPool.remove(Anchor.ID);
        AbstractDungeon.uncommonRelicPool.remove(HornCleat.ID);
        AbstractDungeon.rareRelicPool.remove(CaptainsWheel.ID);
    }

    @Override
    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {
        ArrayList<MapRoomNode> toBeChecked = new ArrayList<>();
        ArrayList<MapRoomNode> monsterRooms = new ArrayList<>();
        ArrayList<MapRoomNode> restRooms = new ArrayList<>();
        ArrayList<MapRoomNode> shopRooms = new ArrayList<>();
        ArrayList<MapRoomNode> eliteRooms = new ArrayList<>();
        ArrayList<MapRoomNode> treasureRooms = new ArrayList<>();
        ArrayList<MapRoomNode> eventRooms = new ArrayList<>();
        ArrayList<MapRoomNode> checkedRooms = new ArrayList<>();
        toBeChecked.add(getNode(startX, startY));
        while(!toBeChecked.isEmpty()) {
            MapRoomNode curr = toBeChecked.remove(0);
            if (curr == null || curr.y == -1) { //Neow room
                toBeChecked.addAll(map.get(0));
            } else {
                if (!checkedRooms.contains(curr)) {
                    switch(curr.getRoom().getClass().toString()){
                        case "class com.megacrit.cardcrawl.rooms.MonsterRoom": monsterRooms.add(curr); break;
                        case "class com.megacrit.cardcrawl.rooms.RestRoom": restRooms.add(curr); break;
                        case "class com.megacrit.cardcrawl.rooms.TreasureRoom": treasureRooms.add(curr); break;
                        case "class com.megacrit.cardcrawl.rooms.EventRoom": eventRooms.add(curr); break;
                        case "class com.megacrit.cardcrawl.rooms.MonsterRoomElite": eliteRooms.add(curr); break;
                        case "class com.megacrit.cardcrawl.rooms.ShopRoom": shopRooms.add(curr); break;
                    }
                    if (curr.hasEdges()) {
                        for (MapEdge edge : curr.getEdges()) {
                            MapRoomNode node = getNode(edge);
                            if(node!=null) {
                                toBeChecked.add(node);
                            }
                        }
                    }
                    checkedRooms.add(curr);
                }
            }
        }
        ArrayList<MapRoomNode> validRooms = new ArrayList<>();
        for(ArrayList<MapRoomNode> list : Arrays.asList(monsterRooms, restRooms, treasureRooms, eventRooms, eliteRooms, shopRooms)) {
            if(!list.isEmpty())
                validRooms.add(list.get(rng.random(0, list.size() - 1)));
        }
        int lim = validRooms.size();
        for(int i = 0; i<Math.min(lim, 3); i++) {
            MapRoomNode targetRoom = validRooms.remove(rng.random(0, validRooms.size() - 1));
            ShowMarkedNodesOnMapPatch.ImageField.MarkNode(targetRoom, id, textures.get(i));
            System.out.println("("+targetRoom.x+","+targetRoom.y+") " + targetRoom.getRoom());
        }
    }

    @Override
    public Random rng() {
        return new Random(Settings.seed ^ (long) AbstractDungeon.actNum * 31 ^ (long) (startY + 1) * 37 ^ (long) startX * 41 ^ id.hashCode());
    }
}
