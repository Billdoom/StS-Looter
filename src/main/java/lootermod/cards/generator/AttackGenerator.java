package lootermod.cards.generator;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import lootermod.cards.DamageType;
import lootermod.cards.LooterCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttackGenerator {

    private WeightedList<TargetType> targetTypes = WeightedList.of(
        new TargetType(AbstractCard.CardTarget.ENEMY,
                1.0f, "", null), 60,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY,0.8f,
                " to ALL enemies", this::allMonsters), 15,
            new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.4f,
                    " to a random enemy", this::randomMonster), 12
    );

    private WeightedList<DamageOption> damageOptions = WeightedList.of(
        new DamageOption(DamageType.NORMAL, 2), 60,
        new DamageOption(DamageType.FIRE, 3.5f), 10,
        new DamageOption(DamageType.COLD, 3), 10,
        new DamageOption(DamageType.LIGHTNING, 2.3f), 10,
        new DamageOption(DamageType.PIERCING, 2.2f), 5,
        new DamageOption(DamageType.POISON, 3.5f), 5
    );

    private Map<AbstractCard.CardRarity, Range> qualityPointsByRarity;

    public AttackGenerator() {
        qualityPointsByRarity = new HashMap<>();
        qualityPointsByRarity.put(AbstractCard.CardRarity.BASIC, new Range(10, 12));
        qualityPointsByRarity.put(AbstractCard.CardRarity.COMMON, new Range(14, 16));
        qualityPointsByRarity.put(AbstractCard.CardRarity.UNCOMMON, new Range(15, 20));
        qualityPointsByRarity.put(AbstractCard.CardRarity.RARE, new Range(22, 30));
    }

    public LooterCard generate(AbstractCard.CardRarity rarity, Random r) {
        Range qualityRange = qualityPointsByRarity.get(rarity);
        int qualityPoints = r.random(qualityRange.low, qualityRange.high);

        TargetType targetType = targetTypes.get(r);
        DamageOption damageOption = damageOptions.get(r);

        int damage = (int)Math.ceil(qualityPoints * targetType.damageScale
                / damageOption.qualityPerDmg);

        StringBuilder desc = new StringBuilder();

        desc.append("Deal !D!").append(damageOption.damageType.keyword)
                .append(" damage").append(targetType.description)
                .append(".");

        LooterCard card = new LooterCard("Attack", null, 1, desc.toString(),
            AbstractCard.CardType.ATTACK, rarity, targetType.targets
        ).addEffect(damageOption.damageType.effect)
        .setEnemySelectionScheme(targetType.scheme);

        card.baseDamage = damage;

        return card;
    }

    private List<AbstractCreature> allMonsters(MonsterGroup monsters) {
        List<AbstractCreature> list = new ArrayList<>();
        for (AbstractMonster monster : monsters.monsters) {
            if (! monster.isDeadOrEscaped()) {
                list.add(monster);
            }
        }
        return list;
    }

    private List<AbstractCreature> randomMonster(MonsterGroup monsters) {
        List<AbstractCreature> list = new ArrayList<>(1);
        list.add(monsters.getRandomMonster(true));
        return list;
    }

    private class TargetType {
        float damageScale;
        AbstractCard.CardTarget targets;
        String description;
        EnemySelectionScheme scheme;

        TargetType(AbstractCard.CardTarget target, float damageScale, String description, EnemySelectionScheme scheme) {
            this.damageScale = damageScale;
            this.targets = target;
            this.description = description;
            this.scheme = scheme;
        }
    }

    private class DamageOption {
        DamageType damageType;
        float qualityPerDmg;

        DamageOption(DamageType type, float qualityPerDmg) {
            this.damageType = type;
            this.qualityPerDmg = qualityPerDmg;
        }
    }

}
