package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.utils.BaseActor;

public class Enemy extends BaseActor {

    private final EnemyData data;
    private boolean dead;
    private Player following;
    private Vector2 playerPosition, enemyPosition;

    public Enemy(EnemyData data, float x, float y, Stage stage) {
        super(x, y, stage);
        this.data = data;

        loadImage(data.getResource());
        setSize(data.getWidth(), data.getHeight());

        this.playerPosition = new Vector2();
        this.enemyPosition = new Vector2();
    }

    @Override
    public void act(float delta) {
        if (dead) {
            this.remove();
            return;
        }

        super.act(delta);

        if (following != null) {
            playerPosition.set(following.getX(), following.getY());
            enemyPosition.set(this.getX(), this.getY());


            if (playerPosition.dst(enemyPosition) > data.getAttackRange()) {
                float angleDeg = playerPosition.sub(enemyPosition).angleDeg();
                setMotionAngle(angleDeg);
                setSpeed(Player.MOVE_SPEED / 2f);
            } else {
                setMotionAngle(0);
                setSpeed(0);
            }
        }

        this.applyPhysics(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void beginFollowingPlayer(Player player) {
        this.following = player;
    }

}
