package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.harlequin.animation.Animation;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.utils.AsepriteAnimationLoader;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class Projectile extends BaseActor {

    private final Vector2 startPosition;
    private final Vector2 currentPosition = new Vector2();
    private final Vector2 target = new Vector2();
    float previousAngle = -4000f;
    private final Player player;
    private final Enemy owner;
    private float duration = 2f;
    Animation<TextureRegion> projectileAnimation;

    public Projectile(Player player, Enemy owner, TextureRegion textureRegion, float startX, float startY, float targetX, float targetY, Stage stage) {
        super(startX, startY, stage);
        this.owner = owner;
        this.player = player;
        this.startPosition = new Vector2(startX, startY);
        projectileAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/enemyMask/projectile");
        projectileAnimation.setPlayMode(Animation.PlayMode.LOOP);
        setAnimation(projectileAnimation);
        setOrigin(Align.center);
        setBoundaryPolygon(6);
        setSpeed(15f);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    private float getAngle(Vector2 start, Vector2 end) {
        return (float) (Math.atan2(end.y - start.y, end.x - start.x) * (180f / Math.PI));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        duration -= delta;
        if (duration < 0) {
            remove();
            return;
        }

        if (owner.isDead()){
            remove();
            return;
        }

//        setRotation(getAngle(startPosition, target));

        currentPosition.set(getX(), getY());
        target.set(player.collisionBox.getX(Align.center), player.collisionBox.getY(Align.center) - 2f);
        float angle = target.cpy().sub(currentPosition).angleDeg();

        // Not working.
//        if (angle - previousAngle > 350) {
//            previousAngle += 360;
//        }
//        else if (previousAngle - angle > 350) {
//            previousAngle -= 360;
//        }

        if (previousAngle == -4000f)
            previousAngle = angle;
        else
            angle = angle * 0.2f + previousAngle * 0.8f;

        setMotionAngle(angle);
        applyPhysics(delta);

        if (player.isDamageable() && overlaps(player.collisionBox.getBoundaryPolygon())) {
            remove();
            player.applyDamage(1);
            player.applyKnockBack(this);
        }

        if (startPosition.dst(getX(), getY()) > 1000) { // it has traveled off-screen or something
            remove();
        }
    }
}
