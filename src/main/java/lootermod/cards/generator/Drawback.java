package lootermod.cards.generator;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.random.Random;
import lootermod.Util;
import lootermod.cards.LooterCard;

import java.util.LinkedList;
import java.util.List;

public class Drawback implements CardComponent {

    public int qualityPoints;
    private CardComponent modifier;
    private boolean allowedOnPowers = true;
    public String description;
    public MXGroup mutualExclusionGroup;

    public Drawback(int qualityPoints, CardComponent modifier) {
        this.qualityPoints = qualityPoints;
        this.modifier = modifier;
    }

    public Drawback excludeOnPowers() {
        this.allowedOnPowers = false;
        return this;
    }

    public Drawback describe(String description) {
        this.description = description;
        return this;
    }

    public Drawback group(MXGroup groupId) {
        this.mutualExclusionGroup = groupId;
        return this;
    }

    @Override
    public void applyToCard(LooterCard card) {
        modifier.applyToCard(card);
    }

    private static WeightedList<Drawback> DRAWBACKS = WeightedList.of(
        new Drawback(15, card -> card.exhaust = true)
            .excludeOnPowers()
            .describe("Exhaust.")
            .group(MXGroup.EXHAUST), 20,
        new Drawback(8, card -> card.isEthereal = true)
            .describe("Ethereal.")
            .group(MXGroup.ETHEREAL), 13,
        new Drawback(6, discard(1, false))
            .describe("Discard a card.")
            .group(MXGroup.DISCARD), 14,
        new Drawback(10, discard(2, false))
            .describe("Discard 2 cards.")
            .group(MXGroup.DISCARD), 5,
        new Drawback(10, discard(1, true))
            .describe("Discard a random card.")
            .group(MXGroup.DISCARD), 10,
        new Drawback(16, discard(2, true))
            .describe("Discard 2 random cards.")
            .group(MXGroup.DISCARD), 4,
        new Drawback(14, loseHP(2))
            .describe("Lose 2 HP.")
            .group(MXGroup.HP), 7,
        new Drawback(20, loseHP(4))
            .describe("Lose 4 HP.")
            .group(MXGroup.HP), 5,
        // +1 Cost
        new Drawback(20, card -> card.cost++)
            .group(MXGroup.ENERGY), 14,
        // +2 Cost
        new Drawback(40, card -> card.cost += 2)
            .group(MXGroup.ENERGY), 2,
        new Drawback(15, loseStrength(1, false))
            .describe("Lose 1 strength.")
            .group(MXGroup.STRENGTH), 7,
        new Drawback(9, loseStrength(3, true))
            .describe("Lose 3 strength this turn.")
            .group(MXGroup.STRENGTH), 5,
        new Drawback(11, gainVulnerable(1))
            .describe("Become Vulnerable.")
            .group(MXGroup.VULNERABLE_SELF), 5,
        new Drawback(16, gainVulnerable(2))
            .describe("Gain 2 Vulnerable.")
            .group(MXGroup.VULNERABLE_SELF), 3,
        new Drawback(5, gainWeakness(1))
            .describe("Become Weak.")
            .group(MXGroup.WEAK_SELF), 5,
        new Drawback(8, gainWeakness(2))
            .describe("Gain 2 Weak.")
            .group(MXGroup.WEAK_SELF), 3,
        new Drawback(12, loseDraw(1))
            .describe("-1 Draw next turn.")
            .group(MXGroup.DRAW_NEXT_TURN), 3,
        new Drawback(16, loseDraw(2))
            .describe("-2 Draw next turn.")
            .group(MXGroup.DRAW_NEXT_TURN), 2,
        new Drawback(10, gainCurse(PileType.HAND))
            .describe("Gain a curse in hand.")
            .group(MXGroup.CURSE), 2,
        new Drawback(15, gainCurse(PileType.DRAW))
            .describe("Put a curse in your draw pile.")
            .group(MXGroup.CURSE), 2,
        new Drawback(8, gainCurse(PileType.DISCARD))
            .describe("Put a curse in your discard pile.")
            .group(MXGroup.CURSE), 2
    );

    public boolean equals(Object other) {
        if (other instanceof Drawback) {
            return this.mutualExclusionGroup ==
                ((Drawback)other).mutualExclusionGroup;
        }
        return false;
    }

    public static List<Drawback> getDrawbacks(AbstractCard.CardType type, int amount, Random random) {
        final List<Drawback> list = new LinkedList<>();

        for (int i = 0; i < amount; i++) {
            Drawback toAdd = DRAWBACKS.get(random,
                d -> ! list.contains(d) && (type != AbstractCard.CardType.POWER || d.allowedOnPowers)
            );
            if (toAdd != null) {
                list.add(toAdd);
            } else {
                break;
            }
        }

        return list;
    }

    private static CardComponent discard(final int amount, final boolean random) {
        return card -> {
            card.baseDiscard += amount;
            card.addSelfEffect((p, p2, card2) ->
                AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, amount, random))
            );
        };
    }

    private static CardComponent loseHP(final int amount) {
        return card -> card.addSelfEffect(((source, target, card1) ->
            AbstractDungeon.actionManager.addToBottom(new LoseHPAction(target, target, amount))
        ));
    }

    private static CardComponent loseStrength(final int amount, final boolean temporary) {
        return card -> card.addSelfEffect(((source, target, card1) -> {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source,
                new StrengthPower(source, -amount), -amount));
            if (temporary) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source,
                    new LoseStrengthPower(source, -amount), -amount));
           }
        }));
    }

    private static CardComponent gainVulnerable(final int amount) {
        return card -> card.addSelfEffect((source, target, card1) ->
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source,
                new VulnerablePower(source, amount, false), amount))
        );
    }

    private static CardComponent gainWeakness(final int amount) {
        return card -> card.addSelfEffect((source, target, card1) ->
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source,
                new WeakPower(source, amount, false), amount))
        );
    }

    private static CardComponent loseDraw(final int amount) {
        return card -> card.addSelfEffect((source, target, card1) ->
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source,
                new DrawDownPower(source, amount), amount))
        );
    }

    private static CardComponent gainCurse(final PileType pile) {
        return card -> card.addSelfEffect((source, target, card1) -> {
            Util.addCardToPile(AbstractDungeon.returnRandomCurse().makeStatEquivalentCopy(), pile);
        });
    }
}
