package no.sandramoen.libgdxjam26.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class ShockwaveShader extends TemporalAction {
    BaseActor baseActor;
    Vector2 center;
    float textureWidth; // Set this to the width of your texture
    float textureHeight; // Set this to the height of your texture

    public ShockwaveShader(Vector2 center, float duration, Interpolation interpolation) {
        super(duration, interpolation);
        this.center = center;
    }


    @Override
    protected void begin () {
        baseActor = (BaseActor) target;
        baseActor.shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.shockwaveShader);
        center.sub(10f, 10f);
        float textureX = center.x / baseActor.getWidth();
        float textureY = center.y / baseActor.getHeight();

        this.center = new Vector2(textureX, 1f - textureY);
    }
    @Override
    protected void end () {
        baseActor.shaderProgram = null;
    }
    protected void update (float percent) {
        if (baseActor.shaderProgram == null)
            return;

        baseActor.shaderProgram.bind();
        baseActor.shaderProgram.setUniformf("u_time", percent);
        baseActor.shaderProgram.setUniformf("u_center", center);
        baseActor.shaderProgram.setUniformf("u_shockParams", new Vector3(10f, .8f, .1f));
    }
}
