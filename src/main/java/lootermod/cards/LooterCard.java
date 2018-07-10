package lootermod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import lootermod.cards.generator.EnemySelectionScheme;
import lootermod.patches.CardColorEnum;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LooterCard extends CustomCard {

    private static int CUR_ID = 1;

    public LooterCard(String name, String img, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
        this("looter:"+(CUR_ID++), name, img, cost, rawDescription, type, rarity, target);
    }

    public LooterCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, CardColorEnum.GRAY, rarity, target);
    }

    @Override
    public void upgrade() {

    }

    private EnemySelectionScheme enemySelectionScheme;

    public LooterCard setEnemySelectionScheme(EnemySelectionScheme scheme) {
        this.enemySelectionScheme = scheme;
        return this;
    }

    private List<CardEffect> effects = new LinkedList<>();

    public LooterCard addEffect(CardEffect effect) {
        this.effects.add(effect);
        return this;
    }

    @Override
    public AbstractCard makeCopy() {
        LooterCard copy = new LooterCard(this.cardID, name, null, cost, rawDescription, type, rarity, target);
        copy.effects = this.effects;
        copy.enemySelectionScheme = this.enemySelectionScheme;
        copy.baseDamage = this.baseDamage;
        copy.baseBlock = this.baseBlock;
        return copy;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        List<AbstractCreature> targets;
        if (target == CardTarget.ENEMY) {
            targets = new ArrayList<>(1);
            targets.add(monster);
        } else if (target == CardTarget.SELF) {
            targets = new ArrayList<>(1);
            targets.add(player);
        } else {
            targets = enemySelectionScheme.selectTarget(AbstractDungeon.getMonsters());
        }
        for (AbstractCreature target : targets) {
            for (CardEffect effect : effects) {
                effect.applyEffect(player, target, this);
            }
        }
    }
}
