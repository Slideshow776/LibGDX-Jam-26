package no.sandramoen.libgdxjam26.ui.experienceBar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdxjam26.utils.BaseActor;

public class ProgressComponent extends BaseActor {
    public ProgressComponent(float x, float height, Stage stage) {
        super(x, 0, stage);
        loadImage("GUI/xp bar");
        setSize(1, height);
        setOrigin(Align.left);

        for (Actor actor : getChildren()) actor.remove(); // hack to remove duplicate tiny lil self
    }
}
