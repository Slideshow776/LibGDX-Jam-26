package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdxjam26.utils.BaseActor;

import java.util.function.Consumer;

public class Projectile extends BaseActor {

    private final Sprite sprite;
    private final Vector2 currentPosition;
    private final Vector2 target;
    private final Consumer<Player> onHit;
    private final Player player;

    public Projectile(Player player, Sprite sprite, float startX, float startY, float targetX, float targetY, Stage stage, Consumer<Player> onHit) {
        super(startX, startY, stage);
        this.player = player;
        this.currentPosition = new Vector2(startX, startY);
        this.target = new Vector2(targetX, targetY);
        this.onHit = onHit;
        this.sprite = sprite;
        sprite.setPosition(getX(), getY());
        sprite.setRotation(getAngle(currentPosition, target));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        sprite.draw(batch);
    }

    private float getAngle(Vector2 start, Vector2 end) {
        return (float) (Math.atan2(end.y - start.y, end.x - start.x) * (180f / Math.PI));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setPosition(getX(), getY());
        sprite.setRotation(getAngle(currentPosition, target));

        float angle = target.cpy().sub(currentPosition).angleDeg();
        setMotionAngle(angle);
        setSpeed(200f);
        this.applyPhysics(delta);
    }
}
