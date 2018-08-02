package lootermod;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import lootermod.cards.generator.PileType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {

    private static boolean isAttacking(AbstractMonster m) {
        switch(m.intent) {
            case ATTACK:
            case ATTACK_BUFF:
            case ATTACK_DEBUFF:
            case ATTACK_DEFEND:
                return true;
            default:
                return false;
        }
    }

    public static List<AbstractCreature> allMonsters(MonsterGroup monsters) {
        List<AbstractCreature> list = new ArrayList<>();
        for (AbstractMonster monster : monsters.monsters) {
            if (! monster.isDeadOrEscaped()) {
                list.add(monster);
            }
        }
        return list;
    }

    public static List<AbstractCreature> randomMonster(MonsterGroup monsters) {
        List<AbstractCreature> list = new ArrayList<>(1);
        list.add(monsters.getRandomMonster(true));
        return list;
    }

    public static List<AbstractCreature> leftMostEnemy(MonsterGroup monsters) {
        List<AbstractCreature> list = new ArrayList<>(1);
        list.add(monsters.monsters.stream().filter(m -> ! m.isDeadOrEscaped()).findFirst().get());
        return list;
    }

    public static List<AbstractCreature> uninjuredEnemies(MonsterGroup monsters) {
        return filterEnemies(monsters, m -> m.maxHealth == m.currentHealth);
    }

    public static List<AbstractCreature> halfDead(MonsterGroup monsters) {
        return filterEnemies(monsters, m -> m.currentHealth * 2 <= m.maxHealth);
    }

    public static List<AbstractCreature> attackingEnemies(MonsterGroup monsters) {
        return filterEnemies(monsters, Util::isAttacking);
    }

    public static List<AbstractCreature> notAttackingEnemies(MonsterGroup monsters) {
        return filterEnemies(monsters, m -> ! Util.isAttacking(m));
    }

    private static List<AbstractCreature> filterEnemies(MonsterGroup monsters, Predicate<AbstractMonster> filter) {
        return monsters.monsters.stream()
                .filter(m -> ! m.isDeadOrEscaped())
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static void addCardToPile(AbstractCard card, PileType pile) {
        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
        pile.get().addToRandomSpot(card);
    }
}
