package no.sandramoen.libgdxjam26.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.animation.KeyFrame;
import io.github.fourlastor.harlequin.animation.KeyFrameAnimation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AsepriteAnimationLoader {

    static Json json = new Json();

    public static Animation<TextureRegion> load(String animationName) {
        List<KeyFrame<TextureRegion>> keyFrames = new ArrayList<>();

        int startTime = 0;
        try {

            FileHandle fileHandle = Gdx.files.internal(animationName + ".json");
            JsonValue asepriteAnimation = BaseGame.jsonSerializer.parse(fileHandle);
            JsonValue frames = asepriteAnimation.get("frames");
            animationName = animationName.split("/", 3)[2];

            for (JsonValue key : frames) {
                TextureAtlas.AtlasRegion frameTexture = BaseGame.textureAtlas.findRegion(animationName);

                int x = key.get("frame").getInt("x");
                int y = key.get("frame").getInt("y");
                int w = key.get("frame").getInt("w");
                int h = key.get("frame").getInt("h");
                int duration = key.getInt("duration");

                keyFrames.add(KeyFrame.create(
                        startTime,
                        new TextureRegion(
                                frameTexture, x, y, w, h)));
                startTime += duration;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new KeyFrameAnimation<>(keyFrames, startTime / 1000f, Animation.PlayMode.NORMAL);
    }
}
