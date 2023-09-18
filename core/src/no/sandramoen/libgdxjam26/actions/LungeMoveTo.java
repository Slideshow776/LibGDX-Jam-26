package no.sandramoen.libgdxjam26.actions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.actors.enemy.EnemyState;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LungeMoveTo extends MoveToAction {

    Player player;
    List<Enemy> enemies;

    public LungeMoveTo(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = new ArrayList<>(enemies);
    }

    @Override
    protected void update(float percentage) {
        super.update(percentage);

        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();
            if (enemy == null) continue;
            if (enemy.getState().equals(EnemyState.DEAD)) continue;
            Rectangle enemyBounds = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            if (enemyBounds.overlaps(playerBounds) || playerBounds.overlaps(enemyBounds)) {
                enemy.hit(50);

                if (enemy.getState().equals(EnemyState.DEAD)) {
                    GameUtils.playWithRandomPitch(BaseGame.kill0Sound, .9f, 1.1f);
                    //Slow down the game
                    BaseGame.levelScreen.slowdown = 0.05f;
                    BaseGame.levelScreen.slowdownDuration = 0.1f;
                }
            }
        }
    }
}
