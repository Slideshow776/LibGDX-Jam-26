package no.sandramoen.libgdxjam26.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AbilityBar extends Actor {

    private int numAbilities; // Number of abilities
    private Texture lockedAbility;
    private Texture unlockedAbility;
    private float abilitySpacing;
    private boolean[] unlocked;

    public AbilityBar(int numAbilities) {
        this.lockedAbility = new Texture(Gdx.files.internal("images/included/GUI/ability-locked.png"));
        this.unlockedAbility = new Texture(Gdx.files.internal("images/included/GUI/ability-unlocked.png"));
        this.numAbilities = numAbilities;
        this.abilitySpacing = 15;
        this.unlocked = new boolean[numAbilities];

        // Calculate the actor's width based on ability texture width and spacing
        setWidth(lockedAbility.getWidth() * numAbilities + ((numAbilities - 1) * abilitySpacing));
        setHeight(lockedAbility.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float startX = getX();
        for (int i = 0; i < numAbilities; i++) {
            Texture texture = unlocked[i] ? unlockedAbility : lockedAbility;
            batch.draw(texture, startX + i * (texture.getWidth() + abilitySpacing), getY());
        }
    }

    @Override
    public float getWidth() {
        return unlockedAbility.getWidth() * numAbilities + (abilitySpacing * (numAbilities - 1));
    }

    @Override
    public float getHeight() {
        return unlockedAbility.getHeight();
    }

}
