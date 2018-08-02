package lootermod.cards.generator;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Supplier;

public enum PileType implements Supplier<CardGroup>{

    HAND(() -> AbstractDungeon.player.hand),
    DRAW(() -> AbstractDungeon.player.drawPile),
    DISCARD(() -> AbstractDungeon.player.discardPile),
    DECK(() -> AbstractDungeon.player.masterDeck)
    ;

    PileType(Supplier<CardGroup> retrieve) {
        this.retrieve = retrieve;
    }

    private Supplier<CardGroup> retrieve;


    public CardGroup get() {
        return retrieve.get();
    }
}
