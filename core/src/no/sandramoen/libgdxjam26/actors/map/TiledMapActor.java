package no.sandramoen.libgdxjam26.actors.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

import java.util.ArrayList;
import java.util.Iterator;

import no.sandramoen.libgdxjam26.utils.BaseGame;

public class TiledMapActor extends Actor {
    public static float mapWidth;
    public static float mapHeight;

    private TiledMap tiledMap;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    public TiledMapActor(TiledMap tiledMap, Stage stage) {
        this.tiledMap = tiledMap;
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(this.tiledMap, BaseGame.UNIT_SCALE);
        tiledMapRenderer.setBlending(true);

        /*centerPositionCamera(
                (OrthographicCamera) stage.getCamera(),
                calculateCenterOfMap()
        );*/

        setMapSize();
        stage.addActor(this);
    }

    public ArrayList<MapObject> getRectangleList(String propertyName) {
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers()) {
            for (MapObject obj : layer.getObjects()) {
                if (!(obj instanceof RectangleMapObject))
                    continue;

                MapProperties props = obj.getProperties();

                if (props.containsKey("name") && props.get("name").equals(propertyName))
                    list.add(obj);
            }
        }
        return list;
    }

    public ArrayList<MapObject> getTileList(String layerName, String propertyName) {
        ArrayList<MapObject> list = new ArrayList();

        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equalsIgnoreCase(layerName))
                for (MapObject obj : layer.getObjects()) {
                    if (!(obj instanceof TiledMapTileMapObject))
                        continue;

                    MapProperties props = obj.getProperties();

                    // Default MapProperties are stored within associated Tile object
                    // Instance-specific overrides are stored in MapObject

                    TiledMapTileMapObject tmtmo = (TiledMapTileMapObject) obj;
                    TiledMapTile t = tmtmo.getTile();
                    MapProperties defaultProps = t.getProperties();

                    if (defaultProps.containsKey("name") && defaultProps.get("name").equals(propertyName))
                        list.add(obj);

                    // get list of default property keys
                    Iterator<String> propertyKeys = defaultProps.getKeys();

                    // iterate over keys; copy default values into props if needed
                    while (propertyKeys.hasNext()) {
                        String key = propertyKeys.next();

                        // check if value already exists; if not, create property with default value
                        if (props.containsKey(key))
                            continue;
                        else {
                            Object value = defaultProps.get(key);
                            props.put(key, value);
                        }
                    }
                }
        }
        return list;
    }

    public void draw(Batch batch, float parentAlpha) {
        tiledMapRenderer.setView((OrthographicCamera) getStage().getCamera());
        tiledMapRenderer.render();
    }

    public void centerPositionCamera(OrthographicCamera camera, Vector2 centerOfMap) {
        camera.zoom = 1f;
        camera.position.set(new Vector3(
                centerOfMap.x,
                centerOfMap.y,
                0f
        ));
        camera.update();
    }

    private void setMapSize() {
        mapWidth = tiledMap.getProperties().get("width", Integer.class);
        mapHeight = tiledMap.getProperties().get("height", Integer.class);
    }

    private Vector2 calculateCenterOfMap() {
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        int numTilesHorizontal = tiledMap.getProperties().get("width", Integer.class);
        int numTilesVertical = tiledMap.getProperties().get("height", Integer.class);
        mapWidth = tileWidth * numTilesHorizontal;
        mapHeight = tileHeight * numTilesVertical;

        System.out.println("center of map: (" + mapWidth / 2 + ", " + mapHeight / 2 + ")");
        System.out.println("center of screen: (" + Gdx.graphics.getWidth() / 2 + ", " + Gdx.graphics.getHeight() / 2 + ")");
        return new Vector2(mapWidth / 2, mapHeight / 2);
    }
}
