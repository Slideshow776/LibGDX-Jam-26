package no.sandramoen.libgdxjam26.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class QuitWindow extends Window {

    private TextButton yes, no;

    public QuitWindow() {
        super("Are you sure?", BaseGame.mySkin);

        yes = new TextButton("Yes", BaseGame.mySkin);
        no = new TextButton("No", BaseGame.mySkin);

        getTitleLabel().setAlignment(Align.center);
        setModal(true);
        setMovable(false);
        setSize(250, 150);
        add(yes).pad(25);
        add(no).pad(25);
        align(Align.center);
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2, (Gdx.graphics.getHeight() - getHeight()) / 2);
        setVisible(false);

        yes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuitWindow.this.setVisible(false);
            }
        });
    }
}
