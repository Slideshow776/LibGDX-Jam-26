package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.particles.EnemyHitEffect;
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
    private int index; // Index for identifying the enemy instance
    private EnemyState state = EnemyState.MOVE; // Current state of the enemy
    private Player following; // Reference to the player character being followed
    private float chatDuration = 1; // Duration for displaying chat messages
    private float chatDelay = 0; // Delay between chat messages
    private float currentHealth; // Current health of the enemy
    private final BaseActor attackCollisionBox;
    private float attackCooldown = 0f;

    private Animation<TextureRegion> walkingAnimation, attackingAnimation, idleAnimation;

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

        loadAnimation(data.getResource());
        setBoundaryRectangle();

        // Initialize enemy-specific attributes
        this.data = data;
        this.currentHealth = data.getBaseHealth();
        this.playerPosition = new Vector2();
        this.enemyPosition = new Vector2();
        this.chatGroup = new Group();
        this.chatLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        this.hitLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), Color.RED));
        this.chatLabel.setAlignment(Align.center);
        this.hitLabel.setAlignment(Align.center);
        this.chatGroup.setScale(BaseGame.UNIT_SCALE);
        this.chatGroup.addActor(chatLabel);
        this.chatGroup.addActor(hitLabel);

        // Add the chat group to the stage for rendering
        this.addActor(chatGroup);

        // Set attack collision box.
        // TODO: should depend on enemy.data rather than being universal for all enemy types.
        attackCollisionBox = new BaseActor(0, 0, stage);
        attackCollisionBox.setSize(12f, 12f);
        attackCollisionBox.setPosition(
                getWidth() / 2 - attackCollisionBox.getWidth() / 2,
                getHeight() / 2 - attackCollisionBox.getHeight() / 2
        );
        attackCollisionBox.setBoundaryRectangle();
        attackCollisionBox.setDebug(true);
        attackCollisionBox.isCollisionEnabled = false;
        addActor(attackCollisionBox);
    }

    private void loadAnimation(String enemyName) {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();

        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/walking1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/walking2"));
        walkingAnimation = new Animation<>(.2f, animationImages, Animation.PlayMode.LOOP);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/attacking1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/attacking2"));
        attackingAnimation = new Animation<>(.2f, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/idle1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("characters/" + enemyName + "/idle2"));
        idleAnimation = new Animation<>(.6f, animationImages, Animation.PlayMode.LOOP);

        setAnimation(walkingAnimation);
        if (BaseGame.debugEnabled) {
            setAnimation(idleAnimation);
            state = EnemyState.DEBUG;
        }
    }

    /**
     * Called on every game frame to update the state and behavior of the enemy.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        // Update chat delay and duration
        chatDelay += delta;
        if (chatDuration > 0) chatDuration -= delta;

        if (state == EnemyState.DETECT_DAMAGE) {

            // Apply damage to the player as soon as the player is hit,
            // then disable hit detection.
            if (attackCollisionBox.overlaps(following.getCollisionBox())) {
                state = EnemyState.ATTACK;
                following.applyDamage(data.attackDamage);
                attackCollisionBox.isCollisionEnabled = false;

                // TODO: remove in the future when not needed.
                System.out.println("damaged");
                System.out.println(following.getHealth());
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

        // Handle enemy movement and attack behaviors when following a player
        if (following != null && state != EnemyState.DEAD) {
            playerPosition.set(following.getX(Align.center), following.getY(Align.center));
            enemyPosition.set(this.getX(), this.getY());

            // Check if the player is out of attack range, and if so, move towards the player
            if (playerPosition.dst(enemyPosition) > data.getAttackRange()) {
                setMotionAngle(playerPosition.sub(enemyPosition).angleDeg());
                setSpeed(Player.MOVE_SPEED / 2f);
                state = EnemyState.MOVE;
            } else {
                // Player is in attack range, stop moving and enter attack state
                state = EnemyState.ATTACK;
                setMotionAngle(0);
                setSpeed(0);
                if (data == EnemyData.ARCHER) {
                    // Projectile.
                }
                else {
                    // Create damage shape in front.
                    // Set debug = true.
                    getActions().clear();

                    Vector2 lungeVector = playerPosition.sub(enemyPosition).nor().scl(data.lungeDistance);
                    Vector2 finalPosition = enemyPosition.add(lungeVector);

                    MoveToAction moveAction = Actions.moveTo(finalPosition.x, finalPosition.y, 0.5f, Interpolation.exp10);
                    SequenceAction sequence = Actions.sequence(
                            moveAction,
                            Actions.delay(0.1f),
                            Actions.run( () -> {
                                state = EnemyState.IDLE;
                                attackCollisionBox.isCollisionEnabled = false;
                                attackCooldown = 1.5f;
                                setAnimation(idleAnimation);
                            })
                    );
                    // Spawn the hitbox in front the enemy after a short delay
                    ParallelAction parallelAction = Actions.parallel(
                            sequence,
                            Actions.sequence(
                                    Actions.delay(0.05f),
                                    Actions.run( () -> {
                                        state = EnemyState.DETECT_DAMAGE;
                                        attackCollisionBox.isCollisionEnabled = true;
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

        // Chat messages above enemy heads.
        if (state != EnemyState.DEAD) {
            // Update chat messages
            if (chatDelay >= 3) {
                chatDelay = 0;
                chatDuration = 2f;

                // Randomly select and display a chat message
                int randomIndex = (int) (Math.random() * EnemyData.CHAT_MESSAGES.length);
                chatLabel.setText(EnemyData.CHAT_MESSAGES[randomIndex]);
            }

            // Hide chat message when its duration expires
            if (chatDuration <= 0) {
                chatDuration = 0;
                chatLabel.setText("");
            }
        }

        // TODO: remove if unneeded
//        // Position the chat group above the enemy
//        chatGroup.setPosition(((getWidth() - chatGroup.getWidth()) / 2f), getHeight() + 2f);

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
    public void draw(Batch batch, float parentAlpha) { super.draw(batch, parentAlpha); }

    /**
     * Initiates following of a player character by the enemy.
     *
     * @param player The player character to be followed.
     */
    public void beginFollowingPlayer(Player player) {
        this.following = player;
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
        moveAction.setPosition(((chatGroup.getWidth() - hitLabel.getWidth()) / 2f), 15);

        ParallelAction parallelAction = new ParallelAction();
        parallelAction.setActor(hitLabel);
        parallelAction.addAction(Actions.alpha(1));
        parallelAction.addAction(Actions.fadeOut(0.25f));
        parallelAction.addAction(moveAction);


        // Check if the enemy has been defeated
        if (this.currentHealth <= 0) {
            state = EnemyState.DEAD;
            parallelAction.addAction(Actions.delay(0.25f, Actions.run(this::remove)));
        }


        // Clear existing actions on the hit label and update its position and text
        this.hitLabel.getActions().clear();
        this.hitLabel.setPosition(((chatGroup.getWidth() - hitLabel.getWidth()) / 2f), 0);
        this.hitLabel.setText("" + (int) damage);
        this.hitLabel.addAction(parallelAction);

        // Apply camera shake.
        shakeCamera();

        EnemyHitEffect enemyHitEffect = new EnemyHitEffect();
        enemyHitEffect.setPosition(+getWidth() / 2 - BaseGame.UNIT_SCALE * 16, getHeight() / 2);
        enemyHitEffect.setScale(BaseGame.UNIT_SCALE / 4f);
//        enemyHitEffect.deltaScale = 4f;
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
