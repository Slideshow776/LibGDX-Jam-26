package no.sandramoen.libgdxjam26.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;
import no.sandramoen.libgdxjam26.ui.experienceBar.ExperienceBar;
import no.sandramoen.libgdxjam26.ui.QuitWindow;
import no.sandramoen.libgdxjam26.utils.BaseActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;
import no.sandramoen.libgdxjam26.utils.BaseScreen;

public class LevelUpScreen extends BaseScreen {
    private QuitWindow quitWindow;
    private ExperienceBar experienceBar;
    private Label levelLabel;
    private Label messageLabel;
    private final float animationDelayIn = 1f;
    private final float animationDelayOut = 3f;

    private int startingLevel;
    private float percentToNextLevel;

    public LevelUpScreen(int levelBefore, int levelsGained, float percentCompletedToNextLevel, Array<Integer> abilityUnlocks) {
        this.startingLevel = levelBefore + levelsGained;
        this.percentToNextLevel = percentCompletedToNextLevel;

        levelLabel.setText("Level " + levelBefore);
        if (levelBefore >= abilityUnlocks.get(abilityUnlocks.size - 1)) {
            messageLabel.setText("");
        } else {
            int index = findAbilityUnlockIndex(levelBefore, abilityUnlocks);
            showNextAbilityMessage(index, abilityUnlocks);
        }

        new BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                Actions.delay(animationDelayIn),
                Actions.run(() -> startAnimation(levelBefore, levelsGained, percentCompletedToNextLevel, abilityUnlocks))
        ));
    }

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
        else if (keycode == Input.Keys.R && BaseGame.debugEnabled) {
            Array<Integer> abilityUnlocks = new Array<>();
            abilityUnlocks.add(20, 40, 60);
            BaseGame.setActiveScreen(new LevelUpScreen(5, 14, .75f, abilityUnlocks));
        }
        return super.keyDown(keycode);
    }

    private void startAnimation(int levelBefore, int levelsGained, float percentCompletedToNextLevel, Array<Integer> abilityUnlocks) {
        animateExperienceBarAndLevels(levelBefore, levelsGained);
        experienceBar.animateToPercent(percentCompletedToNextLevel);

        int index = findAbilityUnlockIndex(levelBefore, abilityUnlocks);

        if (levelBefore >= abilityUnlocks.get(abilityUnlocks.size - 1)) {
            messageLabel.setText("");
        } else if (levelBefore + levelsGained >= abilityUnlocks.get(index)) {
            unlockAbility();
        } else {
            showNextAbilityMessage(index, abilityUnlocks);
        }

        goToScreenWithDelay();
    }

    private void goToScreenWithDelay() {
        experienceBar.progress.addAction(Actions.after(Actions.run(() ->
                new BaseActor(0f, 0f, mainStage).addAction(Actions.sequence(
                        Actions.delay(animationDelayOut),
                        Actions.run(() -> BaseGame.setActiveScreen(new LevelScreen(startingLevel, percentToNextLevel)))
                ))
        )));
    }

    private void animateExperienceBarAndLevels(int levelBefore, int levelsGained) {
        for (int i = 0; i < levelsGained; i++) {
            experienceBar.animateToPercent(1f);

            int finalI = levelBefore + i + 1;
            experienceBar.progress.addAction(Actions.after(Actions.run(() -> levelLabel.setText("Level " + finalI))));
        }
    }

    private int findAbilityUnlockIndex(int levelBefore, Array<Integer> abilityUnlocks) {
        int index = 0;
        for (int i = abilityUnlocks.size - 1; i >= 0; i--) {
            if (levelBefore >= abilityUnlocks.get(i)) {
                index = (i == abilityUnlocks.size - 1) ? i : i + 1;
                break;
            }
        }
        return index;
    }

    private void unlockAbility() {
        experienceBar.progress.addAction(Actions.after(Actions.run(() -> turnLabelColorTo(BaseGame.paletteGreen))));
        experienceBar.progress.addAction(Actions.after(Actions.run(() -> messageLabel.setText("Ability Unlocked!"))));
    }

    private void showNextAbilityMessage(int index, Array<Integer> abilityUnlocks) {
        experienceBar.progress.addAction(Actions.after(Actions.run(() -> turnLabelColorTo(BaseGame.paletteRed))));
        messageLabel.setText("next ability in level " + abilityUnlocks.get(index));
    }

    private void turnLabelColorTo(Color colour) {
        float duration = 0.5f;
        messageLabel.addAction(Actions.color(colour, duration));
        levelLabel.addAction(Actions.color(colour, duration));
    }

    private void initializeGUI() {
        this.quitWindow = new QuitWindow();

        levelLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-59", BitmapFont.class), null));
        Label xpLabel = new Label("xp", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        messageLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("MetalMania-20", BitmapFont.class), null));
        messageLabel.setColor(new Color(0.361f, 0.38f, 0.455f, 1f));

        experienceBar = new ExperienceBar(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f, mainStage);

        uiTable.add(levelLabel)
                .colspan(2)
                .row();

        uiTable.add(xpLabel)
                .padRight(Gdx.graphics.getWidth() * .005f);

        uiTable.add(experienceBar)
                .row();

        uiTable.add(messageLabel)
                .colspan(2);

        // uiTable.setDebug(true);
    }
}
