package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

import java.util.HashMap;

public class Player extends BaseActor {
    public static final float MOVE_SPEED = 18;
    public static final float LUNGE_DISTANCE = 18;
    private static final HashMap<Integer, Float> EXPERIENCE_MAP = new HashMap<>(); // Experience required depending on the level

    private Vector2 source = new Vector2(), target = new Vector2();

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
        loadAnimation();
        setBoundaryRectangle();

        collisionBox = new BaseActor(0, 0, stage);
        collisionBox.setSize(1 / 2f, 1 / 2f);
        collisionBox.setPosition(
                getWidth() / 2 - collisionBox.getWidth() / 2,
                getHeight() / 3 - collisionBox.getHeight() / 2
        );
        collisionBox.setBoundaryRectangle();
        collisionBox.setDebug(true);
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

    private void loadAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/walking1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/walking2"));
        animation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        setAnimation(animation);
    }

    private void handleMovement(float delta) {
        if (state != Player.State.LUNGING) {

            // Update attackCooldown.
            if (this.attackCooldown > 0) this.attackCooldown -= delta;

            // Set mouse and player position for use in calculations.
            source.set(getX(Align.center), getY(Align.center));
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            target.set(mouseX, mouseY);
            getStage().screenToStageCoordinates(target);

            if (target.dst2(source) > 1e-1) {
                // Move player towards cursor.
                isMoving = true;
                float angleDeg = target.sub(source).angleDeg();
                setMotionAngle(angleDeg);
                setSpeed(Player.MOVE_SPEED);

                System.out.println(angleDeg + ", " + isFacingRight);

                checkIfFlip(angleDeg);

            } else {
                isMoving = false;
                setMotionAngle(0f);
                setSpeed(0);
            }
            applyPhysics(delta);
        }
    }

    private void checkIfFlip(float angleDeg) {
        if (
                !isFacingRight &&
                        (angleDeg >= 270 && angleDeg <= 360) || (angleDeg >= 0 && angleDeg <= 90)
        )
            flip();
        else if (
                isFacingRight &&
                        (angleDeg > 90 && angleDeg < 270)
        )
            flip();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        handleMovement(delta);
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
