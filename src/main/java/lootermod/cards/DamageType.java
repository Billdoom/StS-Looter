package lootermod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public enum DamageType {

    NORMAL("", DamageType::basicDamage, null),
    PIERCING(" Piercing", DamageType::piercingDamage, "Skips block"),
    FIRE(" Fire", DamageType::fireDamage, "Deals half of the damage as a burn DoT. NL Does not stack."),
    COLD(" Cold", DamageType::coldDamage, "Deals 1/4 of the damage as temporary strength loss"),
    POISON(" Poison", DamageType::poisonDamage, "Applies poison. Stacks half of the damage."),
    LIGHTNING(" Electrical", DamageType::lightningDamage, "If the enemy has any block, NL deals double damage."),
    LIFESTEAL(" Lifesteal", DamageType::lifestealDamage, "Gain unblocked damage as health");

    public CardEffect effect;
    public String keyword;
    public String description;

    DamageType(String keyword, CardEffect effect, String description) {
        this.keyword = keyword;
        this.effect = effect;
        this.description = description;
    }

    public static void basicDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        dealDamage(source, target, card.damage, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
    }

    public static void piercingDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        int amount = card.damage;
        AbstractDungeon.actionManager.addToBottom(new DamageAction(target,
                new DamageInfo(source, amount, DamageInfo.DamageType.HP_LOSS),
                AbstractGameAction.AttackEffect.SLASH_HEAVY)
        );
    }

    public static void fireDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        int amount = card.damage;
        int burnDamage = amount / 2;
        dealDamage(source, target, amount - burnDamage, AbstractGameAction.AttackEffect.FIRE);

        //TODO: create real burn power
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(target, source, new ConstrictedPower(target, source, burnDamage), burnDamage)
        );
    }

    public static void coldDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        int amount = card.damage;
        int strengthLoss = (int)Math.ceil(amount / 4.0);
        amount -= strengthLoss;

        dealDamage(source, target, amount, AbstractGameAction.AttackEffect.BLUNT_HEAVY);

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, source, new StrengthPower(target, -strengthLoss), -strengthLoss));
        if (target != null && !target.hasPower("Artifact")) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, source, new GainStrengthPower(target, strengthLoss), strengthLoss));
        }
    }

    public static void poisonDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        int amount = card.damage;
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(target, source, new PoisonPower(target, source, amount), amount / 2)
        );
    }

    public static void lightningDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        int amount = card.damage;
        if (target != null && target.currentBlock > 0) {
            amount *= 2;
        }
        dealDamage(source, target, amount, AbstractGameAction.AttackEffect.SMASH);
    }

    public static void lifestealDamage(AbstractCreature source, AbstractCreature target, LooterCard card) {
        AbstractDungeon.actionManager.addToBottom(new VampireDamageAction(target,
                new DamageInfo(source, card.damage, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    private static void dealDamage(AbstractCreature source, AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(target,
                new DamageInfo(source, amount, DamageInfo.DamageType.NORMAL),
                effect)
        );
    }
}
