package no.sandramoen.libgdxjam26.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.utils.BaseGame;

import java.util.List;

public class Shake extends Action {

    float currDir = BaseGame.UNIT_SCALE;
    float counter;
    float duration;

    public float shakeDuration = 2f / 60f;
    public float shakeOffset = 3f;

    public Shake(float duration) {
        this.duration = duration;
    }

    @Override
    public boolean act(float delta) {

        counter += delta;
        if (counter > shakeDuration) {
            counter = 0;
            getActor().addAction(Actions.moveBy(currDir * shakeOffset, 0));
            currDir = -currDir;
        }

        duration -= delta;
        return duration < 0;
    }
}
