package no.sandramoen.libgdxjam26.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;
import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.enemy.Enemy;
import no.sandramoen.libgdxjam26.actors.enemy.EnemySpawnSystem;
import no.sandramoen.libgdxjam26.actors.enemy.EnemyState;
import no.sandramoen.libgdxjam26.actors.map.Background;
import no.sandramoen.libgdxjam26.actors.map.ImpassableTerrain;
import no.sandramoen.libgdxjam26.utils.BaseScreen;
import no.sandramoen.libgdxjam26.ui.ExperienceBar;
import no.sandramoen.libgdxjam26.ui.QuitWindow;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.GameUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LevelScreen extends BaseScreen {
    private TiledMap currentMap;

    private Array<ImpassableTerrain> impassables;
    private Player player;

    private TypingLabel topLabel;

    private Vector2 source = new Vector2(), target = new Vector2();

    private QuitWindow quitWindow;
    private EnemySpawnSystem enemySpawnSystem;
    private ExperienceBar experienceBar;
    private Label levelLabel;
    private Label experienceLabel;

    public LevelScreen() {

        initializeActors();
        initializeGUI();

        OrthographicCamera test = (OrthographicCamera) mainStage.getCamera();
        this.enemySpawnSystem = new EnemySpawnSystem(player);
    }

    @Override
    public void initialize() {
    }

    private final Comparator<Actor> ySortComparator = Comparator.comparing((Actor actor) -> -actor.getY() );

    // (sheerst) NOTE: could move this to a WidgetGroup.
    private void sortActors() {
        Array<Actor> actors = mainStage.getActors();
        Array<Actor> players = new Array<Actor>();
        Array<Actor> enemies = new Array<Actor>();
        Array<Actor> other = new Array<Actor>();
        for (Actor actor : actors) {
            if (actor instanceof Player) players.add(actor);
            else if (actor instanceof Enemy) enemies.add(actor);
            else other.add(actor);
        }
        players.sort(ySortComparator);
        enemies.sort(ySortComparator);
        actors.clear();

        // (sheerst) NOTE: could make a more formal layer system here.
        actors.addAll(other);
        actors.addAll(enemies);
        actors.addAll(players);
    }

    @Override
    public void update(float delta) {

        // Sort actors by layer.
        sortActors();

        this.enemySpawnSystem.update(delta);

        if (slowdown >= 1) {
            Iterator<Enemy> it = this.enemySpawnSystem.getEnemies().iterator();
            while (it.hasNext()) {
                Enemy enemy = it.next();
                if (enemy == null) continue;

                if (enemy.getState().equals(EnemyState.DEAD)) {
                    int previousLevel = player.getLevel();
                    player.addExperience(enemy.getData().getBaseExperience());
                    it.remove();
                    enemy.remove();
                    if (player.getLevel() > previousLevel) {
                        experienceBar.setRange(0, player.getExperienceForCurrentLevel());
                        experienceBar.setValue(player.getExperience());
                        levelLabel.setText("" + player.getLevel());
                    }
                    experienceLabel.setText((int) player.getExperience() + " / " + (int) player.getExperienceForCurrentLevel());
                    experienceBar.setAnimateDuration(0.25f);
                    experienceBar.setValue(player.getExperience());
                }
            }
        }

        if (player.state != Player.State.LUNGING) {
            // (sheerst) TODO: move this to player.update() or player.act()
            // Set mouse and player position for use in calculations.
            source.set(player.getX(Align.center), player.getY(Align.center));
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            target.set(mouseX, mouseY);
            mainStage.screenToStageCoordinates(target);

            if (target.dst2(source) > 1e-1) {
                // Move player towards cursor.
                player.isMoving = true;
                float angleDeg = target.sub(source).angleDeg();
                player.setMotionAngle(angleDeg);
                player.setSpeed(Player.MOVE_SPEED);
            } else {
                player.isMoving = false;
                player.setMotionAngle(0f);
                player.setSpeed(0);
            }
            player.applyPhysics(delta);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            quitWindow.setVisible(!quitWindow.isVisible());
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (player.state == Player.State.IDLE && button == Input.Buttons.LEFT) {
            player.getActions().clear();

            // Get normalized Vector between player and mouse.
            target.set(mainStage.screenToStageCoordinates(new Vector2(screenX, screenY)));
            source.set(player.getX(), player.getY());  // No idea why but Align.center breaks this.
            Vector2 lungeVector = target.sub(source).nor().scl(player.LUNGE_DISTANCE);

            MoveToAction moveAction = new MoveToAction() {
                final List<Enemy> enemies = new ArrayList<>(enemySpawnSystem.getEnemies());

                @Override
                protected void update(float percentage) {
                    super.update(percentage);

                    Iterator<Enemy> it = enemies.iterator();
                    while (it.hasNext()) {
                        Enemy enemy = it.next();
                        if (enemy == null) continue;
                        if (enemy.getState().equals(EnemyState.DEAD)) continue;
                        Rectangle enemyBounds = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                        if (enemyBounds.overlaps(playerBounds) || playerBounds.overlaps(enemyBounds)) {
                            enemy.hit(50);

                            if (enemy.getState().equals(EnemyState.DEAD)) {
                                GameUtils.playWithRandomPitch(BaseGame.kill0Sound, .9f, 1.1f);
                                //Slow down the game
                                slowdown = 0.05f;
                                slowdownDuration = 0.1f;

                            }
                        }
                    }
                }
            };
            moveAction.setDuration(0.6f);
            moveAction.setInterpolation(Interpolation.exp10);
            Vector2 finalPosition = source.add(lungeVector);
            moveAction.setPosition(finalPosition.x, finalPosition.y);
            SequenceAction sequence = Actions.sequence(
                moveAction,
                Actions.delay(0.1f),
                Actions.run( () -> player.state = Player.State.IDLE)
            );
            player.addAction(sequence);
            player.state = Player.State.LUNGING;
            GameUtils.playWithRandomPitch(BaseGame.miss0Sound, .9f, 1.1f);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void initializeActors() {
        this.impassables = new Array();
        new Background(0, 0, mainStage);
        this.player = new Player(0, 0, mainStage);
    }

    private void initializeGUI() {
        topLabel = new TypingLabel("{SLOWER}G A M E   O V E R !", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        topLabel.setAlignment(Align.top);

        this.quitWindow = new QuitWindow();
        this.experienceBar = new ExperienceBar(player.getExperienceForCurrentLevel());
        this.levelLabel = new Label("" + player.getLevel(), BaseGame.mySkin);
        this.experienceLabel = new Label((int) player.getExperience() + " / " + (int) player.getExperienceForCurrentLevel(), BaseGame.mySkin);
        this.experienceBar.setSize(300, 15);
        this.experienceBar.setPosition((Gdx.graphics.getWidth() - experienceBar.getWidth()) / 2, 35);
        this.levelLabel.setPosition((Gdx.graphics.getWidth() - levelLabel.getWidth()) / 2, experienceBar.getY() + experienceBar.getHeight() + levelLabel.getHeight());
        this.experienceLabel.setPosition(experienceBar.getX() + (experienceBar.getWidth() - experienceLabel.getWidth()) / 2, experienceBar.getY() + (experienceBar.getHeight() - experienceLabel.getHeight()) / 2);

        uiStage.addActor(quitWindow);
        uiStage.addActor(experienceBar);
        uiStage.addActor(levelLabel);
        uiStage.addActor(experienceLabel);

        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(topLabel).height(topLabel.getPrefHeight() * 1.5f).expandY().top().row();
        // uiTable.setDebug(true);
    }
}
