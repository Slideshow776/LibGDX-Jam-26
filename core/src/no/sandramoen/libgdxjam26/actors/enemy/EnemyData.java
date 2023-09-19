package no.sandramoen.libgdxjam26.actors.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * This enum represents different types of enemies in the LibGDX game.
 */
public enum EnemyData {
    // Define three types of enemies with their characteristics.

    /**
     * The Archer enemy type.
     * - Name: Archer
     * - Width: 0.5 units
     * - Height: 1 unit
     * - Attack Range: 10 units
     * - Attack Speed: 1 unit
     * - Base Health: 100 units
     * - Base Experience: 50 units
     * - Resource: whitePixel (a placeholder resource)
     */
    ARCHER("Archer", 5, 5, 15, 1, 100, 50, "enemyKnight", 1, 1),

    /**
     * The Mage enemy type.
     * - Name: Mage
     * - Width: 1 unit
     * - Height: 1 unit
     * - Attack Range: 8 units
     * - Attack Speed: 1 unit
     * - Base Health: 120 units
     * - Base Experience: 60 units
     * - Resource: whitePixel (a placeholder resource)
     */
    MAGE("Mage", 5, 5, 12, 1, 120, 60, "enemyKnight", 1, 1),

    /**
     * The Melee enemy type.
     * - Name: Melee
     * - Width: 1 unit
     * - Height: 1 unit
     * - Attack Range: 1 unit
     * - Attack Speed: 1 unit
     * - Base Health: 80 units
     * - Base Experience: 40 units
     * - Resource: whitePixel (a placeholder resource)
     */
    MELEE("Melee", 5, 5, 2, 1, 80, 40, "enemyKnight", 5, 1);

    public static final String[] CHAT_MESSAGES = {"You credit score sucks!", "I'm going to kill you!", "Ich will nicht sterben!"};

    // Private fields to store enemy characteristics.
    private final String name, resource;
    private final float width, height, attackRange, attackSpeed, baseHealth, baseExperience;
    public final float lungeDistance;
    public final int attackDamage;

    /**
     * Constructor for an EnemyData enum value.
     *
     * @param name           The name of the enemy type.
     * @param width          The width of the enemy's hitbox (in game units).
     * @param height         The height of the enemy's hitbox (in game units).
     * @param attackRange    The attack range of the enemy (in game units).
     * @param attackSpeed    The attack speed of the enemy (in game units).
     * @param baseHealth     The base health of the enemy (in game units).
     * @param baseExperience The base experience gained for defeating the enemy (in game units).
     * @param resource       The resource name for rendering the enemy (e.g., texture).
     */
    private EnemyData(String name, float width, float height, float attackRange, float attackSpeed, float baseHealth, float baseExperience, String resource, float lungeDistance, int attackDamage) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.attackRange = attackRange;
        this.attackSpeed = attackSpeed;
        this.baseHealth = baseHealth;
        this.baseExperience = baseExperience;
        this.resource = resource;
        this.lungeDistance = lungeDistance;
        this.attackDamage = attackDamage;

    }

    /**
     * Get the name of the enemy type.
     *
     * @return The name of the enemy.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the resource name associated with the enemy type.
     *
     * @return The resource name (e.g., texture) for rendering the enemy.
     */
    public String getResource() {
        return resource;
    }

    /**
     * Get the width of the enemy's hitbox.
     *
     * @return The width of the enemy's hitbox in game units.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get the height of the enemy's hitbox.
     *
     * @return The height of the enemy's hitbox in game units.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Get the attack range of the enemy.
     *
     * @return The attack range of the enemy in game units.
     */
    public float getAttackRange() {
        return attackRange;
    }

    /**
     * Get the attack speed of the enemy.
     *
     * @return The attack speed of the enemy in game units.
     */
    public float getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Get the base health of the enemy.
     *
     * @return The base health of the enemy in game units.
     */
    public float getBaseHealth() {
        return baseHealth;
    }

    /**
     * Get the base experience gained for defeating the enemy.
     *
     * @return The base experience in game units.
     */
    public float getBaseExperience() {
        return baseExperience;
    }
}