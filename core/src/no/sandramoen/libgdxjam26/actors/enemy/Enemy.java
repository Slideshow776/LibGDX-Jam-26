package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class Enemy extends BaseActor {

    private final EnemyData data;
    private final Vector2 playerPosition, enemyPosition;
    private final Group chatGroup;
    private final Label chatLabel;
    private final Label hitLabel;
    private int index;
    private EnemyState state = EnemyState.MOVE;
    private Player following;
    private float chatDuration = 1;
    private float chatDelay = 0;
    private float currentHealth;

    public Enemy(EnemyData data, float x, float y, Stage stage) {
        super(x, y, stage);
        this.data = data;
        this.currentHealth = data.getBaseHealth();
        this.loadImage(data.getResource());
        this.setSize(data.getWidth(), data.getHeight());
        this.image.setSize(data.getWidth(), data.getHeight());
        this.playerPosition = new Vector2();
        this.enemyPosition = new Vector2();
        this.chatGroup = new Group();
        this.chatLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        this.hitLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        this.chatLabel.setAlignment(Align.center);
        this.hitLabel.setAlignment(Align.center);
        this.chatGroup.setScale(BaseGame.UNIT_SCALE);
        this.chatGroup.addActor(chatLabel);
        this.chatGroup.addActor(hitLabel);

        stage.addActor(chatGroup);
    }

    @Override
    public void act(float delta) {
        if (state.equals(EnemyState.DEAD)) {
            this.remove();
            return;
        }

        super.act(delta);

        chatDelay += delta;
        chatDuration -= delta;

        if (following != null) {
            playerPosition.set(following.getX(), following.getY());
            enemyPosition.set(this.getX(), this.getY());


            if (playerPosition.dst(enemyPosition) > data.getAttackRange()) { // follow player location if not in range
                setMotionAngle(playerPosition.sub(enemyPosition).angleDeg());
                setSpeed(Player.MOVE_SPEED / 2f);
                state = EnemyState.MOVE;
            } else { // we are now in range... attack!
                state = EnemyState.ATTACK;
                setMotionAngle(0);
                setSpeed(0);
            }
        }

        if (chatDelay >= 5) {
            chatDelay = 0;
            chatDuration = 2f;

            int randomIndex = (int) (Math.random() * EnemyData.CHAT_MESSAGES.length);
            chatLabel.setText(EnemyData.CHAT_MESSAGES[randomIndex]);
        }

        if (chatDuration <= 0) {
            chatDuration = 0;
            chatLabel.setText("");
        }

        chatGroup.setPosition(getX() + ((getWidth() - chatGroup.getWidth()) / 2f), getY() + getHeight() + 2f);

        this.applyPhysics(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void beginFollowingPlayer(Player player) {
        this.following = player;
    }

    public void hit(float damage) {
        this.chatDelay = 0;
        this.chatDuration = 0;
        this.currentHealth = Math.max(0, this.currentHealth - damage);

        if (this.currentHealth <= 0) {
            state = EnemyState.DEAD;
            return;
        }

        MoveToAction moveAction = new MoveToAction();
        moveAction.setDuration(0.4f);
        moveAction.setPosition(((chatGroup.getWidth() - hitLabel.getWidth()) / 2f), 15f);

        this.hitLabel.getActions().clear();
        this.hitLabel.setPosition(((chatGroup.getWidth() - hitLabel.getWidth()) / 2f), 0);
        this.hitLabel.setText("" + (int) damage);
        this.hitLabel.addAction(Actions.alpha(1));
        this.hitLabel.addAction(Actions.fadeOut(0.4f));
        this.hitLabel.addAction(moveAction);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private enum EnemyState {
        MOVE, ATTACK, DEAD
    }

}
