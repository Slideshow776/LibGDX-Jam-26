package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.compression.lzma.Base;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.animation.FixedFrameAnimation;
import no.sandramoen.libgdxjam26.actions.ContrastShader;
import no.sandramoen.libgdxjam26.actions.Shake;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.Projectile;
import no.sandramoen.libgdxjam26.actors.particles.EnemyHitEffect;
import no.sandramoen.libgdxjam26.actors.particles.ParticleActor;
import no.sandramoen.libgdxjam26.utils.AsepriteAnimationLoader;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

/**
 * The `Enemy` class represents an enemy character in a game or simulation. It extends the `BaseActor` class
 * to inherit basic actor functionality such as rendering and position tracking. This class encapsulates various
 * attributes and behaviors of an enemy, including its movement, combat, health, and chat interactions.
 */
public class Enemy extends BaseActor {

    // Attributes
    private final EnemyData data; // Data associated with the enemy
    private final Vector2 playerPosition, enemyPosition; // Position vectors for player and enemy
    private final Group chatGroup; // Group for managing chat labels
    private final Label chatLabel; // Label for displaying chat messages
    private final Label hitLabel; // Label for displaying damage received
    public boolean countDead;
    private int index; // Index for identifying the enemy instance
    private EnemyState state = EnemyState.MOVE; // Current state of the enemy
    private Player following; // Reference to the player character being followed
    private float chatDuration = 1; // Duration for displaying chat messages
    private float chatDelay = 0; // Delay between chat messages
    private float currentHealth; // Current health of the enemy
    private float attackCooldown = 0f;
    private Animation<TextureRegion> walkingAnimation, attackingAnimation, idleAnimation;
    private final TextureAtlas.AtlasRegion projectile;
    private final Vector2 diePosition = new Vector2();
    private float skitterTimer = 0f;

    public float moveSpeed = 1f;

    /**
     * Constructs an `Enemy` instance with the provided data and initial position.
     *
     * @param data  The data associated with the enemy, including its appearance and attributes.
     * @param x     The initial X-coordinate of the enemy.
     * @param y     The initial Y-coordinate of the enemy.
     * @param stage The stage to which the enemy is added for rendering and interaction.
     */
    public Enemy(EnemyData data, float x, float y, Stage stage) {
        // Call the superclass constructor to initialize basic actor properties
        super(x, y, stage);

        moveSpeed = data.moveSpeed;

        loadAnimation(data.getResource());
        setBoundaryRectangle();

        setOrigin(Align.center);
        image.setAlign(Align.center);
        image.setOrigin(Align.center);

        // Initialize enemy-specific attributes
        this.data = data;
        this.currentHealth = data.getBaseHealth();
        this.playerPosition = new Vector2();
        this.enemyPosition = new Vector2();
        this.chatGroup = new Group();
        this.chatLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        this.hitLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), BaseGame.paletteRed));
        this.chatLabel.setAlignment(Align.center);
        this.hitLabel.setAlignment(Align.center);

        this.chatGroup.addActor(chatLabel);
        this.chatGroup.addActor(hitLabel);
        this.chatGroup.setScale(.025f);

        this.projectile = BaseGame.textureAtlas.findRegion("characters/enemyMask/projectile");

        // Add the chat group to the stage for rendering
        this.addActor(chatGroup);

        // Set attack collision box.
        collisionBox = new BaseActor(0, 0, stage);
        collisionBox.setSize(4f, 4f);
        collisionBox.setPosition(
                getWidth() / 2 - collisionBox.getWidth() / 2,
                getHeight() / 2 - collisionBox.getHeight() / 2
        );
        collisionBox.setBoundaryPolygon(8);
