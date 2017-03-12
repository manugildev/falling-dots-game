package com.gikdew.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.gameworld.GameWorld.GameState;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.helpers.ColorManager;
import com.gikdew.shapes.ActionResolver;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class Center {

    private GameWorld world;
    private Vector2 position;
    private float radius, radius1, radius2;

    private Sprite sprite, sprite1, sprite2, spriteBack;

    private TweenCallback cb2, cb;
    private TweenManager manager;
    private Value r = new Value();
    private Value r1 = new Value();
    private Value r2 = new Value();
    private Value r3 = new Value();
    private Value yPointTap = new Value();

    private Circle colCircle;
    private float duration = .2f;

    private int shape = 0;
    private Sprite pointsSprite;
    private int counter = 0;

    private ArrayList<String> colors = new ArrayList<String>();

    private ColorManager colorManager;
    private Value yValue = new Value();
    private ActionResolver actionResolver;

    public Center(final GameWorld world, float x, float y, int radius, final ActionResolver actionResolver) {
        this.world = world;
        this.position = new Vector2(x, y);
        this.radius = radius;
        this.colorManager = new ColorManager();
        this.actionResolver = actionResolver;
        radius1 = radius - 10;
        radius2 = radius - 20;
        r.setValue(radius2);
        r1.setValue(radius1);
        r2.setValue(radius);
        yValue.setValue(y);

        yPointTap.setValue(-100);
        sprite = new Sprite(AssetLoader.dot);
        sprite.setBounds(position.x - radius2, position.y - radius2,
                radius2 * 2, radius2 * 2);
        sprite.setOrigin(radius2 / 2, radius2 / 2);

        sprite1 = new Sprite(AssetLoader.dot);
        sprite1.setBounds(position.x - radius1, position.y - radius1,
                radius1 * 2, radius1 * 2);
        sprite1.setOrigin(radius1 / 2, radius1 / 2);
        sprite2 = new Sprite(AssetLoader.dot);
        sprite2.setBounds(position.x - radius, position.y - radius, radius * 2,
                radius * 2);
        sprite2.setOrigin(r2.getValue(), r2.getValue());

        spriteBack = new Sprite(AssetLoader.dot);
        spriteBack.setBounds(position.x - radius, position.y - radius,
                radius * 2, radius * 2);
        spriteBack.setOrigin(radius / 2, radius / 2);

        pointsSprite = new Sprite(AssetLoader.dot);
        pointsSprite.setBounds(position.x - radius, position.y - radius * 2,
                radius * 2, radius * 2);

        pointsSprite.setOriginCenter();
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

        cb2 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                world.currentState = GameState.RUNNING;
                world.restart();

            }
        };
        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                actionResolver.viewAd(true);

            }
        };

        Tween.to(r, -1, 2.55f).target(radius2 - 2).repeatYoyo(1000000, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);

        Tween.to(r1, -1, 3f).target(radius1 - 3).repeatYoyo(1000000, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);

        Tween.to(r2, -1, 3.55f).target(radius - 6).repeatYoyo(1000000, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);

        colCircle = new Circle();

        colors.add(C.C.colorB2.toString());
        colors.add(C.C.colorB1.toString());

    }

    public void update(float delta) {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        colorManager.update(delta);

        manager.update(delta);
        // Gdx.app.log("R", r.getValue() + "");
        radius = r2.getValue();
        position.y = yValue.getValue();
        pointsSprite.setBounds(position.x - r3.getValue(), yValue.getValue()
                - r3.getValue(), r3.getValue() * 2, r3.getValue() * 2);
        // pointsSprite.setOriginCenter();
        sprite.setBounds(position.x - r.getValue(),
                yValue.getValue() - r.getValue(), r.getValue() * 2,
                r.getValue() * 2);
        sprite.setOriginCenter();
        sprite1.setBounds(position.x - r1.getValue(),
                yValue.getValue() - r1.getValue(), r1.getValue() * 2,
                r1.getValue() * 2);
        sprite1.setOriginCenter();
        sprite2.setBounds(position.x - r2.getValue(),
                yValue.getValue() - r2.getValue(), r2.getValue() * 2,
                r2.getValue() * 2);
        sprite2.setOriginCenter();
        spriteBack.setBounds(position.x - r2.getValue(),
                yValue.getValue() - r2.getValue(), r2.getValue() * 2,
                r2.getValue() * 2);
        spriteBack.setOriginCenter();

        // angle++;
        colCircle
                .set(new Vector2(position.x, yValue.getValue()), r2.getValue());

    }

    public void render1(SpriteBatch batch, ShapeRenderer sR) {
        pointsSprite.setColor(world.parseColor("FFFFFF", 0.15f));
        pointsSprite.draw(batch);

    }

    public void render(SpriteBatch batch, ShapeRenderer sR) {

        spriteBack.setColor(C.C.backColor);
        spriteBack.draw(batch);
        sprite2.setColor(world.parseColor(colorManager.getColor().toString(),
                0.5f));
        sprite2.draw(batch);
        sprite1.setColor(world.parseColor(colorManager.getColor().toString(),
                0.8f));
        sprite1.draw(batch);
        sprite.setColor(world
                .parseColor(colorManager.getColor().toString(), 1f));
        sprite.draw(batch);

    }

    public void addPointCounter() {
        counter++;
        if (counter == 5) {
            circlePointsEffect10((r2.getValue() + (counter * 25 * 2)));
            int spawns = (int) Math.pow(3, world.getScore() / 5);
            //Gdx.app.log("Spawns", spawns + "");
            // world.getBallManager().createPoints(spawns);
            if (spawns > 8) {
                // world.getBallManager().addBall(1f);
            }
            counter = 0;

        } else {

            circlePointsEffect((r2.getValue() + (counter * 2 * 25)));
        }

    }

    public float getRadius() {
        return radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getColCircle() {
        return colCircle;
    }

    public void clickPlay() {
        yPointTap.setValue(-100);
        Tween.to(yPointTap, -1, duration).target(5).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.4f);
    }

    public void clickReady() {
        yPointTap.setValue(5);
        Tween.to(yPointTap, -1, duration).target(-100).repeatYoyo(1, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(0)
                .setCallbackTriggers(TweenCallback.END).setCallback(cb2);
    }

    public Value getyPointTap() {
        return yPointTap;
    }

    public void setYPointTap(int i) {
        yPointTap.setValue(i);
    }

    public void clickBye() {
        yPointTap.setValue(5);
        Tween.to(yPointTap, -1, duration).target(-100).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.3f)
                .setCallbackTriggers(TweenCallback.END);
        Tween.to(r, -1, duration + .4f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.5f)
                .setCallbackTriggers(TweenCallback.END);
        Tween.to(r1, -1, duration + .4f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.5f)
                .setCallbackTriggers(TweenCallback.END);
        Tween.to(r2, -1, duration + .4f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.5f)
                .setCallbackTriggers(TweenCallback.END);
        Tween.to(r3, -1, duration + .4f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(.5f)
                .setCallbackTriggers(TweenCallback.END);
        Tween.to(yValue, -1, duration + .4f).target(world.gameHeight / 2)
                .repeatYoyo(0, 0).ease(TweenEquations.easeInOutSine)
                .start(manager).delay(0).setCallbackTriggers(TweenCallback.END);
        world.currentState = GameState.GAMEOVER;
        circlePointsEffect10(0);
        counter = 0;
        actionResolver.viewAd(false);
    }

    public int getShape() {
        return shape;
    }

    public void circlePointsEffect(float target) {
        Tween.to(r3, -1, .25f).target(target).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(0)
                .setCallbackTriggers(TweenCallback.END);
    }

    public void circlePointsEffect10(float target) {

        Tween.to(r3, -1, .25f).target(10).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager).delay(0)
                .setCallbackTriggers(TweenCallback.END);

    }

    public void fallDownEffect() {
        yValue.setValue(world.gameHeight / 2);
        Tween.to(yValue, -1, .5f).target(world.gameHeight - 200)
                .repeatYoyo(0, 0).ease(TweenEquations.easeInOutSine)
                .start(manager).delay(.5f).setCallback(cb)
                .setCallbackTriggers(TweenCallback.COMPLETE);

    }

    public float getyValue() {
        return yValue.getValue();
    }

    public void stop() {
        shape = 0;
        colorManager.twoToOne();
    }

    public void click() {
        if (shape == 0) {
            shape = 1;
            colorManager.oneToTwo();
        }
    }
}
