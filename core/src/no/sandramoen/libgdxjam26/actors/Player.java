package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.Gdx;
import io.github.fourlastor.harlequin.animation.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import io.github.fourlastor.harlequin.animation.FixedFrameAnimation;
import no.sandramoen.libgdxjam26.actions.Shake;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.actors.particles.EnemyHitEffect;
import no.sandramoen.libgdxjam26.utils.AsepriteAnimationLoader;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private final BaseActor collisionBox;
    private float attackCooldown = 0f;
    private int level = 1;
    private float experience;
    private int health = 20;
    public int getHealth() { return health; }

    public Animation<TextureRegion> walkingAnimation, attackingAnimation, idleAnimation;

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
//        collisionBox.setPosition(
//                getX(Align.center) + getWidth() / 2 - collisionBox.getWidth() / 2,
//                getY(Align.center) + getHeight() / 2 - collisionBox.getHeight() / 2
//        );
        return collisionBox;
    }

    public void applyKnockBack(Enemy enemy) {
        getActions().clear();

        shakeCamera(4f);
//        CenterCamera.MOVE_DURATION = .125f;
//        CenterCamera.INTERPOLATION = Interpolation.slowFast;

        // Get normalized Vector between player and mouse.
        target.set(getX(Align.center), getY(Align.center));
        source.set(enemy.getX(Align.center), enemy.getY(Align.center));
        Vector2 lungeVector = target.sub(source).nor().scl(10);
        Vector2 finalPosition = source.add(lungeVector);

        MoveToAction moveAction = Actions.moveTo(finalPosition.x, finalPosition.y, 0.2f, Interpolation.exp10Out);
        moveAction.setAlignment(Align.center);
        SequenceAction sequence = Actions.sequence(
                moveAction,
                Actions.delay(0.3f),
                Actions.run(() -> state = State.IDLE)
        );
        addAction(sequence);

        sequence = Actions.sequence(
                Actions.moveBy(0, BaseGame.UNIT_SCALE * 4f),
                Actions.delay(0.1f),
                Actions.moveBy(0, -BaseGame.UNIT_SCALE * 4f)
        );
        addAction(sequence);

        addAction(new Shake(.2f));

        // Hit particle effect.
        EnemyHitEffect hitEffect = new EnemyHitEffect();
        hitEffect.setPosition(+getWidth() / 2 - BaseGame.UNIT_SCALE * 16, getHeight() / 3);
        hitEffect.setScale(BaseGame.UNIT_SCALE / 4f);
        hitEffect.deltaScale = 1f / 3f;
        hitEffect.start();
        addActor(hitEffect);


        state = State.KNOCKED_BACK;
    }

    public void applyDamage(int amount) {

        // Play a random 'hit' noise.
        List<Integer> numberList = new ArrayList<>();
        for (int i = 0; i < BaseGame.hitSounds.size(); ++i) {
            numberList.add(i);
        }
        // Don't play the previous hit noise.
        numberList.remove(BaseGame.hitSoundsPreviousIndex);
        int randomIndex =  numberList.get(BaseGame.random.nextInt(numberList.size()));
        BaseGame.hitSoundsPreviousIndex = randomIndex;
        GameUtils.playWithRandomPitch(BaseGame.hitSounds.get(randomIndex), .8f, .9f);

        health -= amount;

        if (BaseGame.levelScreen != null) {
            BaseGame.levelScreen.hearts.decreaseHealth(amount);
        }

        if (health <= 0)
            die();
    }

    public void die() {
        isDead = true;
    }

    private void loadAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();

        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/walking1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/walking2"));
        walkingAnimation = new FixedFrameAnimation<>(.2f, animationImages, Animation.PlayMode.LOOP);

        animationImages.clear();
        attackingAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/player/attack");

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/idle1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/idle2"));
        idleAnimation = new FixedFrameAnimation<>(.6f, animationImages, Animation.PlayMode.LOOP);

        setAnimation(idleAnimation);
    }

    private void handleMovement(float delta) {
        if (state != State.LUNGING && state != State.KNOCKED_BACK) {

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
                if (!isMoving) {
                    setAnimation(walkingAnimation);
                    isMoving = true;
                }
                float angleDeg = target.sub(source).angleDeg();
                angleDeg = Math.floorMod((int)angleDeg, 360);
                setMotionAngle(angleDeg);
                setSpeed(Player.MOVE_SPEED);
                checkIfFlip(angleDeg);
            } else {
                if (isMoving) {
                    setAnimation(idleAnimation);
                    isMoving = false;
                }
                setMotionAngle(0f);
                setSpeed(0);
            }
            applyPhysics(delta);
        }
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
            this.level += 1;
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
        LUNGING,
        KNOCKED_BACK;
    }

}
