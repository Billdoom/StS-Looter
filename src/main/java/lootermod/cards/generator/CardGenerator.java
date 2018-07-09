package lootermod.cards.generator;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import lootermod.cards.LooterCard;

public class CardGenerator {

    private static final CardGenerator instance = new CardGenerator();

    public static final CardGenerator get() { return instance; }

    private WeightedList<AbstractCard.CardRarity> rarityWeights = WeightedList.of(
            AbstractCard.CardRarity.COMMON, 40,
            AbstractCard.CardRarity.UNCOMMON, 20,
            AbstractCard.CardRarity.RARE, 10
    );

    private Random r;

    private CardGenerator() {
        r = AbstractDungeon.cardRng;
    }

    public LooterCard generateAttack() {
        return generateAttack(rarityWeights.get(r));
    }

    public LooterCard generateAttack(AbstractCard.CardRarity rarity) {

    }
}
