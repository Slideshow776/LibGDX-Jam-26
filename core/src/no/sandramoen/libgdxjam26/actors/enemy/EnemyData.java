package no.sandramoen.libgdxjam26.actors.enemy;

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
     * - Resource: whitePixel (a placeholder resource)
     */
    ARCHER("Archer", 0.5f, 1, 10, "whitePixel"),

    /**
     * The Mage enemy type.
     * - Name: Mage
     * - Width: 1 unit
     * - Height: 1 unit
     * - Attack Range: 8 units
     * - Resource: whitePixel (a placeholder resource)
     */
    MAGE("Mage", 1, 1, 8, "whitePixel"),

    /**
     * The Melee enemy type.
     * - Name: Melee
     * - Width: 1 unit
     * - Height: 1 unit
     * - Attack Range: 1 unit
     * - Resource: whitePixel (a placeholder resource)
     */
    MELEE("Melee", 1, 1, 1, "whitePixel");

    // Private fields to store enemy characteristics.
    private final String name, resource;
    private final float width, height, attackRange;

    /**
     * Constructor for an EnemyData enum value.
     *
     * @param name        The name of the enemy type.
     * @param width       The width of the enemy's hitbox (in game units).
     * @param height      The height of the enemy's hitbox (in game units).
     * @param attackRange The attack range of the enemy (in game units).
     * @param resource    The resource name for rendering the enemy (e.g., texture).
     */
    private EnemyData(String name, float width, float height, float attackRange, String resource) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.attackRange = attackRange;
        this.resource = resource;
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
}
