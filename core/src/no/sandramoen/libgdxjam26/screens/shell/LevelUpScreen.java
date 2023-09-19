package no.sandramoen.libgdxjam26.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import no.sandramoen.libgdxjam26.actors.ExperienceBar;
import no.sandramoen.libgdxjam26.actors.map.Background;
import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;
import no.sandramoen.libgdxjam26.ui.QuitWindow;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.BaseScreen;

public class LevelUpScreen extends BaseScreen {
    private QuitWindow quitWindow;

    @Override
    public void initialize() {
        initializeGUI();
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            quitWindow.setVisible(!quitWindow.isVisible());
        else if (keycode == Input.Keys.R)
            BaseGame.setActiveScreen(new LevelUpScreen());
        return super.keyDown(keycode);
    }


    private void initializeGUI() {
        this.quitWindow = new QuitWindow();

        Label levelLabel = new Label("Level 18", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        Label xpLabel = new Label("xp", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        Label abilityLabel = new Label("next ability in level 20", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));

        ExperienceBar experienceBar = new ExperienceBar(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f, mainStage);
        experienceBar.animateOneLevel();
        experienceBar.animateOneLevel();
        experienceBar.animateOneLevel();
        experienceBar.animateOneLevel();

        uiTable.add(levelLabel)
                .colspan(2)
                .row();

        uiTable.add(xpLabel);

        uiTable.add(experienceBar)
                .row();

        uiTable.add(abilityLabel)
                .colspan(2);

        // uiTable.setDebug(true);
    }
}
