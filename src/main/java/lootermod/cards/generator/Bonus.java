package lootermod.cards.generator;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.random.Random;
import lootermod.LooterMod;
import lootermod.cards.LooterCard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Bonus implements CardComponent {

    private int minQualityPoints;
    private float costPerPoint;
    private int currentValue;
    private int maxVal;
    public int currentSpend;
    private String description;
    public MXGroup mutualExclusionGroup;
    private BonusComponent modifier;
    private boolean[] allowedOnType;

    public Bonus(int qualityPoints, BonusComponent modifier) {
        this.minQualityPoints = qualityPoints;
        this.costPerPoint = qualityPoints;
        this.maxVal = -1;
        this.modifier = modifier;
        allowedOnType = new boolean[AbstractCard.CardType.values().length];
        Arrays.fill(allowedOnType, true);
    }

    public Bonus notOn(AbstractCard.CardType... types) {
        for (AbstractCard.CardType type : types) {
            allowedOnType[type.ordinal()] = false;
        }
        return this;
    }

    public Bonus costPerPoint(float amount) {
        this.costPerPoint = amount;
        return this;
    }

    public Bonus maxValue(int amount) {
        this.maxVal = amount;
        return this;
    }

    public Bonus describe(String description) {
        this.description = description;
        return this;
    }

    public Bonus group(MXGroup group) {
        this.mutualExclusionGroup = group;
        return this;
    }

    public int spendPoints(int amount, Random random) {
        int maxSpend = (int)(amount / costPerPoint);
        if (maxVal != -1 && maxSpend > maxVal) {
            maxSpend = maxVal;
        }
        this.currentValue = random.random(1, maxSpend);
        this.currentSpend = (int)(costPerPoint * this.currentValue);
        return amount - currentSpend;
    }

    public String getDescription() {
        if (description != null) {
            return description.replace("!V!", ""+currentValue)+" ";
        } else {
            return "";
        }
    }

    private interface BonusComponent {
        void applyToCard(LooterCard card, int value);
    }

    @Override
    public void applyToCard(LooterCard card) {
        modifier.applyToCard(card, currentValue);
    }

    private static WeightedList<Bonus> BONUSES = WeightedList.of(
        new Bonus(10, (card, value) -> {
            card.baseBlock = value * 2;
            card.addSelfEffect((p, p2, c) ->
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, value * 2)));
        }).notOn(AbstractCard.CardType.SKILL)
            .describe("Gain !B! block.")
            .costPerPoint(6.5f)
            .group(MXGroup.BLOCK), 20,
        new Bonus(20, (card, value) ->
            card.addSelfEffect((p, p2, c) ->
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, value), value))
        ))
            .describe("Gain !V! strength.")
            .costPerPoint(15f)
            .group(MXGroup.STRENGTH), 10,
        // -1 Cost
        new Bonus(15, (card, value) -> card.cost--
        ).group(MXGroup.ENERGY), 15,

        new Bonus(12, (card, value) -> {
            card.baseDraw += value;
            card.addSelfEffect((p, p2, c) ->
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, value))
            );
        })
            .describe("Draw !V! card(s).")
            .maxValue(3)
            .group(MXGroup.DRAW), 20,
        new Bonus(10, (card, value) -> card.addEffect((p, m, c) ->
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new WeakPower(m, value, false), value))
        ))
            .describe("Apply !V! Weak.")
            .costPerPoint(8)
            .maxValue(3)
            .group(MXGroup.WEAK), 20,
        new Bonus(15, (card, value) -> card.addEffect((p, m, c) ->
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new VulnerablePower(m, value, false), value))
        ))
            .describe("Apply !V! Vulnerable.")
            .costPerPoint(12)
            .maxValue(3)
            .group(MXGroup.VULNERABLE), 20
    );

    public static List<Bonus> getBonuses(AbstractCard.CardType type, int pointsToSpend, Random random,
                                         List<Drawback> drawbacks) {
        List<Bonus> bonuses = new LinkedList<>();

        while (pointsToSpend > 0) {
            final int pointsLeft = pointsToSpend;
            Bonus newBonus = BONUSES.get(random, b -> {
                for (Drawback d : drawbacks) {
                    if (d.mutualExclusionGroup == b.mutualExclusionGroup) {
                        return false;
                    }
                }

                for (Bonus bonus : bonuses) {
                    if (bonus.mutualExclusionGroup == b.mutualExclusionGroup) {
                        return false;
                    }
                }

                return b.minQualityPoints <= pointsLeft && b.allowedOnType[type.ordinal()];
            });
            if (null == newBonus) {
                break;
            }
            pointsToSpend = newBonus.spendPoints(pointsLeft, random);
            bonuses.add(newBonus);
        }

        return bonuses;
    }



}
