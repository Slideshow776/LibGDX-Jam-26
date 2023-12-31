package no.sandramoen.libgdxjam26.actions;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CenterCamera extends Actor {
    final Camera camera;
    final Vector3 currentPosition = new Vector3();

    public static float MOVE_DURATION = .25f;
    public static Interpolation INTERPOLATION = Interpolation.linear;

    public CenterCamera(Camera camera) {
        this.camera = camera;
        currentPosition.set(camera.position);
        setPosition(camera.position.x, camera.position.y);
    }

    @Override
    public void act (float delta) {
        super.act(delta);
        currentPosition.set(getX(), getY(), 0f);
        if (currentPosition.dst(camera.position) > 1e-1) {
            camera.position.lerp(currentPosition, INTERPOLATION.apply(delta / MOVE_DURATION));
        }
        else {
            camera.position.set(currentPosition);
        }
    }
}
