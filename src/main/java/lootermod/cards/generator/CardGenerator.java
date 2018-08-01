package lootermod.cards.generator;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import lootermod.LooterMod;
import lootermod.cards.LooterCard;

public class CardGenerator {

    private static final CardGenerator instance = new CardGenerator();

    public static CardGenerator get() { return instance; }

    private WeightedList<AbstractCard.CardRarity> rarityWeights = WeightedList.of(
            AbstractCard.CardRarity.COMMON, 40,
            AbstractCard.CardRarity.UNCOMMON, 30,
            AbstractCard.CardRarity.RARE, 20
    );

    private Random r;
    private AttackGenerator attackGenerator = new AttackGenerator();

    private CardGenerator() {
        r = new Random();
        LooterMod.logger.info("Created the random. r="+r.random.toString());
    }

    public LooterCard generateAttack() {
        return generateAttack(rarityWeights.get(r));
    }

    public LooterCard generateAttack(AbstractCard.CardRarity rarity) {
        LooterMod.logger.info("Generating a "+rarity.name()+" attack");
        return attackGenerator.generate(rarity, r);
    }
}
