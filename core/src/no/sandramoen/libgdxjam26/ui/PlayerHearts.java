package no.sandramoen.libgdxjam26.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class PlayerHearts extends Actor {
    private int health; // Current health
    private Texture fullHeart;
    private Texture emptyHeart;
    private boolean[] destroyed = new boolean[4];
    private float heartSpacing = 15;
    public PlayerHearts() {
        this.fullHeart = new Texture(Gdx.files.internal("images/included/GUI/heart.png"));
        this.emptyHeart = new Texture(Gdx.files.internal("images/included/GUI/empty_heart.png"));
        this.health = 4; // Starting health with 4 hearts

        // Calculate the actor's width based on heart texture width and spacing
        setWidth((fullHeart.getWidth() * 4 + (3 * heartSpacing)) * BaseGame.UNIT_SCALE);
        setHeight(fullHeart.getHeight() * BaseGame.UNIT_SCALE);
//        setScale(BaseGame.UNIT_SCALE);
    }

    public void decreaseHealth(int amount) {
        for (int i = 0; i < amount; ++i) {
            decreaseHealth();
        }
    }

    public void decreaseHealth() {
        if (health > 0) {
            health--;
            destroyed[health] = true;
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        float startX = getX();
        for (int i = 0; i < health; i++) {
            Texture texture = destroyed[i] ? emptyHeart : fullHeart;
            batch.draw(texture, startX + i * (texture.getWidth() + heartSpacing), getY());
        }
    }
}
