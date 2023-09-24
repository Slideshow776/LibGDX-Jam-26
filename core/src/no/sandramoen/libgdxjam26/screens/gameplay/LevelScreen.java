package no.sandramoen.libgdxjam26.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.compression.lzma.Base;
import no.sandramoen.libgdxjam26.actions.*;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.actors.enemy.EnemySpawnSystem;
import no.sandramoen.libgdxjam26.actors.enemy.EnemyState;
import no.sandramoen.libgdxjam26.actors.map.Background;
import no.sandramoen.libgdxjam26.actors.map.ImpassableTerrain;
import no.sandramoen.libgdxjam26.ui.AbilityBar;
import no.sandramoen.libgdxjam26.ui.PlayerHearts;
import no.sandramoen.libgdxjam26.ui.QuitWindow;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.BaseScreen;
import no.sandramoen.libgdxjam26.utils.GameUtils;
import no.sandramoen.libgdxjam26.utils.LoopedSound;

import java.util.Comparator;
import java.util.Iterator;

public class LevelScreen extends BaseScreen {
    private final Comparator<Actor> ySortComparator = Comparator.comparing((Actor actor) -> -actor.getY());
    public PlayerHearts hearts;
    public Background background;

    public Player player;
    public Label waveLabel;
    public Label waveFadeLabel;
    private TiledMap currentMap;
    private Array<ImpassableTerrain> impassables;
    private QuitWindow quitWindow;
    private EnemySpawnSystem enemySpawnSystem;
    private Label levelLabel;
    private AbilityBar abilityBar;
    private Vector2 source = new Vector2(), target = new Vector2();
    private int startingLevel;
    private float percentToNextLevel;
    public float shockWaveTimer = 0f;
    public float chargeAttackTimer = 0f;
    private boolean rightButtonDown;
    private boolean leftButtonDown;
    float startOffset = 1f;

    public LevelScreen(int startingLevel, float percentToNextLevel) {
        BaseGame.levelScreen = this;
        this.startingLevel = startingLevel;
        this.percentToNextLevel = percentToNextLevel;

        initializeActors();
        initializeGUI();
        BaseGame.menuMusic.stop();
        GameUtils.playLoopingMusic(BaseGame.levelMusic);

        OrthographicCamera test = (OrthographicCamera) mainStage.getCamera();
        mainStage.addActor(new CenterCamera(mainStage.getCamera()));
    }

    @Override
    public void initialize() {
    }

    // (sheerst) NOTE: could move this to a WidgetGroup.
    private void sortActors() {
        Array<Actor> actors = mainStage.getActors();
        Array<Actor> particles = new Array<Actor>();
        Array<Actor> characters = new Array<Actor>();
        Array<Actor> other = new Array<Actor>();
        for (Actor actor : actors) {
            if (actor instanceof Player) characters.add(actor);
            else if (actor instanceof Enemy) characters.add(actor);
            else if (actor instanceof ParticleEffectActor) characters.add(actor);
            else other.add(actor);
        }
        characters.sort(ySortComparator);
        actors.clear();

        // (sheerst) NOTE: could make a more formal layer system here.
        actors.addAll(other);
        actors.addAll(characters);
        actors.addAll(particles);
    }

    private void checkIfPlayerHitsEnemies() {
        if (slowdown >= 1) {
            Iterator<Enemy> it = this.enemySpawnSystem.getEnemies().iterator();
            while (it.hasNext()) {
                Enemy enemy = it.next();
                if (enemy == null) continue;

                if (enemy.countDead) {
                    enemy.countDead = false;
                    int previousLevel = player.getLevel();
                    player.addExperience(enemy.getData().getBaseExperience());
                    if (player.getLevel() > previousLevel)
                        levelLabel.setText("Level: " + player.getLevel());
                }
            }
        }
    }

    public void updateButtonsDown(int screenX, int screenY, int pointer, int button) {

        if (player.state != Player.State.IDLE && player.state != Player.State.MOVING)
            return;

        if (button == Input.Buttons.LEFT) {
            leftButtonDown = true;
        }
        else if (button == Input.Buttons.RIGHT) {
            rightButtonDown = true;
        }
    }

