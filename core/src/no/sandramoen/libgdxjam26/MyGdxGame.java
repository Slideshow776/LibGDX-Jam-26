package no.sandramoen.libgdxjam26;

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
		setActiveScreen(new MenuScreen());
//		 setActiveScreen(new LevelScreen());
//		 setActiveScreen(new LevelUpScreen());
	}
}

