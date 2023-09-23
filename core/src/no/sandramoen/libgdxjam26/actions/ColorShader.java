package no.sandramoen.libgdxjam26.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class ColorShader extends TemporalAction {
    BaseActor baseActor;
    Color color;

    public ColorShader(Color color, float duration, Interpolation interpolation) {
        super(duration, interpolation);
        this.color = color;
    }
    @Override
    protected void begin () {
        baseActor = (BaseActor) target;
        baseActor.shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.colorShader);
    }
    @Override
    protected void end () {
        baseActor.shaderProgram = null;
    }
    protected void update (float percent) {
        if (baseActor.shaderProgram == null)
            return;

        baseActor.shaderProgram.bind();
        baseActor.shaderProgram.setUniformf("u_percent", 1f - percent);
        baseActor.shaderProgram.setUniformf("u_color", color.r, color.g, color.b, color.a);
    }
}
