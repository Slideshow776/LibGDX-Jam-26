package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdxjam26.utils.BaseActor;

import java.util.HashMap;

public class Player extends BaseActor {
    public static final float MOVE_SPEED = 18;
    public static final float LUNGE_DISTANCE = 18;
    private static final HashMap<Integer, Float> EXPERIENCE_MAP = new HashMap<>(); // Experience required depending on the level

    static {
        // Pre-load the experience map
        float baseExperience = 200; // starting experience at level 1
        for (int level = 1; level <= 100; level++) { // from level 1 to 100
            EXPERIENCE_MAP.put(level, baseExperience + (100 * (level - 1)));
        }
    }

    public boolean isDead;
    public boolean isMoving;
    public int attackPower = 2;
    public State state = State.IDLE;
    private BaseActor collisionBox;
    private float attackCooldown = 0f;
    private int level = 1;
    private float experience;

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

        if (state != State.LUNGING) {
            if (this.attackCooldown > 0) this.attackCooldown -= delta;
        }
    }

    public void addExperience(float experience) {
        this.experience += experience;
        if (this.experience >= getExperienceForCurrentLevel()) {
            this.experience -= getExperienceForCurrentLevel();
            this.level++;
            if (level >= 100)
                level = 100;
        }
    }

    public float getExperience() {
        return experience;
    }

    public float getExperienceForCurrentLevel() {
        return EXPERIENCE_MAP.get(level);
    }

    public int getLevel() {
        return level;
    }

    public enum State {
        IDLE,
        MOVING,
        LUNGING;
    }

}
