package lootermod.cards;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface CardEffect {
    void applyEffect(AbstractCreature source, AbstractCreature target, LooterCard card);
}
