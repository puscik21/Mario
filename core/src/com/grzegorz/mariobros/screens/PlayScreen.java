package com.grzegorz.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.sprites.TileObjects.Brick;
import com.grzegorz.mariobros.sprites.enemies.Enemy;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.items.CoinAnimation;
import com.grzegorz.mariobros.sprites.items.Item;
import com.grzegorz.mariobros.sprites.items.ItemDef;
import com.grzegorz.mariobros.sprites.items.Mushroom;
import com.grzegorz.mariobros.sprites.items.PieceOfBrick;
import com.grzegorz.mariobros.tools.B2WorldCreator;
import com.grzegorz.mariobros.tools.Timer;
import com.grzegorz.mariobros.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    // Final variables
    public static final float MARIO_VELOCITY_Y = 4f;
    public static final float MARIO_VELOCITY_X = 0.1f;
    public static final float MARIO_MAX_VELOCITY_X = 1.5f;


    // general variables
    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private TextureAtlas atlas;

    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;    // graficzna reprezentacja rzeczy w swiecie?
    private B2WorldCreator creator;

    // Sprites
    private Mario player;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private Music music;

    private boolean theEnd;
    private Timer timer;


    public PlayScreen(MarioBros game){
        atlas = new TextureAtlas("Mario_and_Enemies2.pack");

        this.game = game;
        gameCam = new OrthographicCamera();

        // do ustawiania odpowiedniego widoku
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);

        // hud - punkty, czas, poziom
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);  // TODO dlaczego potrzebne jest skalowanie tutaj
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0,-10), true);  // wektor dla grawitacji, true ze obiekty sa uspione
        b2dr = new Box2DDebugRenderer();

        // tworzy Mario w swiecie gry
        player = new Mario(this);

        // konstruktor ktory przypisuje swiatu (world) bloczki z mapy
        creator = new B2WorldCreator(this);

        world.setContactListener(new WorldContactListener());

//        music = MarioBros.manager.get("Mario_GFX/music/ds3_soundtrack.mp3", Music.class);
        // TODO play music