//        collisionBox.setDebug(true);
        collisionBox.isCollisionEnabled = false;
        stage.addActor(collisionBox);

        TextureAtlas.AtlasRegion atlasRegion = BaseGame.textureAtlas.findRegion("characters/" + data.getResource() + "/shadow");
        shadow = new Image(new TextureRegionDrawable(atlasRegion));
        shadow.setScale(BaseGame.UNIT_SCALE);
        if (data == EnemyData.ARCHER) {
            shadow.setPosition(0f, -5f);
        }
        else if (data == EnemyData.MELEE)
            shadow.setVisible(false);
        addActor(shadow);
    }

    private void loadAnimation(String enemyName) {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();
        walkingAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/" + enemyName + "/walking");
        walkingAnimation.setPlayMode(Animation.PlayMode.LOOP);

        attackingAnimation = AsepriteAnimationLoader.load("assets/images/included/characters/" + enemyName + "/attacking");

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/idle1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/idle2"));
        idleAnimation = new FixedFrameAnimation<>(.6f, animationImages, Animation.PlayMode.LOOP);

        setAnimation(walkingAnimation);
    }

    /**
     * Called on every game frame to update the state and behavior of the enemy.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (state == EnemyState.CORPSE) {
            setPosition(BaseGame.levelScreen.background.getX() + diePosition.x, BaseGame.levelScreen.background.getY() + diePosition.y);
        }

        if (isDead()) {
            return;
        }

        // Update chat delay and duration
        chatDelay += delta;
        if (chatDuration > 0) chatDuration -= delta;

        if (state == EnemyState.DETECT_DAMAGE) {

            if (following.isDamageable()) {
                // Apply damage to the player as soon as the player is hit,
                // then disable hit detection.
                if (collisionBox.overlaps(following.collisionBox)) {
                    state = EnemyState.ATTACK;
                    following.applyDamage(data.attackDamage);
                    following.applyKnockBack(this);
                    collisionBox.isCollisionEnabled = false;
                }
            }
            return;
        }

        if (state == EnemyState.ATTACK) return;

        if (state == EnemyState.IDLE) {
            attackCooldown -= delta;
            if (attackCooldown <= 0) {
                state = EnemyState.MOVE;
                setAnimation(walkingAnimation);
            }
            return;
        }

        if (data == EnemyData.MELEE) {
            skitterTimer -= delta;
            if (skitterTimer < 0f) {
                state = EnemyState.ATTACK;
                skitterTimer = BaseGame.random.nextInt(8) + 4;
                moveSpeed = 12f;
                animationSpeed = 2f;
                SequenceAction sequenceAction = Actions.sequence(
                    Actions.delay(1f),
                    Actions.run(() -> {
                        moveSpeed = 6f;
                        animationSpeed = 1f;
                    })
                );
                addAction(sequenceAction);
            }
        }

        // Handle enemy movement and attack behaviors when following a player
        if (following != null) {
            playerPosition.set(following.getX(Align.center), following.getY(Align.center));
            enemyPosition.set(this.getX(Align.center), this.getY(Align.center));

            float angleDegrees = playerPosition.cpy().sub(enemyPosition).angleDeg();
            checkIfFlip(angleDegrees);

            // Check if the player is out of attack range, and if so, move towards the player
            if (playerPosition.dst(enemyPosition) > data.getAttackRange()) {
                setMotionAngle(angleDegrees);
                setSpeed(moveSpeed);
                state = EnemyState.MOVE;
            } else if (following.isDamageable()){

                // Player is in attack range, stop moving and enter attack state
                state = EnemyState.ATTACK;
                setMotionAngle(0);
                setSpeed(0);

                setAnimation(attackingAnimation);
                animationTime = 0f;

                if (data == EnemyData.ARCHER || data == EnemyData.MAGE) {
                    float x1 = getX(Align.center);
                    float y1 = getY(Align.center);
                    float x2 = following.getX(Align.center);
                    float y2 = following.getY(Align.center);
                    SequenceAction sequence = Actions.sequence(
                        Actions.delay(1.5f),
                        Actions.run(() -> {
                            Projectile projectile = new Projectile(following, this, this.projectile, x1, y1, x2, y2, getStage());
                            getStage().addActor(projectile);
                        }),
                        Actions.delay(.5f),
                        Actions.run(() -> {
                            if (isDead())
                                return;
                            state = EnemyState.IDLE;
                            attackCooldown = 1.5f;
                            setAnimation(idleAnimation);
                        })
                    );
                    addAction(sequence);
                } else {
                    // Create damage shape in front.
                    // Set debug = true.
                    getActions().clear();

                    Vector2 lungeVector = playerPosition.sub(enemyPosition).nor().scl(data.lungeDistance);
                    Vector2 finalPosition = enemyPosition.add(lungeVector);

                    MoveToAction moveAction = Actions.moveToAligned(finalPosition.x, finalPosition.y, Align.center,0.5f, Interpolation.exp10);
                    SequenceAction sequence = Actions.sequence(
                            moveAction,
                            Actions.delay(0.1f),
                            Actions.run(() -> {
                                state = EnemyState.IDLE;
                                collisionBox.isCollisionEnabled = false;
                                attackCooldown = 1.5f;
                                setAnimation(idleAnimation);
                            })
                    );
                    // Spawn the hitbox in front the enemy after a short delay
                    ParallelAction parallelAction = Actions.parallel(
                            sequence,
                            Actions.sequence(
                                    Actions.delay(0.2f),
                                    Actions.run(() -> {
                                        state = EnemyState.DETECT_DAMAGE;
                                        collisionBox.isCollisionEnabled = true;
                                    })
                            )
                    );
                    addAction(parallelAction);
                }
            }
        } else {
            setMotionAngle(0);
            setSpeed(0);
        }

        // Update chat messages
        if (chatDelay >= 3) {
            chatDelay = 0;
            chatDuration = 2f;

            // Randomly select and display a chat message
            int randomIndex = (int) (Math.random() * EnemyData.CHAT_MESSAGES.length);
            chatLabel.setText(EnemyData.CHAT_MESSAGES[randomIndex]);
            this.chatGroup.setPosition(getWidth() / 2, getHeight() - 1f);
        }
        // Hide chat message when its duration expires
        if (chatDuration <= 0) {
            chatDuration = 0;
            chatLabel.setText("");
        }

        // Apply physics and continue actor processing
        this.applyPhysics(delta);
    }

    private void attackPlayer(Player following) {

    }

    /**
     * Draws the enemy on the stage with optional parent alpha blending.
     *
     * @param batch       The batch used for rendering.
     * @param parentAlpha The alpha value of the parent actor, if applicable.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    /**
     * Initiates following of a player character by the enemy.
     *
     * @param player The player character to be followed.
     */
    public void beginFollowingPlayer(Player player) {
        this.following = player;
    }

    public boolean isDead() {
        return state == EnemyState.DEAD || state == EnemyState.CORPSE;
    }

    /**
     * Inflicts damage on the enemy and updates chat and health-related attributes.
     *
     * @param damage The amount of damage to be inflicted.
     */
    public void hit(float damage) {
        // Reset chat delay and duration
        this.chatDelay = 0;
        this.chatDuration = 0;

        // Decrease current health and ensure it doesn't go below zero
        this.currentHealth = Math.max(0, this.currentHealth - damage);

        // Create an action to display the damage received
        MoveToAction moveAction = new MoveToAction();
        moveAction.setDuration(0.25f);
        moveAction.setPosition(getX(Align.center), 45 + 15);

        ParallelAction parallelAction = new ParallelAction();
        parallelAction.setActor(hitLabel);
        parallelAction.addAction(Actions.alpha(1));
        parallelAction.addAction(Actions.fadeOut(0.25f));
        parallelAction.addAction(moveAction);

        // Check if the enemy has been defeated
        if (this.currentHealth <= 0) {
            countDead = true;
            state = EnemyState.DEAD;
            ParticleActor particleActor = new ParticleActor("effects/EnemyDie2.pfx");
            SequenceAction sequenceAction = Actions.sequence(
                    Actions.parallel(
                            Actions.color(new Color(0.3882353f, 0.1254902f, 0.2627451f, 1f), .4f, Interpolation.exp10),
                            new ContrastShader(.4f, Interpolation.exp10)
                    ),
                    Actions.delay(.05f),
                    Actions.fadeOut(.2f),
                    Actions.run(() -> {
                        setColor(0f, 0f, 0f, 0f);
                        shadow.remove();
                    }),
                    Actions.delay(.15f),
                    Actions.parallel(
                            Actions.fadeIn(.15f),
                            Actions.color(Color.WHITE, .15f),
                            Actions.run(() -> {
                                particleActor.remove();
                                shaderProgram = null;
                                loadImage("characters/" + data.getResource() + "/dead");
                                state = EnemyState.CORPSE;
                            })
                    ),
                    Actions.delay(10f),
                    Actions.fadeOut(.2f),
                    Actions.run(this::remove)
            );
            // Set animation to final frame of attack animation.
            setAnimation(attackingAnimation);
            animationTime = 10f;

            // Prevent messages from popping up anymore.
            chatLabel.remove();

            addAction(sequenceAction);
            Shake shake = new Shake(1f);
            shake.shakeDuration = 4f / 60f;
            shake.shakeOffset = 2f;
            addAction(shake);
            particleActor.setPosition(+getWidth() / 2, getHeight() / 3);
            particleActor.setScale(BaseGame.UNIT_SCALE / 4f);
            particleActor.deltaScale = 3f / 4f;
            particleActor.start();
            addActor(particleActor);

            diePosition.set(getX(), getY());
            if (data == EnemyData.ARCHER)
                diePosition.y -= 4f;
        }


        // Clear existing actions on the hit label and update its position and text
        this.hitLabel.getActions().clear();
        this.hitLabel.setPosition(getX(Align.center), 45);
        this.hitLabel.setText("" + (int) damage);
        this.hitLabel.addAction(parallelAction);

        // Apply camera shake.
        shakeCamera();

        EnemyHitEffect enemyHitEffect = new EnemyHitEffect();
        enemyHitEffect.setPosition(+getWidth() / 2 - BaseGame.UNIT_SCALE * 18, getHeight() / 2 - BaseGame.UNIT_SCALE * 4);
        enemyHitEffect.setScale(4f * BaseGame.UNIT_SCALE / 15f);
        enemyHitEffect.deltaScale = 3f / 4f;
        enemyHitEffect.start();
        addActor(enemyHitEffect);
    }

    // Getter and setter methods for various attributes

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EnemyState getState() {
        return state;
    }

    public EnemyData getData() {
        return data;
    }
}
