package spireQuests.quests.ramchops;

import basemod.helpers.CardPowerTip;
import basemod.patches.whatmod.RelicTips;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NlothsMask;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CeaselessHungerQuest extends AbstractQuest {

    private boolean initialPickup = false;

    public CeaselessHungerQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);


        new TriggeredUpdateTracker<>(QuestTriggers.ACT_CHANGE, 1, 1, ()->{

            if (!initialPickup && AbstractDungeon.actNum <= 3 && AbstractDungeon.getCurrRoom() != null)  AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH/2f, Settings.HEIGHT/2f, new NlothsMask());

            return 1;
        }).hide().add(this);


        new TriggeredUpdateTracker<>(QuestTriggers.CHEST_OPENED, 0, 1, ()-> AbstractDungeon.actNum == 3 ? 1 : 0).add(this);

        useDefaultReward = false;
        rewardsText = localization.EXTRA_TEXT[1];

        addReward(new QuestReward.RandomRelicReward(AbstractRelic.RelicTier.COMMON));
        addReward(new QuestReward.RandomRelicReward(AbstractRelic.RelicTier.COMMON));
    }


    @Override
    public void onStart() {
        initialPickup = true;
        super.onStart();
        initialPickup = false;

        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH/2f, Settings.HEIGHT/2f, new NlothsMask());
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractRelic> relicsToRemove = (ArrayList<AbstractRelic>) AbstractDungeon.player.relics.stream().filter(r -> r instanceof NlothsMask).collect(Collectors.toList());

        questRewards.clear();

        if (!Settings.hasSapphireKey){
            AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.BLUE));
        }

        for (AbstractRelic byeByeNloth : relicsToRemove){

            AbstractDungeon.player.loseRelic(byeByeNloth.relicId);

            //For each Nloths Mask removed you get 2 relics! What a deal!
            AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
            AbstractRelic relic2 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
            addReward(new QuestReward.RelicReward(relic));
            addReward(new QuestReward.RelicReward(relic2));
        }


        super.onComplete();
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);

        NlothsMask newMask = new NlothsMask();

        tipList.add(new PowerTip(newMask.name, newMask.description));
    }
}
