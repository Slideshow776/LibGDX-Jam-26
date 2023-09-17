package no.sandramoen.libgdxjam26.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.map.Background;
import no.sandramoen.libgdxjam26.actors.map.ImpassableTerrain;
import no.sandramoen.libgdxjam26.actors.map.TiledMapActor;
import no.sandramoen.libgdxjam26.screens.BaseScreen;
import no.sandramoen.libgdxjam26.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class LevelScreen extends BaseScreen {
    private TiledMap currentMap;

    private Array<ImpassableTerrain> impassables;
    private Player player;

    private TypingLabel topLabel;

    private TiledMapActor tilemap;

    public LevelScreen(TiledMap tiledMap) {
        currentMap = tiledMap;
        this.tilemap = new TiledMapActor(currentMap, mainStage);

        initializeActors();
        initializeGUI();

        OrthographicCamera test = (OrthographicCamera) mainStage.getCamera();
        System.out.println(test.zoom);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen(currentMap));
        else if (keycode == Keys.T)
            BaseGame.setActiveScreen(new LevelSelectScreen());
        return super.keyDown(keycode);
    }

    private void initializeActors() {
        impassables = new Array();
        new Background(0, 0, mainStage);
        player = new Player(0, 0, mainStage);
        // loadActorsFromMap();
    }

    private void loadActorsFromMap() {
        MapLoader mapLoader = new MapLoader(mainStage, tilemap, player, impassables);
        player = mapLoader.player;
    }

    private void initializeGUI() {
        topLabel = new TypingLabel("{SLOWER}G A M E   O V E R !", new Label.LabelStyle(BaseGame.mySkin.get("Play-Bold20white", BitmapFont.class), null));
        topLabel.setAlignment(Align.top);

        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(topLabel).height(topLabel.getPrefHeight() * 1.5f).expandY().top().row();
        uiTable.setDebug(true);
    }
}
