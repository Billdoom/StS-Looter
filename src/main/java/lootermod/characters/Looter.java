package lootermod.characters;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.relics.SnakeRing;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import lootermod.patches.PlayerClassEnum;

import java.util.ArrayList;

public class Looter extends CustomPlayer {

    public static final int START_HP = 70;
    public static final int ENERGY_PER_TURN = 3; // how much energy you get every turn
    public static final String MY_CHARACTER_SHOULDER_2 = "img/char/shoulder2.png"; // campfire pose
    public static final String MY_CHARACTER_SHOULDER_1 = "img/char/shoulder1.png"; // another campfire pose
    public static final String MY_CHARACTER_CORPSE = "img/char/corpse.png"; // dead corpse
    public static final String MY_CHARACTER_SKELETON_ATLAS = "img/char/idle/skeleton.atlas"; // spine animation atlas
    public static final String MY_CHARACTER_SKELETON_JSON = "img/char/idle/skeleton.json"; // spine animation json
    private static final int START_GOLD = 150;
    private static final int CARD_DRAW = 5;
    public static final String[] orbs = {
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png",
            "img/orbs/empty2.png"
    };

    public Looter(String name, PlayerClass chosenClass) {
        super(name, chosenClass, orbs, "img/orbs/empty2.png", (String)null, null);

        this.initializeClass(MY_CHARACTER_SHOULDER_1, MY_CHARACTER_SHOULDER_2, MY_CHARACTER_SHOULDER_1, MY_CHARACTER_CORPSE,
                getLoadout(), 20.0f, -10.0f, 220.0f, 290.0f, new EnergyManager(ENERGY_PER_TURN));

        this.loadAnimation(MY_CHARACTER_SKELETON_ATLAS, MY_CHARACTER_SKELETON_JSON, 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public static ArrayList<String> getStartingRelics() {
        ArrayList<String> relics = new ArrayList<>();
        relics.add(SnakeRing.ID);
        return relics;
    }

    public static ArrayList<String> getStartingDeck() {
        ArrayList<String> cards = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            cards.add("looter:"+(i+1));
        }

        return cards;
    }

    public static CharSelectInfo getLoadout() {
        return new CharSelectInfo("Chaotician", "Chaos is his game.", START_HP, START_HP, 0,
                START_GOLD, CARD_DRAW, PlayerClassEnum.CHAOTICIAN,
                getStartingRelics(), getStartingDeck(), false);
    }
}
