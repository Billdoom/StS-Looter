package lootermod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import lootermod.patches.CardColorEnum;

public class LooterCard extends CustomCard {

    private static int CUR_ID = 1;

    public LooterCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, CardColorEnum.GRAY, rarity, target);
    }

    public LooterCard(String name, String img, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
        super("looter:"+(CUR_ID++), name, img, cost, rawDescription, type, CardColorEnum.GRAY, rarity, target);
    }

    @Override
    public void upgrade() {

    }

    @Override
    public AbstractCard makeCopy() {
        return null;
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }
}
