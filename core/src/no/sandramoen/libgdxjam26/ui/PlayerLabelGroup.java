package no.sandramoen.libgdxjam26.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdxjam26.utils.BaseGame;

public class PlayerLabelGroup extends Group {
    public PlayerLabelGroup() {
        TypingLabel label = new TypingLabel("Level Up!", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-59", BitmapFont.class), null));
        addActor(label);
        setScale(.025f);
        setColor(BaseGame.paletteColourIDX12);
        getColor().a = 0f;
    }

    public void showLabelAndAnimate() {
        float moveAmount = 1;
        float moveAndFadeDuration = 1;

        addAction(Actions.sequence(
                Actions.fadeIn(.1f),
                Actions.parallel(
                        Actions.moveBy(0f, moveAmount, moveAndFadeDuration),
                        Actions.fadeOut(moveAndFadeDuration)
                ),
                Actions.moveBy(0f, -moveAmount)
        ));
    }
}
