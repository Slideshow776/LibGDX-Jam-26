package no.sandramoen.libgdxjam26.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;
    public static Skin mySkin;
    public static LevelScreen levelScreen;

    public static String defaultShader;
    public static String shockwaveShader;

    public static Array<TiledMap> maps;
    public static TiledMap testMap;
    public static TiledMap level1;
    public static TiledMap level2;
    public static TiledMap currentLevel;

    public static Sound click1Sound;
    public static Sound hoverOverEnterSound;

    public static Music menuMusic;

    // game state
    public static Preferences preferences;
    public static boolean loadPersonalParameters;
    public static boolean isCustomShadersEnabled = true;
    public static boolean isHideUI = false;
    public static float voiceVolume = 1f;
    public static float soundVolume = .5f;
    public static float musicVolume = .1f;
    public static final float UNIT_SCALE = .125f;

    private final Map<String, Pixmap> pixmapCache = new HashMap<String, Pixmap>();

    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        loadGameState();
        initializeUI();
        assetManager();

        maps = new Array<>();
        maps.add(testMap, level1, level2);
    }

    public static void setActiveScreen(BaseScreen screen) {
        game.setScreen(screen);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            assetManager.dispose();
        } catch (Error error) {
            Gdx.app.error(this.getClass().getSimpleName(), error.toString());
        }
    }

    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(this.getClass().getSimpleName(), "Could not load asset: " + asset.fileName, throwable);
    }

    private void loadGameState() {
        GameUtils.loadGameState();
        if (!loadPersonalParameters) {
            soundVolume = .75f;
            musicVolume = .5f;
            voiceVolume = 1f;
        }
    }

    private void initializeUI() {
        mySkin = new Skin(Gdx.files.internal("skins/mySkin/mySkin.json"));
        float scale = Gdx.graphics.getWidth() * .000656f; // magic number ensures scale ~= 1, based on screen width
        scale *= 1.01f; // make x percent bigger, bigger = more fuzzy

        mySkin.getFont("MetalMania-20").getData().setScale(scale);
        mySkin.getFont("MetalMania-40").getData().setScale(scale);
        mySkin.getFont("MetalMania-59").getData().setScale(scale);
    }

    private void assetManager() {
        long startTime = System.currentTimeMillis();
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);
        assetManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
        assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas.class);

        // shaders
        assetManager.load(new AssetDescriptor("shaders/default.vs", Text.class, new TextLoader.TextParameter()));
        assetManager.load(new AssetDescriptor("shaders/shockwave.fs", Text.class, new TextLoader.TextParameter()));

        // music
        // assetManager.load("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);

        // sound
        assetManager.load("audio/sound/click1.wav", Sound.class);
        assetManager.load("audio/sound/hoverOverEnter.wav", Sound.class);

        // tiled maps
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/test.tmx", TiledMap.class);
        assetManager.load("maps/level1.tmx", TiledMap.class);
        assetManager.load("maps/level2.tmx", TiledMap.class);

        assetManager.finishLoading();

        // shaders
        defaultShader = assetManager.get("shaders/default.vs", Text.class).getString();
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text.class).getString();

        // music
        // menuMusic = assetManager.get("audio/music/587251__lagmusics__epic-and-aggressive-percussion.mp3", Music.class);

        // sound
        click1Sound = assetManager.get("audio/sound/click1.wav", Sound.class);
        hoverOverEnterSound = assetManager.get("audio/sound/hoverOverEnter.wav", Sound.class);

        // tiled maps
        testMap = assetManager.get("maps/test.tmx", TiledMap.class);
        level1 = assetManager.get("maps/level1.tmx", TiledMap.class);
        level2 = assetManager.get("maps/level2.tmx", TiledMap.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        GameUtils.printLoadingTime(getClass().getSimpleName(), "Assetmanager", startTime);

        // TODO: particle effects.
    }

    /**
     * Cache pixmaps and retrieve from the cache if already created.
     */
    protected Pixmap getPixmap(String name) {
        if (!pixmapCache.containsKey(name)) {
            TextureAtlas.AtlasRegion atlasRegion = BaseGame.textureAtlas.findRegion(name);
            TextureData textureData = atlasRegion.getTexture().getTextureData();
            if (!textureData.isPrepared()) {
                textureData.prepare();
            }
            Pixmap newPixmap = new Pixmap(atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), Pixmap.Format.RGBA8888);
            newPixmap.drawPixmap(textureData.consumePixmap(), 0, 0);
            pixmapCache.put(name, newPixmap);
        }
        return pixmapCache.get(name);
    }
}
