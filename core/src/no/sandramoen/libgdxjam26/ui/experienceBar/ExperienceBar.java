package no.sandramoen.libgdxjam26.ui.experienceBar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class ExperienceBar extends BaseActor {
    public ProgressComponent progress;

    private final float progressOffset = .017f;
    private final float bounceDuration = 0.125f;
    private final float animationDuration = .125f;

    public ExperienceBar(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("GUI/xp frame");
        setSize(
                Gdx.graphics.getWidth() * .5f,
                Gdx.graphics.getHeight() * .1f
        );
        setOrigin(Align.center);

        progress = new ProgressComponent(getWidth() * progressOffset, getHeight(), stage);
        addActor(progress);
    }

    public void animateToPercent(float percent) {
        if (percent < 0f || percent > 1f)
            Gdx.app.error(getClass().getSimpleName(), "Error: percent must be [0f, 1f]");

        progress.addAction(Actions.after(Actions.sequence(
                resetAction(),
                progressAction(percent),
                Actions.run(() -> GameUtils.playWithRandomPitch(BaseGame.levelUpSound, .99f, 1.01f)),
                bounceAction()
        )));
    }

    private SequenceAction resetAction() {
        return Actions.sequence(
                Actions.sizeTo(1f, progress.getHeight(), 0f),
                Actions.moveTo(getWidth() * progressOffset, progress.getY(), 0)
        );
    }

    private float getPercentOffset(float percent) {
        return getWidth() * progressOffset * (1 - percent);
    }

    private ParallelAction progressAction(float percent) {
        return Actions.parallel(
                Actions.sizeTo(getWidth() * percent, progress.getHeight(), animationDuration),
                Actions.moveTo(getPercentOffset(percent), 0f, animationDuration)
        );
    }

    private RunnableAction bounceAction() {
        final float bounceAmount = 0.01f;
        return Actions.run(() -> addAction(Actions.sequence(
                Actions.scaleTo(1 + bounceAmount, 1 - bounceAmount, bounceDuration / 2),
                Actions.scaleTo(1.0f, 1.0f, bounceDuration / 2)
        )));
    }
}
