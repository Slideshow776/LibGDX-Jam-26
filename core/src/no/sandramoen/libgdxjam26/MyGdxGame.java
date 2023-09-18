package no.sandramoen.libgdxjam26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import no.sandramoen.libgdxjam26.screens.gameplay.LevelScreen;
import no.sandramoen.libgdxjam26.screens.shell.MenuScreen;
import no.sandramoen.libgdxjam26.screens.shell.SplashScreen;
import no.sandramoen.libgdxjam26.utils.BaseGame;

import java.util.Map;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		// setActiveScreen(new SplashScreen());
		// setActiveScreen(new MenuScreen());
		setActiveScreen(new LevelScreen(BaseGame.testMap));

		// Set custom cursor.
		Cursor customCursor = Gdx.graphics.newCursor(getPixmap("cursor"), 0, 0);
		Gdx.graphics.setCursor(customCursor);
	}
}

