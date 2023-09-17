package no.sandramoen.libgdxjam26.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class ExperienceBar extends ProgressBar {
    public ExperienceBar(float max) {
        super(0, max, 1, false, BaseGame.mySkin);
    }
}
