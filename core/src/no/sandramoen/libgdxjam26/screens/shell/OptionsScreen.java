package no.sandramoen.libgdxjam26.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdxjam26.screens.BaseScreen;
import no.sandramoen.libgdxjam26.ui.BaseSlider;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;


public class OptionsScreen extends BaseScreen {

    @Override
    public void initialize() {
        TypingLabel mainLabel = new TypingLabel("Options", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-59", BitmapFont.class), null));
        mainLabel.getFont().scale(.8f, .8f);
        uiTable.add(mainLabel)
                .growY()
                .padBottom(-Gdx.graphics.getHeight() * .15f)
                .row();

        uiTable.add(optionsTable())
                .growY()
                .row();

        uiTable.add(initializeBackButton())
                .expandY()
                .width(Gdx.graphics.getWidth() * .125f)
                .height(Gdx.graphics.getHeight() * .075f);

        /*uiTable.setDebug(true);*/
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            BaseGame.setActiveScreen(new MenuScreen());
        return super.keyDown(keycode);
    }

    private Table optionsTable() {
        Table table = new Table();

        BaseSlider soundSlider = new BaseSlider("Sound", 0, 1, .1f);
        BaseSlider musicSlider = new BaseSlider("Music", 0, 1, .1f);
        BaseSlider voiceSlider = new BaseSlider("Voice", 0, 1, .1f);

        table.defaults().spaceTop(Gdx.graphics.getHeight() * .05f).width(Gdx.graphics.getWidth() * .6f);
        table.add(soundSlider).row();
        table.add(musicSlider).row();
        table.add(voiceSlider).row();

        /*table.setDebug(true);*/
        return table;
    }

    private TextButton initializeBackButton() {
        TextButton backButton = new TextButton("Back", BaseGame.mySkin);
        backButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new MenuScreen());
                    return false;
                }
        );
        return backButton;
    }
}
