package no.sandramoen.libgdxjam26;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;
import no.sandramoen.libgdxjam26.screens.shell.LevelUpScreen;
import no.sandramoen.libgdxjam26.screens.shell.MenuScreen;
import no.sandramoen.libgdxjam26.screens.shell.SplashScreen;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		// setActiveScreen(new SplashScreen());
		// setActiveScreen(new MenuScreen());
		setActiveScreen(new LevelScreen());

		/*Array<Integer> abilityUnlocks = new Array<>();
		abilityUnlocks.add(20, 40, 60);
		setActiveScreen(new LevelUpScreen(10, 10, .75f, abilityUnlocks));*/
	}
}