    public void checkPlayerLunge(int screenX, int screenY, int pointer, int button) {

        if (button != Input.Buttons.LEFT)
            return;

        if (player.state != Player.State.IDLE && player.state != Player.State.MOVING && player.state != Player.State.SHOCKWAVE_CHARGE && player.state != Player.State.CHARGEATTACK_CHARGE)
            return;

        rightButtonDown = false;
        player.chargeSound.stop();
        leftButtonDown = false;

        player.getActions().clear();
        source.set(player.getX(Align.center), player.getY(Align.center));

        if (chargeAttackTimer > 1.8f) {
            chargeAttackTimer = 0f;

            target.set(mainStage.screenToStageCoordinates(new Vector2(screenX, screenY)));

            Vector2 lungeVector = target.cpy().sub(source).nor().scl(Player.CHARGEATTACK_DISTANCE);
            MoveToAction lungeAction = new LungeMoveTo(player, enemySpawnSystem.getEnemies());
            lungeAction.setAlignment(Align.center);
            lungeAction.setDuration(0.3f);
            lungeAction.setInterpolation(Interpolation.exp10);
            Vector2 finalPosition = source.cpy().add(lungeVector);
            lungeAction.setPosition(finalPosition.x, finalPosition.y);
            SequenceAction sequence = Actions.sequence(
                Actions.run(() -> {
                    BaseGame.levelScreen.slowdown = 0.05f;
                    BaseGame.levelScreen.slowdownDuration = 0.4f;
                    GameUtils.playWithRandomPitch(BaseGame.chargeDo1Sound, 0.9f, 1.1f);
                }),
                Actions.delay(0.2f),
                Actions.run(() -> {
                    player.addAction(new ColorShader(new Color(1f, 1f, 1f, 1f), 0.4f, Interpolation.elastic));
                    GameUtils.playWithRandomPitch(BaseGame.chargeDo2Sound, 0.9f, 1.1f);
                }),
                lungeAction,
                Actions.delay(0.1f),
                Actions.run(() -> {
                    player.state = Player.State.IDLE;
                })
            );

            player.addAction(sequence);
            player.state = Player.State.CHARGEATTACK_DO;
            player.setAnimation(player.attackingAnimation);
            player.animationTime = .55f;
            player.chargeSound.stop();

            target.set(source.cpy().add(0, 12f));
            Vector2 moveVector = target.cpy().sub(source);
            finalPosition = source.cpy().add(moveVector);
            MoveToAction moveAction = Actions.moveToAligned(finalPosition.x, finalPosition.y, Align.center, 0.1f, Interpolation.exp10Out);
            player.addAction(moveAction);

            return;
        }

        // Get normalized Vector between player and mouse.
        target.set(mainStage.screenToStageCoordinates(new Vector2(screenX, screenY)));
        Vector2 lungeVector = target.sub(source).nor().scl(Player.LUNGE_DISTANCE);

        MoveToAction moveAction = new LungeMoveTo(player, enemySpawnSystem.getEnemies());
        moveAction.setAlignment(Align.center);
        moveAction.setDuration(0.6f);
        moveAction.setInterpolation(Interpolation.exp10);
        Vector2 finalPosition = source.add(lungeVector);
        moveAction.setPosition(finalPosition.x, finalPosition.y);
        SequenceAction sequence = Actions.sequence(
            moveAction,
            Actions.run(() -> {
                player.state = Player.State.IDLE;
            })
        );
        player.addAction(sequence);
        player.state = Player.State.LUNGING;
        player.setAnimation(player.attackingAnimation);
        player.animationTime = .55f;
        GameUtils.playWithRandomPitch(BaseGame.miss0Sound, .9f, 1.1f);
    }

