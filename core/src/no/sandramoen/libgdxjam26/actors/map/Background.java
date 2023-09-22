package no.sandramoen.libgdxjam26.actors.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.harlequin.animation.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import io.github.fourlastor.harlequin.animation.FixedFrameAnimation;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class Background extends BaseActor {
    Vector2 center = new Vector2(40,30);
    float scroll = .1f;

    public Background(float x, float y, Stage stage) {
        super(x, y, stage);
        Array<TextureAtlas.AtlasRegion> animationImages = new Array<>();
        animationImages.add(BaseGame.textureAtlas.findRegion("bg2"));
        animation = new FixedFrameAnimation<>(2f, animationImages, Animation.PlayMode.LOOP);
        setAnimation(animation);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition((BaseGame.levelScreen.player.getX(Align.center) - center.x) * scroll, (BaseGame.levelScreen.player.getY(Align.center) - center.y) * scroll);
    }
}
