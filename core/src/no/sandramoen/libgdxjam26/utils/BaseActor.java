package no.sandramoen.libgdxjam26.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;


public class BaseActor extends Group {

    protected static Rectangle worldBounds;
    public final int ID = MathUtils.random(1000, 9999);
    public Animation<TextureRegion> animation;
    public boolean isFacingRight = true;
    public boolean pause = false;
    public float animationWidth = getWidth();
    public float animationHeight = getWidth();
    public boolean isCollisionEnabled = true;
    public float shakyCamIntensity = 22.025f;
    public boolean isShakyCam = false;
    protected Image image;
    private float animationTime = 0f;
    private boolean animationPaused = false;
    private Vector2 velocityVec = new Vector2(0f, 0f);
    private Vector2 accelerationVec = new Vector2(0f, 0f);
    private float acceleration = 0f;
    private float maxSpeed = 1000f;
    private float deceleration = 0f;
    private Polygon boundaryPolygon = null;

    public BaseActor(float x, float y, Stage stage) {
        super();

        setPosition(x, y);
        stage.addActor(this);

        animation = null;
        animationTime = 0;
        animationPaused = false;

        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;

        boundaryPolygon = null;
        /*setDebug(true);*/
    }

    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setAnimationSize(width, height);
    }

    @Override
    public void act(float delta) {
        if (!pause)
            super.act(delta);

        if (animation != null && !animationPaused) {
            animationTime += delta;
            ((TextureRegionDrawable) image.getDrawable()).setRegion(animation.getKeyFrame(animationTime));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //  apply color tint effect
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        super.draw(batch, parentAlpha);
    }

    private void setAnimationSize(float width, float height) {
        animationWidth = width;
        animationHeight = height;
    }

    public void flip() {
        isFacingRight = !isFacingRight;
    }

    public void setAnimationPaused(Boolean pause) {
        animationPaused = pause;
    }

    public Boolean isAnimationFinished() {
        return animation.isAnimationFinished(animationTime);
    }

    public Animation<TextureRegion> loadTexture(String fileName) {
        Array<String> fileNames = new Array(1);
        fileNames.add(fileName);
        return loadAnimationFromFiles(fileNames, 1f, true);
    }

    private Animation<TextureRegion> loadAnimationFromFiles(Array<String> fileNames, float frameDuration, Boolean loop) {
        Array<TextureRegion> textureArray = new Array();

        for (int i = 0; i < fileNames.size; i++) {
            Texture texture = new Texture(Gdx.files.internal(fileNames.get(i)));
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth() * BaseGame.UNIT_SCALE;
        float h = tr.getRegionHeight() * BaseGame.UNIT_SCALE;
        setSize(w, h);
        setOrigin(Align.center);

        if (image != null) image.remove();
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable();
        textureRegionDrawable.setRegion(tr);
        image = new Image(textureRegionDrawable);
        image.setSize(w, h);
        image.setAlign(Align.center);
        image.setOrigin(Align.center);
        addActor(image);

        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    public void loadImage(String name) {
        TextureRegion region = BaseGame.textureAtlas.findRegion(name);
        if (region == null)
            Gdx.app.error(getClass().getSimpleName(), "Error: region is null. Are you sure the image '" + name + "' exists?");
        setAnimation(new Animation(1f, region));
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    // Physics ---------------------------------------------------------------------------------------------------
    public void setSpeed(float speed) {
        if (velocityVec.len() == 0)
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    public void setAcceleration(float acc) {
        acceleration = acc;
    }

    public void setDeceleration(float dec) {
        deceleration = dec;
    }

    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    public float getMotionAngle() {
        return velocityVec.angleDeg();
    }

    public void setMotionAngle(float angle) {
        velocityVec.setAngleDeg(angle);
    }

    public void accelerateAtAngle(float angle) {
        accelerationVec.add(
                new Vector2(acceleration, 0).setAngleDeg(angle));
    }

    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    public void applyPhysics(float dt) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);

        float speed = getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0)
            speed -= deceleration * dt;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);

        // update velocity
        setSpeed(speed);

        // update position according to value stored in velocity vector
        moveBy(velocityVec.x * dt, velocityVec.y * dt);

        // reset acceleration
        accelerationVec.set(0, 0);
    }

    // camera ---------------------------------------------------------------------------------------------------
    public void zoomCamera(float zoom) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            camera.zoom = zoom;

            bindCameraToWorld(camera);
            camera.update();
        }
    }

    public void alignCamera(float targetX, float targetY, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getViewport().getCamera();

            // center camera on actor
            camera.position.set(new Vector3(
                    camera.position.x + (targetX + getOriginX() - camera.position.x) * lerp,
                    camera.position.y + (targetY + getOriginY() - camera.position.y) * lerp,
                    0f
            ));

            /*bindCameraToWorld(camera);*/
            camera.update();
        }
    }

    private void bindCameraToWorld(OrthographicCamera camera) {
        float minX = (camera.viewportWidth * camera.zoom) / 2;
        float maxX = worldBounds.width - (camera.viewportWidth * camera.zoom) / 2;
        if (minX <= maxX)
            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        else
            camera.position.x = (camera.viewportWidth * camera.zoom) / 2 - ((camera.viewportWidth * camera.zoom) - worldBounds.width) / 2;

        float minY = (camera.viewportHeight * camera.zoom) / 2;
        float maxY = worldBounds.height - (camera.viewportHeight * camera.zoom) / 2;
        if (minY <= maxY)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        else
            camera.position.y = (camera.viewportHeight * camera.zoom) / 2 - ((camera.viewportHeight * camera.zoom) - worldBounds.height) / 2;
    }

    public void averageBetweenTargetsCamera(Vector2 targetA, Vector2 targetB, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            float avgX = (targetA.x + targetB.x) / 2;
            float avgY = (targetA.y + targetB.y) / 2;
            Vector3 position = camera.position;

            position.x = camera.position.x + (avgX - camera.position.x) * lerp;
            position.y = camera.position.y + (avgY - camera.position.y) * lerp;
            camera.position.set(position);

            bindCameraToWorld(camera);
            camera.update();
        }
    }

    public Boolean searchFocalPoints(Array<Vector2> focalPoints, Vector2 target, float threshold, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            for (Vector2 point : focalPoints) {
                if (target.dst(point) < threshold) {
                    alignCamera(point.x, point.y, lerp);
                    return true;
                }
            }
        }
        return false;
    }

    public void shakeCamera() {
        this.getStage().getCamera().position.set(
                new Vector3(
                        this.getStage().getCamera().position.x + MathUtils.random(
                                -shakyCamIntensity,
                                shakyCamIntensity
                        ),
                        this.getStage().getCamera().position.y + MathUtils.random(
                                -shakyCamIntensity,
                                shakyCamIntensity
                        ),
                        0f
                )
        );
        /*bindCameraToWorld((OrthographicCamera) this.getStage().getCamera());*/
    }

    // Collision detection --------------------------------------------------------------------------------------
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();

        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2; // x-coordinate
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2; // y-coordinate
        }
        boundaryPolygon = new Polygon(vertices);
    }

    public boolean overlaps(BaseActor other) {
        if (!isCollisionEnabled || !other.isCollisionEnabled)
            return false;

        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    public Vector2 preventOverlap(BaseActor other) {
        if (!isCollisionEnabled || !other.isCollisionEnabled) return null;
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return null;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
            return null;

        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }

    // miscellaneous -------------------------------------------------------------------------------------------
    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor baseActor) {
        centerAtPosition(baseActor.getX() + baseActor.getWidth() / 2, baseActor.getY() + baseActor.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }

    public void boundToWorld() {
        if (getX() < 0)
            setX(0);
        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());
        if (getY() < 0)
            setY(0);
        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }

    public void setBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }
}
