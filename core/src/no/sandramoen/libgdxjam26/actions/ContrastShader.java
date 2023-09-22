package no.sandramoen.libgdxjam26.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class ContrastShader extends TemporalAction {
    BaseActor baseActor;

    public ContrastShader(float duration, Interpolation interpolation) {
        super(duration, interpolation);
    }
    @Override
    protected void begin () {
        baseActor = (BaseActor) target;
        baseActor.shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.contrastShader);
    }
    @Override
//    protected void end () {
//        baseActor.shaderProgram = null;
//    }
    protected void update (float percent) {
        if (baseActor.shaderProgram == null)
            return;

        baseActor.shaderProgram.bind();
        baseActor.shaderProgram.setUniformf("u_contrast", 1f - percent);
    }
}
