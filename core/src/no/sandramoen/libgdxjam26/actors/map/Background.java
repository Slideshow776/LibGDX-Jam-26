package no.sandramoen.libgdxjam26.actors.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class Background extends BaseActor {
    private Array<TextureAtlas.AtlasRegion> animationImages = new Array();

    public Background(float x, float y, Stage stage) {
        super(x, y, stage);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setColor(Color.BLACK);

        animationImages.add(BaseGame.textureAtlas.findRegion("whitePixel"));
        animation = new Animation(2f, animationImages, Animation.PlayMode.LOOP);
    }
}
