package lootermod;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import lootermod.cards.DamageType;
import lootermod.cards.generator.CardGenerator;
import lootermod.characters.Looter;
import lootermod.patches.CardColorEnum;
import lootermod.patches.PlayerClassEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class LooterMod implements EditCharactersSubscriber,
        EditCardsSubscriber, PostInitializeSubscriber,
        EditKeywordsSubscriber
{

    public static final Logger logger = LogManager.getLogger(LooterMod.class.getName());

    public static final String MODNAME = "LooterMod";
    public static final String AUTHOR = "Billdoom";
    public static final String DESCRIPTION = "v0.0.1\nAdds a new character: the Chaotician,\nwhose cards are procedurally generated.";

    public LooterMod() {
        BaseMod.subscribe(this);
        receiveEditColors();
    }

    public static void initialize() {
        new LooterMod();
    }

    private static final Color GRAY = new Color(0.8f, 0.8f, 0.8f, 1.0f);

    public void receiveEditColors() {
        logger.info("begin editing colors");
        BaseMod.addColor(
                CardColorEnum.GRAY.toString(),
                GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY,
                "img/cardui/512/bg_attack_gray.png",
                "img/cardui/512/bg_skill_gray.png",
                "img/cardui/512/bg_power_gray.png",
                "img/cardui/512/card_gray_orb.png",
                "img/cardui/1024/bg_attack_gray.png",
                "img/cardui/1024/bg_skill_gray.png",
                "img/cardui/1024/bg_power_gray.png",
                "img/cardui/1024/card_gray_orb.png");
        logger.info("done editing colors");
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(Looter.class, "Chaotician", "chaotician", CardColorEnum.GRAY.toString(),
                "Chaotician", "img/char/selectButton.png",
                "img/char/portrait.jpg", PlayerClassEnum.CHAOTICIAN.toString());
    }

    @Override
    public void receiveEditCards() {
        for (int i = 0; i < 30; i++) {
            BaseMod.addCard(CardGenerator.get().generateAttack());
        }
    }

    @Override
    public void receivePostInitialize() {
        logger.info("initialize mod badge");
        // Mod badge
        Texture badgeTexture = new Texture("img/LRelicBadge.png");
        ModPanel settingsPanel = new ModPanel();
        settingsPanel.addLabel("This mod does not have any settings.", 400.0f, 700.0f, (me) -> {});
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
        logger.info("done with mod badge");
    }

    @Override
    public void receiveEditKeywords() {
        for (DamageType damageType : DamageType.values()) {
            if (damageType.description != null) {
                String trimmed = damageType.keyword.trim();
                BaseMod.addKeyword(new String[]{trimmed, trimmed.toLowerCase()}, damageType.description);
            }
        }
    }
}
