package com.gikdew.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.gikdew.gameobjects.BallManager;
import com.gikdew.gameobjects.Center;
import com.gikdew.gameobjects.Menu;
import com.gikdew.gameworld.GameWorld.GameState;
import com.gikdew.helpers.AssetLoader;

import C.C;

public class GameRenderer {

    private GameWorld world;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Menu menu;
    private Center center;
    private BallManager ballManager;

    private ShaderProgram fontShader;

    public GameRenderer(GameWorld world, int gameWidth, int gameHeight) {
        this.world = world;
        cam = new OrthographicCamera();
        cam.setToOrtho(true, gameWidth, gameHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        fontShader = new ShaderProgram(Gdx.files.internal("font.vert"),
                Gdx.files.internal("font.frag"));
        if (!fontShader.isCompiled()) {
            Gdx.app.error("fontShader",
                    "compilation failed:\n" + fontShader.getLog());
        }

        initObjects();
    }

    private void initObjects() {
        menu = world.getMenu();
        center = world.getCircle();
        ballManager = world.getBallManager();
    }

    public void render(float delta, float runTime) {
        Gdx.gl.glClearColor(C.backColor.r, C.backColor.g, C.backColor.b,
                C.backColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // IF YOU WANNA DRAW THE SPAWN POINTS
        // shapeRenderer.begin(ShapeType.Filled);
        // shapeRenderer.setColor(Color.WHITE);
        // for (int i = 0; i < world.getBallManager().getPoints().size(); i++) {
        // shapeRenderer.circle(world.getBallManager().getPoints().get(i).x,
        // world.getBallManager().getPoints().get(i).y, 2);
        // }
        //
        // shapeRenderer.end();

        batch.begin();
        center.render1(batch, shapeRenderer);
        ballManager.render(batch, shapeRenderer);
        center.render(batch, shapeRenderer);

        if (world.isRunning()) {
            drawScore();
            ballManager.render1(batch, shapeRenderer);
        } else if (world.isReady()) {
            drawScore();
        } else if (world.isGameover()) {
            center.render(batch, shapeRenderer);
            drawMenu();
        } else if (world.isMenu()) {
            drawMenu();
            center.render(batch, shapeRenderer);
        }

        batch.end();

    }

    public void drawHScore() {
        batch.setShader(fontShader);
        batch.setShader(null);
    }

    private void drawMenu() {
        drawButtons();
        batch.setShader(fontShader);
        batch.setShader(null);
    }

    private void drawButtons() {
        menu.render(batch, fontShader, shapeRenderer);
    }

    private void drawScore() {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(world.parseColor(C.colorB1.toString(), 0.5f));
        shapeRenderer.rect(0, world.getCenter().getyPointTap().getValue() - 5,
                world.gameWidth, 60);
        shapeRenderer.end();
        batch.begin();
        batch.setShader(fontShader);
        AssetLoader.font.setColor(Color.WHITE);

        // DEBUG
        // AssetLoader.font2.draw(batch, world.getBallManager().getSegundos() +
        // "", 100, 100);
        if (world.currentState == GameState.RUNNING) {
            AssetLoader.font2.draw(batch, "" + world.getScore(), 50, world
                    .getCenter().getyPointTap().getValue());

            AssetLoader.font2.draw(batch, "Level " + world.getLevel(), 500,
                    world.getCenter().getyPointTap().getValue());

        } else {
            AssetLoader.font2.draw(batch, "Tap to start", 50, world.getCenter()
                    .getyPointTap().getValue());
        }

        batch.setShader(null);
    }
}
