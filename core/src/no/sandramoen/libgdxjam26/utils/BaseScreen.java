package no.sandramoen.libgdxjam26.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseScreen implements Screen, InputProcessor, ControllerListener {
    public float slowdown = 1f;
    public float slowdownDuration = 1f;
    protected Stage mainStage;
    protected Stage uiStage;
    protected Table uiTable;
    private boolean pause;

    public BaseScreen() {
        mainStage = new Stage(new ExtendViewport(80, 45));

        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage = new Stage();
        uiStage.setViewport(new ScreenViewport());
        uiStage.addActor(uiTable);

        initialize();
    }

    public abstract void initialize();

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        if (slowdown > 0 && slowdown < 1) {
            delta = delta * slowdown;
            slowdownDuration -= Gdx.graphics.getDeltaTime();
            if (slowdownDuration <= 0) {
                slowdown = 1f;
                slowdownDuration = 1f;
            }
        }

        uiStage.act(delta);
        if (!pause) {
            mainStage.act(delta);
            update(delta);
        }

        Gdx.gl.glClearColor(.1f, .1f, .1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainStage.getViewport().apply();
        mainStage.draw();

        uiStage.getViewport().apply();
        uiStage.draw();
    }

    @Override
    public void show() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.addProcessor(this);
        im.addProcessor(uiStage);
        im.addProcessor(mainStage);
    }

    @Override
    public void hide() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        im.removeProcessor(mainStage);
    }

    @Override
    public void resize(int width, int height) {
        mainStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }


    @Override
    public void pause() {
        pause = true;
    }

    @Override
    public void resume() {
        pause = false;
    }

    @Override
    public void dispose() {
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1 && BaseGame.debugEnabled) {
            for (Actor actor : mainStage.getActors()) actor.setDebug(!actor.getDebug());
            for (Actor actor : uiStage.getActors()) actor.setDebug(!actor.getDebug());
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public void connected(Controller controller) {
    }

    @Override
    public void disconnected(Controller controller) {
    }
}