//        music = Gdx.audio.newMusic(Gdx.files.internal("music/ds3_soundtrack.mp3"));
//        music.setLooping(true);
//        music.setVolume(1);
//        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef iDef){
           itemsToSpawn.add(iDef);
    }

    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class)
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            if (idef.type == CoinAnimation.class)
                items.add(new CoinAnimation(this, idef.position.x, idef.position.y));
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public Mario getPlayer() {
        return player;
    }

    public B2WorldCreator getCreator() {
        return creator;
    }

    @Override
    public void show() {

    }

    public void handleInput(){
        if (player.currentState != Mario.State.DEAD && player.currentState !=Mario.State.GROWING && player.currentState != Mario.State.SLIDING) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.b2body.applyLinearImpulse(new Vector2(0, MARIO_VELOCITY_Y), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= MARIO_MAX_VELOCITY_X) {
                player.b2body.applyLinearImpulse(new Vector2(MARIO_VELOCITY_X, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -MARIO_MAX_VELOCITY_X) {
                player.b2body.applyLinearImpulse(new Vector2(-MARIO_VELOCITY_X, 0), player.b2body.getWorldCenter(), true);
            }
        }
    }

    public void update(float dt){
        // sprawdza wciskane klawisze
        handleInput();
        // sprawdza czy sa jakies przedmioty do stworzenia
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        // when Mario came to the castle
        if (player.getX() > 35.04)
            theEnd = true;

        if (!theEnd)
            player.update(dt);

        if (hud.getTime() == 0 && !player.isDead())
            player.endOfTime();

        if (player.getY() < -1 && !player.isDead())
            player.outOfMap();



        // jezeli wypadnaie poza mape to usun z listy przeciwnikow
        for (Enemy enemy : creator.getEnemnies()){
            if (enemy.getY() < -1) {
                world.destroyBody(enemy.b2body);
                creator.removeEnemy(enemy);
            }
        }

        for (Enemy enemy : creator.getEnemnies()) {
            // metoda update - jesli jest to goomba to uzyta bedzie ta z goomba,
            // jest turtle to z turtle
            enemy.update(dt);
            // set active the enemy when Mario is near
            if (enemy.getX() < player.getX() + 3.5f)
                enemy.b2body.setActive(true);

            // here is where enemies are removed
            if (enemy.isRemoveBody()) {
                world.destroyBody(enemy.b2body);
                creator.removeEnemy(enemy);
            }
        }

        // remove item if it fall out of map
        for (Item item : items){
            if (item.getY() < -1 || (!item.isDestroyed() && item.isToDestroy())) {
                items.removeValue(item, true);
                world.destroyBody(item.getBody());
            }
        }
        for (Item item : items)
            item.update(dt);

        // TODO zrobic odpowiednia funkcje do tego bo za duzo to zajmuje
        for (Brick brick : creator.getBricks()) {
            if (brick.getBumpedBrick() != null) {
                //brick.getBumpedBrick().update(dt);
                if (brick.getBumpedBrick().isToDestroy() && !brick.getBumpedBrick().isDestroyed()) {
                    world.destroyBody(brick.getBumpedBrick().getBody());
                    brick.setTimerNull();
                    brick.setBumpedBrick(null);
                }
            }
            else if(brick.isDoAnimation())
                brick.setBumpedBrick(brick.new BumpedBrick(this, brick.getBody().getPosition().x, brick.getBody().getPosition().y));

            if (brick.getKillingBrick() != null) {
                //brick.getBumpedBrick().update(dt);
                if (brick.getKillingBrick().isToDestroy() && !brick.getKillingBrick().isDestroyed()) {
                    world.destroyBody(brick.getKillingBrick().getBody());
                    brick.setTimerNull();
                    brick.setKillingBrick(null);
                }
            }
            else if(brick.isDoKillingBrick())
                brick.setKillingBrick(brick.new KillingBrick(this, brick.getBody().getPosition().x, brick.getBody().getPosition().y));

            // update killingBrick or bumpedBrick
            if (brick.getKillingBrick() != null)
                brick.getKillingBrick().update(dt);
            if (brick.getBumpedBrick() != null)
                brick.getBumpedBrick().update(dt);

            // animating destroyed brick
            else if (brick.isTurnToPieces() && brick.getPieces().size() < 4) {
                brick.getPieces().add(new PieceOfBrick(this, brick.getBody().getPosition().x  - 4 / MarioBros.PPM,
                        brick.getBody().getPosition().y + 4 / MarioBros.PPM, -1f, 1.5f));
                brick.getPieces().add(new PieceOfBrick(this, brick.getBody().getPosition().x  + 4 / MarioBros.PPM,
                        brick.getBody().getPosition().y + 4 / MarioBros.PPM, 1f, 1.5f));
                brick.getPieces().add(new PieceOfBrick(this, brick.getBody().getPosition().x  - 4 / MarioBros.PPM,
                        brick.getBody().getPosition().y - 4 / MarioBros.PPM, -1f, 0.75f));
                brick.getPieces().add(new PieceOfBrick(this, brick.getBody().getPosition().x  + 4 / MarioBros.PPM,
                        brick.getBody().getPosition().y - 4 / MarioBros.PPM, 1f, 0.75f));
            }

            else if (brick.isTurnToPieces() && brick.getPieces().size() == 4)
                for (int i = 0; i < 4; i++)
                    brick.getPieces().get(i).update(dt);
        }

        if (theEnd) {
            if (hud.getTime() > 0) {
                Hud.addScore(50);
                hud.timeDecrease();
            }
        }
        else
            hud.update(dt);

        if (player.currentState != Mario.State.DEAD && player.currentState != Mario.State.SLIDING && player.getX() < 34)
            gameCam.position.x = player.b2body.getPosition().x + gamePort.getWorldWidth() / 4;

        gameCam.update();
        renderer.setView(gameCam);

    }

    @Override
    public void render(float dt) {
        update(dt);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render map
        renderer.render();

        // render Box2DDebugLines
        b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        if (!theEnd)
            player.draw(game.batch);
        for (Enemy enemy : creator.getEnemnies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);

        // animations for bricks
        for (Brick brick : creator.getBricks()) {
            // hitted by the little Mario
            if (brick.getBumpedBrick() != null)
                brick.getBumpedBrick().draw(game.batch);


                // hitted by the big Mario
            else if (brick.isTurnToPieces())
                for (int i = 0; i < brick.getPieces().size(); i++) {
                    System.out.println(brick.getPieces().size());
                    brick.getPieces().get(i).draw(game.batch);
                }
        }

        game.batch.end();

        // ustawienia aby do batcha zaladowac tylko to co widzi kamera
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // GAME OVER
        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        // GAME WON
        else if (gameWon()){
            game.setScreen(new GameWonScreen(game));
            dispose();
        }
    }


    public boolean gameOver(){
        return player.currentState == Mario.State.DEAD && player.getStateTimer() > 3;
    }


    // metoda sprawdzajaca czy mozna juz przelaczyc screen'a
    // theEnd uzywane jest jeszcze po to by dac znac Hud'owi czy ma zmieniac czas na punkty
    private boolean gameWon(){
        boolean itsTime = false;

        if (timer == null && hud.getTime() <= 0) {
            timer = new Timer(3000);
            Thread thread = new Thread(timer);
            thread.start();
        }
        if (timer != null && timer.isItTime())
            itsTime = true;
        return theEnd && itsTime;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }


    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
