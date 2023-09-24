package no.sandramoen.libgdxjam26.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdxjam26.utils.BaseScreen;
import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

public class MenuScreen extends BaseScreen {
    private int initializedWidth;

    @Override
    public void initialize() {
        Image featureGraphics = new Image(BaseGame.textureAtlas.findRegion("GUI/title"));
        featureGraphics.setOrigin(Align.center);
        featureGraphics.addAction(Actions.sequence(Actions.fadeOut(0), Actions.fadeIn(.5f)));
        uiTable.add(featureGraphics)
                .padBottom(Gdx.graphics.getHeight() * .045f)
                .size(Gdx.graphics.getWidth() * .6f, Gdx.graphics.getHeight() * .225f)
                .row();

        addTextButtons();

        /*uiTable.add(new MadeByLabel())
                .fillX()
                .padTop(Gdx.graphics.getHeight() * .09f);*/

        // uiTable.setDebug(true);

        if (Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(false);

        BaseGame.levelMusic.stop();
        GameUtils.playLoopingMusic(BaseGame.menuMusic);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        if (initializedWidth != 0)
            initializedWidth = width;
        super.resize(width, height);
    }

    @Override
    public void resume() {
        if (initializedWidth == 0)
            BaseGame.setActiveScreen(new MenuScreen());
        super.resume();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q && !uiTable.hasActions())
            uiTable.addAction(exitGameWithSoundAndDelay());
        else if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE)
            start();
        return super.keyDown(keycode);
    }

    private void addTextButtons() {
        uiTable.defaults()
                .width(Gdx.graphics.getWidth() * .125f)
                .height(Gdx.graphics.getHeight() * .075f)
                .spaceTop(Gdx.graphics.getHeight() * .01f);

        if (BaseGame.levelScreen != null)
            uiTable.add(resumeButton()).row();
        uiTable.add(startButton()).row();
        uiTable.add(optionsButton()).row();
        // uiTable.add(exitButton()).row();
        uiTable.defaults().reset();
    }

    private TextButton resumeButton() {
        TextButton button = new TextButton("Resume", BaseGame.mySkin);
        button.setColor(Color.BLUE);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(BaseGame.levelScreen);
                    return false;
                }
        );
        return button;
    }

    private TextButton startButton() {
        TextButton button = new TextButton("Start", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        start();
                    return false;
                }
        );
        return button;
    }

    private TextButton optionsButton() {
        TextButton button = new TextButton("Options", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new OptionsScreen());
                    return false;
                }
        );
        return button;
    }

    private TextButton exitButton() {
        TextButton button = new TextButton("Exit", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event) && !button.hasActions())
                        button.addAction(exitGameWithSoundAndDelay());
                    return false;
                }
        );
        return button;
    }

    private void start() {
        BaseGame.continuesLeft = BaseGame.MAX_CONTINUES;
        LevelScreen levelScreen =  new LevelScreen(1, 0);
        BaseGame.setActiveScreen(levelScreen);
    }

    private SequenceAction exitGameWithSoundAndDelay() {
        return Actions.sequence(
                // Actions.run(() -> playRandomSound()),
                Actions.delay(1),
                Actions.run(() -> Gdx.app.exit())
        );
    }
}