    public void checkPlayerDash(int screenX, int screenY, int pointer, int button) {

        if (button != Input.Buttons.RIGHT)
            return;

        if (player.state != Player.State.IDLE && player.state != Player.State.MOVING && player.state != Player.State.SHOCKWAVE_CHARGE && player.state != Player.State.CHARGEATTACK_CHARGE)
            return;

        player.chargeSound.stop();
        leftButtonDown = false;
        rightButtonDown = false;

        player.getActions().clear();
        source.set(player.getX(Align.center), player.getY(Align.center));

        if (shockWaveTimer > 1.8f) {
            shockWaveTimer = 0f;

            player.state = Player.State.SHOCKWAVE_DO;
            final Vector2 finalPosition = source.cpy();
            MoveToAction moveAction = Actions.moveToAligned(finalPosition.x, finalPosition.y, Align.center, 0.1f, Interpolation.exp10Out);
            SequenceAction sequence = Actions.sequence(
                    Actions.run(() -> {
                        BaseGame.levelScreen.slowdown = 0.05f;
                        BaseGame.levelScreen.slowdownDuration = 0.4f;
                        GameUtils.playWithRandomPitch(BaseGame.chargeDo1Sound, 0.9f, 1.1f);
                    }),
                    Actions.delay(0.2f),
                    moveAction,
                    Actions.run(() -> {
                        GameUtils.playWithRandomPitch(BaseGame.shockwave1Sound, .9f, 1.2f);

                        Polygon boundaryPolygon = player.getBoundaryPolygon();
                        boundaryPolygon.setScale(6f, 6f);

                        for (Enemy enemy : enemySpawnSystem.getEnemies()) {
                            if (enemy == null) continue;
                            if (enemy.isDead()) continue;
                            if (enemy.overlaps(boundaryPolygon)) {
                                enemy.hit(80);

                                if (enemy.getState().equals(EnemyState.DEAD)) {
                                    GameUtils.playWithRandomPitch(BaseGame.kill0Sound, .9f, 1.1f);
                                    // Slow down the game
                                    if (BaseGame.levelScreen != null) {
                                        BaseGame.levelScreen.slowdown = 0.05f;
                                        BaseGame.levelScreen.slowdownDuration = 0.1f;
                                    }
                                }
                            }
                        }

                        player.shakeCamera(4f);
                        background.addAction(new ShockwaveShader(finalPosition.cpy(),2f, Interpolation.linear));
                    }),
                    Actions.delay(.2f),
                    Actions.run(() -> {
                        player.state = Player.State.IDLE;
                    })
            );
            player.addAction(sequence);

            // Small jump beginning
            target.set(source.cpy().add(0, 1f));
            Vector2 moveVector = target.cpy().sub(source).nor().scl(Player.SHOCKWAVE_DISTANCE);
            Vector2 finalPosition2 = source.cpy().add(moveVector);
            moveAction = Actions.moveToAligned(finalPosition2.x, finalPosition2.y, Align.center, 0.1f, Interpolation.exp10Out);
            player.addAction(moveAction);

            return;
        }

        shockWaveTimer = 0f;

        // Get normalized Vector between player and mouse.
        target.set(mainStage.screenToStageCoordinates(new Vector2(screenX, screenY)));
        Vector2 lungeVector = target.sub(source).nor().scl(Player.DASH_DISTANCE);

        Vector2 finalPosition = source.add(lungeVector);
        MoveToAction moveAction = Actions.moveToAligned(finalPosition.x, finalPosition.y, Align.center, 0.4f, Interpolation.exp10Out);
        SequenceAction sequence = Actions.sequence(
                moveAction,
                Actions.run(() -> {
                    player.state = Player.State.IDLE;
                })
        );
        player.addAction(sequence);
        ParallelAction parallelAction = Actions.parallel(
                new ColorShader(new Color(1f, 1f, 1f, 1f), 0.4f, Interpolation.elastic)
        );
        player.addAction(parallelAction);
        player.state = Player.State.DASHING;
        player.loadImage("characters/player/dash1");
        GameUtils.playWithRandomPitch(BaseGame.dash1Sound, .9f, 1.2f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void update(float delta) {

        if (startOffset > 0) {
            startOffset -= delta;
            return;
        }

        if (rightButtonDown) {
            shockWaveTimer += delta;

            if (player.state != Player.State.SHOCKWAVE_CHARGE && shockWaveTimer > .4f) {
                player.state = Player.State.SHOCKWAVE_CHARGE;
                player.loadImage("characters/player/shockwave1");

                SequenceAction sequenceAction = Actions.sequence(
                        Actions.delay(1.8f - shockWaveTimer),
                        new ColorShader(new Color(1f, 1f, 1f, 1f), 0.4f, Interpolation.elastic)
                );
                player.addAction(sequenceAction);

                player.chargeSound.stop();
                long soundId = player.chargeSound.play();
                player.chargeSound.setLooping(soundId, true);
            }
        }
        if (leftButtonDown) {
            chargeAttackTimer += delta;

            if (player.state != Player.State.CHARGEATTACK_CHARGE && chargeAttackTimer > .4f) {
                player.state = Player.State.CHARGEATTACK_CHARGE;
                player.loadImage("characters/player/charge1");

                SequenceAction sequenceAction = Actions.sequence(
                        Actions.delay(1.8f - chargeAttackTimer),
                        new ColorShader(new Color(1f, 1f, 1f, 1f), 0.4f, Interpolation.elastic)
                );
                player.addAction(sequenceAction);

                player.chargeSound.stop();
                player.chargeSound.play();
            }

        }

        // Sort actors by layer.
        sortActors();

        this.enemySpawnSystem.update(delta);

        // Check if the player is currently hitting any enemies.
        // Apply slow down effect and damage to enemies if so.
        checkIfPlayerHitsEnemies();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
            quitWindow.setVisible(!quitWindow.isVisible());
            pause = !pause;
        }
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen(startingLevel, percentToNextLevel));
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (startOffset > 0)
            return false;

        updateButtonsDown(screenX, screenY, pointer, button);

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (startOffset > 0)
            return false;

        checkPlayerLunge(screenX, screenY, pointer, button);

        checkPlayerDash(screenX, screenY, pointer, button);

        return super.touchUp(screenX, screenY, pointer, button);
    }

