package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdxjam26.utils.BaseActor;

public class ExperienceBar extends BaseActor {
    private BaseActor progress;

    private float nextLevel = 10f;
    private float currentXP = 0f;
    private float constant = 2f;
    private float ratio = 1.14f;

    float level = 1;

    public ExperienceBar(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("GUI/xp frame");
        setSize(
                Gdx.graphics.getWidth() * .5f,
                Gdx.graphics.getHeight() * .1f
        );
        setPosition(x, y - getHeight());
        setOrigin(Align.center);

        progress = new BaseActor(0f, 0f, stage);
        progress.loadImage("GUI/xp bar");
        progress.setSize(1, getHeight() * .71f);
        progress.setPosition(getWidth() * .017f, getHeight() * .15f);
        addActor(progress);

        // setDebug(true);
        // progress.setDebug(true);
    }

    public void animateOneLevel() {
        final float totalDuration = 0.5f;
        final float progressMaxSize = getWidth() - getWidth() * .034f;
        final float bounceAmount = 0.01f;

        progress.addAction(Actions.sizeTo(progressMaxSize, progress.getHeight(), totalDuration));
        progress.addAction(Actions.after(
                Actions.run(() -> addAction(Actions.sequence(

                        Actions.scaleTo(1 + bounceAmount, 1 - bounceAmount, totalDuration / 8),
                        Actions.scaleTo(1.0f, 1.0f, totalDuration / 8)
                )))
        ));
    }
}
