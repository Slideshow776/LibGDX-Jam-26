package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdxjam26.utils.BaseActor;

public class Projectile extends BaseActor {

    private final Sprite sprite;
    private final Vector2 startPosition;
    private final Vector2 target;
    private final Player player;

    public Projectile(Player player, Sprite sprite, float startX, float startY, float targetX, float targetY, Stage stage) {
        super(startX, startY, stage);
        this.player = player;
        this.startPosition = new Vector2(startX, startY);
        this.target = new Vector2(targetX, targetY);
        this.sprite = sprite;

        sprite.setOriginCenter();
        setSize(sprite.getWidth(), sprite.getHeight());
        setOrigin(Align.center);
        setBoundaryRectangle();
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
        sprite.setRotation(getAngle(startPosition, target));

        float angle = target.cpy().sub(startPosition).angleDeg();
        setMotionAngle(angle);
        setSpeed(300f);
        applyPhysics(delta);

        BaseActor a = player.getCollisionBox();
        Rectangle r2 = new Rectangle(player.getX() + a.getX(), player.getY() + a.getY(), a.getWidth(), a.getHeight());
        Rectangle r4 = new Rectangle(getX(), getY(), getWidth(), getHeight());

        if (r4.overlaps(r2)) {
            remove();
            player.applyDamage(1);
        }

        if (startPosition.dst(getX(), getY()) > 1000) { // it has traveled off-screen or something
            remove();
        }
    }
}
