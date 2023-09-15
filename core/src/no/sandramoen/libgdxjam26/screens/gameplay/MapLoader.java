package no.sandramoen.libgdxjam26.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdxjam26.actors.Player;
import no.sandramoen.libgdxjam26.actors.map.ImpassableTerrain;
import no.sandramoen.libgdxjam26.actors.map.TiledMapActor;
import no.sandramoen.libgdxjam26.utils.BaseGame;

public class MapLoader {
    public Player player;
    public Array<ImpassableTerrain> impassables;

    private TiledMapActor tilemap;
    private Stage mainStage;

    public MapLoader(Stage mainStage, TiledMapActor tilemap,
                     Player player, Array<ImpassableTerrain> impassables) {
        this.tilemap = tilemap;
        this.mainStage = mainStage;

        this.player = player;
        this.impassables = impassables;

        initializePlayer();
        initializeImpassables();
    }

    private void initializeImpassables() {
        if (tilemap.getTileList("actors", "impassable").size() > 0)
            for (MapObject mapObject : tilemap.getTileList("actors", "impassable")) {
                MapProperties mapProperties = mapObject.getProperties();
                float x = mapProperties.get("x", Float.class) * BaseGame.UNIT_SCALE;
                float y = mapProperties.get("y", Float.class) * BaseGame.UNIT_SCALE;
                float width = mapProperties.get("width", Float.class) * BaseGame.UNIT_SCALE;
                float height = mapProperties.get("height", Float.class) * BaseGame.UNIT_SCALE;
                impassables.add(new ImpassableTerrain(x, y, width, height, mainStage));
            }
    }

    private void initializePlayer() {
        String layerName = "actors";
        String propertyName = "player";
        if (tilemap.getTileList(layerName, propertyName).size() == 1) {
            MapObject mapObject = tilemap.getTileList(layerName, propertyName).get(0);
            float x = mapObject.getProperties().get("x", Float.class) * BaseGame.UNIT_SCALE;
            float y = mapObject.getProperties().get("y", Float.class) * BaseGame.UNIT_SCALE;
            player = new Player(x, y, mainStage);
        } else if (tilemap.getTileList(layerName, propertyName).size() > 1) {
            Gdx.app.error(getClass().getSimpleName(), "Error => found more than one property: " + propertyName + " on layer: " + layerName + "!");
        } else {
            Gdx.app.error(getClass().getSimpleName(), "Error => found no property: " + propertyName + " on layer: " + layerName + "!");
            player = null;
        }
    }
}
