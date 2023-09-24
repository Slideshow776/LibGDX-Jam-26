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

    List<Enemy> alreadyHit = new ArrayList<>();

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
            if (enemy.isDead()) continue;
            if (alreadyHit.contains(enemy)) continue;

            if (enemy.overlaps(player.collisionBox.getBoundaryPolygon())) {

                int damage = 50;
                if (player.state == Player.State.CHARGEATTACK_DO)
                    damage = 150;
                enemy.hit(damage);
                alreadyHit.add(enemy);

                if (enemy.getState().equals(EnemyState.DEAD)) {
                    GameUtils.playWithRandomPitch(BaseGame.kill0Sound, .9f, 1.1f);
                    //Slow down the game
                    if (BaseGame.levelScreen != null) {
                        BaseGame.levelScreen.slowdown = 0.05f;
                        BaseGame.levelScreen.slowdownDuration = 0.1f;
                    }
                }
                else {
                    GameUtils.playWithRandomPitch(BaseGame.enemyHitSound, .9f, 1.1f);
                }
            }
        }
    }
}
