package no.sandramoen.libgdxjam26.actors.enemy;

/**
 * The `EnemyState` enum represents the different states that an enemy character can be in within a game or simulation.
 * This enum is typically used to manage and control the behavior of enemy characters based on their current state.
 * It provides a convenient way to categorize and identify the various states an enemy can be in.
 */
public enum EnemyState {
    /**
     * The `MOVE` state indicates that the enemy is actively moving towards a target or a specific location.
     * During this state, the enemy may be navigating the game world, patrolling, or following a predefined path.
     */
    MOVE,

    /**
     * The `ATTACK` state signifies that the enemy is engaged in combat with a target, such as a player character or another entity.
     * In this state, the enemy is actively trying to harm or defeat its designated target using various attack behaviors.
     */
    ATTACK,

    /**
     * The `DEAD` state represents the condition where the enemy character has been defeated or is no longer functional.
     * In this state, the enemy is usually removed from the game or simulation, and any associated resources may be cleaned up.
     * Dead enemies typically do not participate in further game interactions.
     */
    DEAD,
    DETECT_DAMAGE,
    IDLE,
}
