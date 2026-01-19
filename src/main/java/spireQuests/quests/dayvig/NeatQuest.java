package spireQuests.quests.dayvig;

import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.dayvig.relics.Binder;

public class NeatQuest extends AbstractQuest {
    public NeatQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        new PassiveTracker<Integer>(() -> (int)AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.type == CardType.ATTACK).count(), 10)
            .add(this);
        
        new PassiveTracker<Integer>(() -> (int)AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.type == CardType.SKILL).count(), 10)
            .add(this);
        
        new PassiveTracker<Integer>(() -> (int)AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.type == CardType.POWER).count(), 5)
            .add(this);

        addReward(new QuestReward.RelicReward(new Binder()));
    }
}
