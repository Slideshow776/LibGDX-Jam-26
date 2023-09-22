package no.sandramoen.libgdxjam26.actors.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import no.sandramoen.libgdxjam26.utils.BaseGame;

public class ParticleActor extends Group {

    public float accumulate = 0f;
    private float accumulateTimer = 0f;

    public float deltaScale = 1f;
    private ParticleEffect effect;
    private ParticleRenderer renderingActor;

    private class ParticleRenderer extends Actor {
        private ParticleEffect effect;

        ParticleRenderer(ParticleEffect e) {
            effect = e;
        }

        public void draw(Batch batch, float parentAlpha) {
//            for (ParticleEmitter emitter : effect.getEmitters()) {
//                emitter.getActiveCount();
//            }
            effect.draw(batch);
        }
    }

    public ParticleActor(String pfxFile) {
        super();
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(pfxFile), BaseGame.textureAtlas);
        renderingActor = new ParticleRenderer(effect);
        this.addActor(renderingActor);
    }

    public void start() {
        effect.start();
    }

    // pauses continuous emitters
    public void stop() {
        effect.allowCompletion();
    }

    public boolean isRunning() {
        return !effect.isComplete();
    }

    public void centerAtActor(Actor other) {
        setPosition(
                other.getX() + other.getWidth() / 2,
                other.getY() + other.getHeight() / 2
        );
    }

    public void act(float dt) {
        accumulateTimer += dt;
        if (accumulateTimer < accumulate) return;
        dt += accumulate;
        accumulateTimer -= accumulate;

        super.act(dt);
        effect.update(dt * deltaScale);

        if (effect.isComplete() && !effect.getEmitters().first().isContinuous()) {
            effect.dispose();
            this.remove();
            renderingActor.remove();
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}