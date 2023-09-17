package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdxjam26.utils.BaseActor;

public class Player extends BaseActor {
    public static final float MOVE_SPEED = 25;
    public boolean isDead;
    public boolean isMoving;
    public int attackPower = 2;
    public State state = State.IDLE;
    private BaseActor collisionBox;
    private float attackCooldown = 0f;

    public Player(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("characters/player/idle1");
        setBoundaryRectangle();

        collisionBox = new BaseActor(0, 0, stage);
        collisionBox.setSize(1 / 2f, 1 / 2f);
        collisionBox.setPosition(
                getWidth() / 2 - collisionBox.getWidth() / 2,
                getHeight() / 3 - collisionBox.getHeight() / 2
        );
        collisionBox.setBoundaryRectangle();
        // collisionBox.setDebug(true);
        addActor(collisionBox);
    }

    public BaseActor getCollisionBox() {
        collisionBox.setPosition(
                getX() + getWidth() / 2 - collisionBox.getWidth() / 2,
                getY() + getHeight() / 2 - collisionBox.getHeight() / 2
        );
        return collisionBox;
    }

    public void die() {
        isDead = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (state != State.ATTACKING) {
            if (this.attackCooldown > 0) this.attackCooldown -= delta;
        }
    }

    public enum State {
        IDLE,
        MOVING,
        ATTACKING,
        ;
    }

}
