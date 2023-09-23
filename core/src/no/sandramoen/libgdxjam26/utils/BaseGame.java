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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.utils.JsonReader;
import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;

import java.util.*;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;
    public static Skin mySkin;
    public static LevelScreen levelScreen;
    public static String defaultShader;
    public static String shockwaveShader;
    public static String contrastShader;
    public static String colorShader;
    public static ShaderProgram hallucinationShader;

    public static Sound click1Sound;
    public static Sound hoverOverEnterSound;
    public static Sound kill0Sound;
    public static Sound miss0Sound;
    public static Sound dash1Sound;
    public static Sound charge1Sound;
    public static Sound shockwave1Sound;
    public static Sound chargeDo1Sound;
    public static Sound chargeDo2Sound;
    public static Sound swordDrag1Sound;
    public static List<Sound> hitSounds = new ArrayList<>();
    public static Integer hitSoundsPreviousIndex = -1;
    public static List<Sound> swingSounds;
    public static Sound levelUpSound;

    public static Music menuMusic;
    public static Music levelMusic;

    public static Color paletteRed = new Color(0.353f, 0.125f, 0.2f, 1f);
    public static Color paletteGreen = new Color(0.255f, 0.455f, 0.353f, 1f);
    public static Color paletteColourIDX12 = new Color(0.125f, 0.286f, 0.294f, 1f);

    // game state
    public static Preferences preferences;
    public static boolean loadPersonalParameters;
    public static boolean isCustomShadersEnabled = true;
    public static boolean isHideUI = false;
    public static float voiceVolume = 1f;
    public static float soundVolume = .5f;
    public static float musicVolume = .1f;
    public static final float UNIT_SCALE = .125f;
    public static final int MAX_CONTINUES = 4;
    public static int continuesLeft = -1 ;
    public static final boolean debugEnabled = true;
    private final Map<String, Pixmap> pixmapCache = new HashMap<String, Pixmap>();
    public static JsonReader jsonSerializer = new JsonReader();

    public static Random random = new Random();

    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        loadGameState();
        initializeUI();
        setCursor();
        assetManager();
    }

    public void setCursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/included/cursor.png"));
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(cursor);
        pixmap.dispose();
    }

    public static void setActiveScreen(BaseScreen screen) {
        game.setScreen(screen);
        if (screen instanceof  LevelScreen) {
            BaseGame.levelScreen = (LevelScreen)screen;
        }
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
        assetManager.load(new AssetDescriptor("shaders/contrast.fs", Text.class, new TextLoader.TextParameter()));
        assetManager.load(new AssetDescriptor("shaders/color.fs", Text.class, new TextLoader.TextParameter()));

        // music
        assetManager.load("audio/music/menuMusic.ogg", Music.class);
        assetManager.load("audio/music/levelMusic.ogg", Music.class);

        // sound
        assetManager.load("audio/sound/click1.wav", Sound.class);
        assetManager.load("audio/sound/hoverOverEnter.wav", Sound.class);
        assetManager.load("audio/sound/player/kill0.ogg", Sound.class);
        assetManager.load("audio/sound/player/miss0.ogg", Sound.class);
        assetManager.load("audio/sound/player/hit1.ogg", Sound.class);
        assetManager.load("audio/sound/player/hit2.ogg", Sound.class);
        assetManager.load("audio/sound/player/hit3.ogg", Sound.class);
        assetManager.load("audio/sound/player/hit4.ogg", Sound.class);
        assetManager.load("audio/sound/player/charge1.ogg", Sound.class);
        assetManager.load("audio/sound/player/dash1.ogg", Sound.class);
        assetManager.load("audio/sound/player/shockwave1.ogg", Sound.class);
        assetManager.load("audio/sound/player/chargeDo1.ogg", Sound.class);
        assetManager.load("audio/sound/player/chargeDo2.ogg", Sound.class);
        assetManager.load("audio/sound/player/swordDrag1.ogg", Sound.class);
        assetManager.load("audio/sound/GUI/levelUp.ogg", Sound.class);

        assetManager.finishLoading();

        // shaders
        defaultShader = assetManager.get("shaders/default.vs", Text.class).getString();
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text.class).getString();
        contrastShader = assetManager.get("shaders/contrast.fs", Text.class).getString();
        colorShader = assetManager.get("shaders/color.fs", Text.class).getString();

        // music
        menuMusic = assetManager.get("audio/music/menuMusic.ogg", Music.class);
        levelMusic = assetManager.get("audio/music/levelMusic.ogg", Music.class);

        // sound
        click1Sound = assetManager.get("audio/sound/click1.wav", Sound.class);
        hoverOverEnterSound = assetManager.get("audio/sound/hoverOverEnter.wav", Sound.class);
        kill0Sound = assetManager.get("audio/sound/player/kill0.ogg", Sound.class);
        miss0Sound = assetManager.get("audio/sound/player/miss0.ogg", Sound.class);
        charge1Sound = assetManager.get("audio/sound/player/charge1.ogg", Sound.class);
        dash1Sound = assetManager.get("audio/sound/player/dash1.ogg", Sound.class);
        shockwave1Sound = assetManager.get("audio/sound/player/shockwave1.ogg", Sound.class);
        chargeDo1Sound = assetManager.get("audio/sound/player/chargeDo1.ogg", Sound.class);
        chargeDo2Sound = assetManager.get("audio/sound/player/chargeDo2.ogg", Sound.class);
        swordDrag1Sound = assetManager.get("audio/sound/player/swordDrag1.ogg", Sound.class);
        hitSounds.add(assetManager.get("audio/sound/player/hit1.ogg", Sound.class));
        hitSounds.add(assetManager.get("audio/sound/player/hit2.ogg", Sound.class));
        hitSounds.add(assetManager.get("audio/sound/player/hit3.ogg", Sound.class));
        hitSounds.add(assetManager.get("audio/sound/player/hit4.ogg", Sound.class));
        levelUpSound = assetManager.get("audio/sound/GUI/levelUp.ogg", Sound.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        GameUtils.printLoadingTime(getClass().getSimpleName(), "Assetmanager", startTime);

        String defaultVertexShader = Gdx.files.internal("shaders/default.vs").readString();
        String fragmentShader = Gdx.files.internal("shaders/hallucination.fs").readString();
        hallucinationShader = new ShaderProgram(defaultVertexShader, fragmentShader);
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