    private void initializeActors() {
        this.impassables = new Array();
        this.background = new Background(-2, -2, mainStage);
        this.player = new Player(35, 20, startingLevel, percentToNextLevel, mainStage);
    }

    private void initializeGUI() {
        this.enemySpawnSystem = new EnemySpawnSystem(player);

        Label abilityLabel = new Label("Ability unlocked at level 20", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        Label continueLabel = new Label("Continues left " + BaseGame.continuesLeft, new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));

        float horizontalPadding = Gdx.graphics.getWidth() * .02f;
        float verticalPadding = Gdx.graphics.getHeight() * .02f;

        this.quitWindow = new QuitWindow(this);
        this.levelLabel = new Label("Level " + player.getLevel(), new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        this.levelLabel.setPosition(horizontalPadding, Gdx.graphics.getHeight() - levelLabel.getHeight() - verticalPadding);

        this.hearts = new PlayerHearts();
        this.hearts.setPosition(Gdx.graphics.getWidth() - hearts.getWidth() - horizontalPadding, Gdx.graphics.getHeight() - hearts.getHeight() - verticalPadding);

        this.abilityBar = new AbilityBar(3);
        this.abilityBar.setPosition((Gdx.graphics.getWidth() - abilityBar.getWidth()) / 2f, verticalPadding);

        this.waveLabel = new Label("Wave " + enemySpawnSystem.getCurrentWave().wave, new Label.LabelStyle(BaseGame.mySkin.getFont("MetalMania-20"), null));
        this.waveLabel.setPosition((Gdx.graphics.getWidth() - waveLabel.getWidth()) / 2, Gdx.graphics.getHeight() - waveLabel.getHeight() - verticalPadding);

        this.waveFadeLabel = new Label("Wave " + enemySpawnSystem.getCurrentWave().wave, new Label.LabelStyle(BaseGame.mySkin.getFont("MetalMania-20"), null));
        this.waveFadeLabel.setFillParent(true);
        this.waveFadeLabel.setAlignment(Align.center);
        this.waveFadeLabel.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1), Actions.fadeOut(1)));
        this.waveFadeLabel.setFontScale(5);

        uiTable.addActor(levelLabel);
        uiTable.addActor(waveLabel);
        uiTable.addActor(waveFadeLabel);
        uiTable.addActor(hearts);
        uiTable.addActor(abilityBar);
        uiTable.addActor(quitWindow);

        uiTable.defaults()
                .padTop(Gdx.graphics.getHeight() * .02f)
                .padRight(Gdx.graphics.getWidth() * .02f)
                .padBottom(Gdx.graphics.getHeight() * .02f)
                .padLeft(Gdx.graphics.getWidth() * .02f);

        uiTable.add(continueLabel)
                .height(abilityLabel.getPrefHeight() * 1.5f)
                .bottom()
                .left();

        uiTable.add(abilityLabel)
                .height(abilityLabel.getPrefHeight() * 1.5f)
                .expand()
                .bottom()
                .right()
                .row();

        // uiTable.setDebug(true);
    }
}
