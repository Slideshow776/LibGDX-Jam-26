package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.utils.BaseGame;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The EnemySpawnSystem class handles the spawning of enemies in the game world
 * based on a set time interval. Enemies are spawned randomly within the game map.
 */
public class EnemySpawnSystem {

    private static final LinkedList<WaveData> waves = new LinkedList<>();

    static {
        float startTime = 0; // Initial time
        byte enemyCount = 5; // Initial enemy count

        for (byte i = 1; i < Byte.MAX_VALUE; i++) {
            waves.offer(new WaveData(i, startTime, enemyCount));
            startTime += 15; // Increase time by 15 seconds for the next wave
            enemyCount += 5; // Increase enemy count by 5 for the next wave
        }
        System.out.println(waves);
    }

    // Fields to store references to the player and the game map.
    private final Player player;
    private final ArrayList<Enemy> enemies;
    // A timer to keep track of the time elapsed since the last enemy spawn.
    private float waveTime;
    private float enemySpawnTime;
    private WaveData currentWave;

    /**
     * Constructor for the EnemySpawnSystem class.
     *
     * @param player The player character in the game.
     */
    public EnemySpawnSystem(Player player) {
        this.player = player;
        this.enemies = new ArrayList<>();
        this.currentWave = waves.pop();
    }

    /**
     * Update method called during each game frame to handle enemy spawning logic.
     *
     * @param delta The time elapsed since the last frame (in seconds).
     */
    public void update(float delta) {
        this.waveTime += delta;
        this.enemySpawnTime += delta;

        float waveDifference = waves.peek().startTime - currentWave.startTime;
        float spawnTime = waveDifference / (float) currentWave.enemyCount;

        if (enemySpawnTime >= spawnTime) {
            enemySpawnTime = 0;
            spawnEnemyFromCurrentWave();
        }

        // Check if the elapsed time has surpassed the spawnTime interval.
        if (waveTime >= waveDifference) {
            waveTime = 0;

            currentWave = waves.pop();

            BaseGame.levelScreen.waveLabel.setText("Wave " + currentWave.wave);
            BaseGame.levelScreen.waveFadeLabel.setText("Wave " + currentWave.wave);
            BaseGame.levelScreen.waveFadeLabel.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1), Actions.fadeOut(1)));
        }
    }

    /**
     * Spawn an enemy based on the current wave's data.
     */
    public void spawnEnemyFromCurrentWave() {
        // Generate a random index to select an enemy type from the EnemyData enum.
        int randomIndex = (int) (Math.random() * EnemyData.values().length);
        EnemyData data = EnemyData.values()[2];

        // Define the minimum distance between the player and the enemy spawn point (radius).
        float minSpawnDistance = 30.0f; // Adjust this value as needed.

        // Calculate a random angle (in radians) for the enemy's spawn point.
        double randomAngle = Math.random() * 2 * Math.PI;

        // Calculate the spawn point coordinates that are outside the player's radius.
        float spawnX = (float) (player.getX() + minSpawnDistance * Math.cos(randomAngle));
        float spawnY = (float) (player.getY() + minSpawnDistance * Math.sin(randomAngle));

        // Create a new enemy instance and add it to the game stage.
        Enemy enemy = new Enemy(data, spawnX, spawnY, player.getStage());
        enemies.add(enemy);
        enemy.setIndex(enemies.indexOf(enemy));

        player.getStage().addActor(enemy);

        // Initiate the enemy to begin following the player character.
        enemy.beginFollowingPlayer(player);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public WaveData getCurrentWave() {
        return currentWave;
    }

    public static class WaveData {
        public byte wave;
        public float startTime;
        public byte enemyCount;

        public WaveData(byte wave, float startTime, byte enemyCount) {
            this.wave = wave;
            this.startTime = startTime;
            this.enemyCount = enemyCount;
        }

        @Override
        public String toString() {
            return String.format("Wave=%s, start=%s, enemies=%s", wave, startTime, enemyCount);
        }
    }

}