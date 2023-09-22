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
import java.util.ArrayList;
import java.util.List;

class AsepriteFrame {

    static class Rect {
        int x, y, w, h;

        public Rect() {}
    }
    ;

    Rect frame = new Rect();
    boolean rotated;
    boolean trimmed;
    Rect spriteSourceSize = new Rect();
    Rect sourceSize = new Rect();
    int duration;

    public AsepriteFrame() {}
}

public class AsepriteAnimationLoader {

    static Json json = new Json();

    public static Animation<TextureRegion> load(String animationName) {
        List<KeyFrame<TextureRegion>> keyFrames = new ArrayList<>();

        int startTime = 0;
        try {

            FileHandle fileHandle = Gdx.files.internal(animationName + ".json");
            JsonValue asepriteAnimation = BaseGame.jsonSerializer.parse(fileHandle);
            JsonValue frames = asepriteAnimation.get("frames");
            animationName = animationName.split("/", 4)[3];

            for (JsonValue key : frames) {
                TextureAtlas.AtlasRegion frameTexture = BaseGame.textureAtlas.findRegion(animationName);

                AsepriteFrame frame = json.fromJson(AsepriteFrame.class, key.prettyPrint(OutputType.minimal, 0));

                keyFrames.add(KeyFrame.create(
                        startTime,
                        new TextureRegion(
                                frameTexture, frame.frame.x, frame.frame.y, frame.frame.w, frame.frame.h)));
                startTime += frame.duration;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new KeyFrameAnimation<>(keyFrames, startTime / 1000f, Animation.PlayMode.NORMAL);
    }
}
