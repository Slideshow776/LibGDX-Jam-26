package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class ShockwaveBackground extends BaseActor {
    public ShaderProgram shaderProgram;
    private float totalTime;
    private float animationDelay = 1f;
    private float shockWavePositionX = -5f;
    private float shockWavePositionY = -5f;

    public ShockwaveBackground(String texturePath, Stage stage) {
        super(0, 0, stage);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        init();
    }

    public void init() {
        shaderProgram = GameUtils.initShaderProgram(BaseGame.defaultShader, BaseGame.shockwaveShader);
        addListener(
            (Event event) -> {
                if (GameUtils.isTouchDownEvent(event)) {
                    float xNormalized = Gdx.input.getX() / (float) Gdx.graphics.getWidth();
                    float yNormalized = Gdx.input.getY() / (float) Gdx.graphics.getHeight();
                    start(xNormalized, yNormalized);
                }
                return false;
            }
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (BaseGame.isCustomShadersEnabled) {
            try {
                drawWithShader(batch, parentAlpha);
            } catch (Throwable throwable) {
                super.draw(batch, parentAlpha);
            }
        } else {
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;
    }

    private void drawWithShader(Batch batch, float parentAlpha) {
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf("u_time", totalTime);
        shaderProgram.setUniformf("u_center", new Vector2(shockWavePositionX, shockWavePositionY));
        shaderProgram.setUniformf("u_shockParams", new Vector3(10f, .8f, .1f));
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    private void start(float normalizedPosX, float normalizedPosY) {
        if (totalTime >= animationDelay) { // prevents interrupting previous animation
            this.shockWavePositionX = normalizedPosX;
            this.shockWavePositionY = normalizedPosY;
            totalTime = 0;
        }
    }
}
