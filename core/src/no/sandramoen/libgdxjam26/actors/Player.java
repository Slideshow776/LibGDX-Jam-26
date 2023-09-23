package no.sandramoen.libgdxjam26.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.animation.FixedFrameAnimation;
import no.sandramoen.libgdxjam26.actions.Shake;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.actors.particles.EnemyHitEffect;
import no.sandramoen.libgdxjam26.screens.shell.LevelUpScreen;
import no.sandramoen.libgdxjam26.ui.PlayerLabelGroup;
import no.sandramoen.libgdxjam26.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends BaseActor {
    public static final float MOVE_SPEED = 18;
    public static final float LUNGE_DISTANCE = 18;
    public static final float CHARGEATTACK_DISTANCE = 26;
    public static final float DASH_DISTANCE = 22;
    public static final float SHOCKWAVE_DISTANCE = 10;
    private static final HashMap<Integer, Float> EXPERIENCE_MAP = new HashMap<>(); // Experience required depending on the level
    private final BaseActor collisionBox;
    private final int MAX_LEVEL = 100;
    public boolean isDead;
    public int attackPower = 2;
    public State state = State.IDLE;
    public Animation<TextureRegion> walkingAnimation, attackingAnimation, idleAnimation;
    private Vector2 source = new Vector2(), target = new Vector2();
    private float attackCooldown = 0f;
    private int level = -1;
    private int startingLevel = -1;
    private float experience;
    private int health = 4;
    private PlayerLabelGroup playerLabelGroup;

    {
        // Pre-load the experience map
        float baseExperience = 200; // starting experience at level 1
        for (int level = this.level; level <= 100; level++) { // from level 1 to 100
            EXPERIENCE_MAP.put(level, baseExperience + (100 * (level - 1)));
        }
    }

    public LoopedSound chargeSound;
    {
        chargeSound = new LoopedSound(Gdx.files.internal("assets/audio/sound/player/charge1-start.ogg"), Gdx.files.internal("assets/audio/sound/player/charge1-loop.ogg"));
    };

    public Player(float x, float y, int startingLevel, float percentToNextLevel, Stage stage) {
        super(x, y, stage);

        this.level = startingLevel;
        this.startingLevel = level;
        this.experience = MathUtils.ceil(getExperienceForNextLevel() * percentToNextLevel);

        loadAnimation();
        setBoundaryRectangle();

        collisionBox = new BaseActor(0, 0, stage);
        collisionBox.setSize(2, 4f);
        collisionBox.setPosition(
                (getWidth() - collisionBox.getWidth()) / 2,
                (getHeight() - collisionBox.getHeight()) / 4
        );
        collisionBox.setBoundaryRectangle();
        addActor(collisionBox);

        playerLabelGroup = new PlayerLabelGroup();
        playerLabelGroup.setPosition(getWidth() / 2 - 2.5f, getHeight() - 1.5f);
        addActor(playerLabelGroup);
    }

    public boolean isDamageable() {
        return state == State.MOVING || state == State.IDLE || state == State.SHOCKWAVE_CHARGE || state == State.CHARGEATTACK_CHARGE|| state == State.LUNGING;
    }

    public int getHealth() {
        return health;
    }

    public BaseActor getCollisionBox() {
        return collisionBox;
    }

    public void applyKnockBack(Enemy enemy) {

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
        int randomIndex = numberList.get(BaseGame.random.nextInt(numberList.size()));
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

        Array<Integer> abilityUnlocks = new Array<>();
        abilityUnlocks.add(20, 40, 60);

        float xpForNextLevel = getExperienceForNextLevel();
        float currentXp = experience;
        float percent = ((currentXp * 100) / xpForNextLevel) / 100;

        BaseGame.setActiveScreen(new LevelUpScreen(startingLevel, level - startingLevel, percent, abilityUnlocks));
    }

    private void loadAnimation() {

        walkingAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/player/walking");
        walkingAnimation.setPlayMode(Animation.PlayMode.LOOP);

        attackingAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/player/attack");

        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/idle1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/player/idle2"));
        idleAnimation = new FixedFrameAnimation<>(.6f, animationImages, Animation.PlayMode.LOOP);
        setAnimation(idleAnimation);
    }

    private void handleMovement(float delta) {

        if (state != State.MOVING)
            BaseGame.swordDrag1Sound.stop();

        if (state != State.IDLE && state != State.MOVING)
            return;

        // Update attackCooldown.
        if (this.attackCooldown > 0) this.attackCooldown -= delta;

        // Set mouse and player position for use in calculations.
        source.set(getX(Align.center), getY(Align.center));
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        target.set(mouseX, mouseY);
        getStage().screenToStageCoordinates(target);

        if (target.dst2(source) > 5) {
            // Move player towards cursor.
            if (state != State.MOVING) {
                long soundId = BaseGame.swordDrag1Sound.play();
                BaseGame.swordDrag1Sound.setLooping(soundId, true);
                setAnimation(walkingAnimation);
                state = State.MOVING;
            }
            float angleDeg = target.sub(source).angleDeg();
            angleDeg = Math.floorMod((int) angleDeg, 360);
            setMotionAngle(angleDeg);
            setSpeed(Player.MOVE_SPEED);
            checkIfFlip(angleDeg);
        } else {
            if (state == State.MOVING) {
                BaseGame.swordDrag1Sound.stop();
                setAnimation(idleAnimation);
                state = State.IDLE;
            }
            setMotionAngle(0f);
            setSpeed(0);
        }
        applyPhysics(delta);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        handleMovement(delta);
    }

    public void addExperience(float experience) {
        this.experience += experience;
        if (this.experience >= getExperienceForCurrentLevel()) {
            if (level < MAX_LEVEL) {
                this.experience -= getExperienceForCurrentLevel();
                this.level += 1;

                playerLabelGroup.showLabelAndAnimate();

                GameUtils.playWithRandomPitch(BaseGame.levelUpSound, .99f, 1.01f);
            }
        }
    }

    public float getExperience() {
        return experience;
    }

    public float getExperienceForCurrentLevel() {
        return EXPERIENCE_MAP.get(level);
    }

    private float getExperienceForNextLevel() {
        if (level < MAX_LEVEL)
            return EXPERIENCE_MAP.get(level + 1);
        else return 0;
    }

    public int getLevel() {
        return level;
    }

    public enum State {
        IDLE,
        MOVING,
        LUNGING,
        DASHING,
        KNOCKED_BACK,
        SHOCKWAVE_CHARGE,
        SHOCKWAVE_DO,
        CHARGEATTACK_CHARGE,
        ;
    }

}
