package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdxjam26.utils.BaseActor;

import java.util.function.Consumer;

public class Projectile extends BaseActor {

    private final Texture texture;
    private final Vector2 currentPosition;
    private final Vector2 target;
    private final Consumer<Player> onHit;
    private final Player player;

    public Projectile(Player player, Texture texture, float startX, float startY, float targetX, float targetY, Stage stage, Consumer<Player> onHit) {
        super(startX, startY, stage);
        this.player = player;
        this.currentPosition = new Vector2(startX, startY);
        this.target = new Vector2(targetX, targetY);
        this.onHit = onHit;
        this.texture = texture;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(texture, getX(), getY());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        currentPosition.set(getX(), getY());

        float angle = target.sub(currentPosition).angleDeg();
        setMotionAngle(angle);
        setSpeed(100f);

        this.applyPhysics(delta);
    }
}
