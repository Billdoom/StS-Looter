package lootermod.cards.generator;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.List;

public interface EnemySelectionScheme {
    List<AbstractCreature> selectTarget(MonsterGroup group);
}
