package lootermod.cards.generator;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.random.Random;
import lootermod.Util;
import lootermod.cards.DamageType;
import lootermod.cards.LooterCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttackGenerator {

    private WeightedList<TargetType> targetTypes = WeightedList.of(
        new TargetType(AbstractCard.CardTarget.ENEMY,
                1.0f, "", null), 60,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY,0.8f,
            " to ALL enemies", Util::allMonsters), 15,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.4f,
            " to a random enemy", Util::randomMonster), 12,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.3f,
                " to the first enemy", Util::leftMostEnemy), 8,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.7f,
                " to uninjured enemies", Util::uninjuredEnemies), 3,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.7f,
                " to half-dead enemies", Util::halfDead), 2,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.2f,
                " to attacking enemies", Util::attackingEnemies), 3,
        new TargetType(AbstractCard.CardTarget.ALL_ENEMY, 1.25f,
                " to non-attacking enemies", Util::notAttackingEnemies), 3
    );

    private WeightedList<DamageOption> damageOptions = WeightedList.of(
        new DamageOption(DamageType.NORMAL, 2), 60,
        new DamageOption(DamageType.FIRE, 3.5f), 10,
        new DamageOption(DamageType.COLD, 3), 10,
        new DamageOption(DamageType.LIGHTNING, 2.3f), 10,
        new DamageOption(DamageType.PIERCING, 2.2f), 5,
        new DamageOption(DamageType.POISON, 3.3f), 5,
        new DamageOption(DamageType.LIFESTEAL, 4.0f), 2
    );

    private Map<AbstractCard.CardRarity, Range> qualityPointsByRarity;
    private Map<AbstractCard.CardRarity, WeightedList<Integer>> drawbacksByRarity;
    private Map<AbstractCard.CardRarity, WeightedList<Float>> bonusPctByRarity;

    public AttackGenerator() {
        qualityPointsByRarity = new HashMap<>();
        qualityPointsByRarity.put(AbstractCard.CardRarity.BASIC, new Range(10, 12));
        qualityPointsByRarity.put(AbstractCard.CardRarity.COMMON, new Range(14, 16));
        qualityPointsByRarity.put(AbstractCard.CardRarity.UNCOMMON, new Range(15, 20));
        qualityPointsByRarity.put(AbstractCard.CardRarity.RARE, new Range(22, 30));

        drawbacksByRarity = new HashMap<>();
        drawbacksByRarity.put(AbstractCard.CardRarity.COMMON, WeightedList.of(
            0, 50,
            1, 30
        ));
        drawbacksByRarity.put(AbstractCard.CardRarity.UNCOMMON, WeightedList.of(
            0, 50,
            1, 50,
            2, 10
        ));
        drawbacksByRarity.put(AbstractCard.CardRarity.RARE, WeightedList.of(
            0, 30,
            1, 50,
            2, 20,
            3, 5
        ));

        bonusPctByRarity = new HashMap<>();
        bonusPctByRarity.put(AbstractCard.CardRarity.COMMON, WeightedList.of(
            0f, 50,
            0.3f, 10,
            0.5f, 5
        ));
        bonusPctByRarity.put(AbstractCard.CardRarity.UNCOMMON, WeightedList.of(
            0f, 40,
            0.2f, 20,
            0.35f, 10,
            0.6f, 5
        ));
        bonusPctByRarity.put(AbstractCard.CardRarity.RARE, WeightedList.of(
            0.15f, 20,
            0.2f, 20,
            0.3f, 10,
            0.4f, 10,
            0.7f, 5
        ));
    }

    public LooterCard generate(AbstractCard.CardRarity rarity, Random r) {
        Range qualityRange = qualityPointsByRarity.get(rarity);
        int qualityPoints = r.random(qualityRange.low, qualityRange.high);

        TargetType targetType = targetTypes.get(r);
        DamageOption damageOption = damageOptions.get(r);

        List<Drawback> drawbacks = Drawback.getDrawbacks(AbstractCard.CardType.ATTACK,
            drawbacksByRarity.get(rarity).get(r), r);

        StringBuilder desc = new StringBuilder();

        desc.append("Deal !D!").append(damageOption.damageType.keyword)
                .append(" damage").append(targetType.description)
                .append(". NL ");

        double drawbackBonusMod = Math.pow(0.8, drawbacks.size() - 1);

        for (Drawback drawback : drawbacks) {
            qualityPoints += drawback.qualityPoints * drawbackBonusMod;
        }

        qualityPoints *= targetType.damageScale;

        float pctBonuses = bonusPctByRarity.get(rarity).get(r);

        List<Bonus> bonuses = Bonus.getBonuses(AbstractCard.CardType.ATTACK,
            (int)Math.ceil(qualityPoints * pctBonuses), r, drawbacks);

        boolean hasBonusDesc = false;

        for (Bonus bonus : bonuses) {
            String bonusDesc = bonus.getDescription();
            hasBonusDesc |= ! bonusDesc.isEmpty();
            desc.append(bonusDesc);
            qualityPoints -= bonus.currentSpend;
        }
        if (hasBonusDesc) {
            desc.append(" NL ");
        }

        for (Drawback drawback : drawbacks) {
            if (drawback.description != null) {
                desc.append(drawback.description).append(" ");
            }
        }

        int damage = (int)Math.ceil(qualityPoints / damageOption.qualityPerDmg);

        LooterCard card = new LooterCard("Attack", null, 1, desc.toString(),
            AbstractCard.CardType.ATTACK, rarity, targetType.targets
        ).addEffect(damageOption.damageType.effect)
        .setEnemySelectionScheme(targetType.scheme);

        card.baseDamage = damage;

        for (Drawback drawback : drawbacks) {
            drawback.applyToCard(card);
        }

        for (Bonus bonus : bonuses) {
            bonus.applyToCard(card);
        }

        return card;
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
