package no.sandramoen.libgdxjam26.actors.enemy;

import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.map.TiledMapActor;

import java.util.ArrayList;

/**
 * The EnemySpawnSystem class handles the spawning of enemies in the game world
 * based on a set time interval. Enemies are spawned randomly within the game map.
 */
public class EnemySpawnSystem {

    // Fields to store references to the player and the game map.
    private final Player player;
    private final TiledMapActor tiledMapActor;
    private final ArrayList<Enemy> enemies;

    // The time interval (in seconds) between enemy spawns.
    private float spawnTime = 3;

    // A timer to keep track of the time elapsed since the last enemy spawn.
    private float surpassed;

    /**
     * Constructor for the EnemySpawnSystem class.
     *
     * @param tiledMapActor The TiledMapActor representing the game map.
     * @param player        The player character in the game.
     */
    public EnemySpawnSystem(TiledMapActor tiledMapActor, Player player) {
        this.player = player;
        this.tiledMapActor = tiledMapActor;
        this.enemies = new ArrayList<>();
    }

    /**
     * Update method called during each game frame to handle enemy spawning logic.
     *
     * @param delta The time elapsed since the last frame (in seconds).
     */
    public void update(float delta) {
        this.surpassed += delta;

        // Check if the elapsed time has surpassed the spawnTime interval.
        if (surpassed >= spawnTime) {
            surpassed = 0;

            // Spawn a random enemy.
            spawnRandomEnemy();
        }
    }

    /**
     * Spawn a random enemy at a random location within the game map.
     */
    public void spawnRandomEnemy() {
        // Generate a random index to select an enemy type from the EnemyData enum.
        int randomIndex = (int) (Math.random() * EnemyData.values().length);
        EnemyData data = EnemyData.values()[randomIndex];

        // Generate random X and Y coordinates within the game map bounds.
        int randomX = (int) (Math.random() * TiledMapActor.mapWidth);
        int randomY = (int) (Math.random() * TiledMapActor.mapHeight);

        // Create a new enemy instance and add it to the game stage.
        Enemy enemy = new Enemy(data, randomX, randomY, player.getStage());
        enemies.add(enemy);
        enemy.setIndex(enemies.indexOf(enemy));

        player.getStage().addActor(enemy);

        // Initiate the enemy to begin following the player character.
        enemy.beginFollowingPlayer(player);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
