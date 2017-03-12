package com.gikdew.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import java.util.ArrayList;

import C.C;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class Ball {

    int w;
    private Vector2 position, velocity, point;
    private Sprite sprite;
    private GameWorld world;
    private float radius, initialRadius;
    private Circle colCircle;
    private Value alpha = new Value();
    private Value radiusValue = new Value();
    private TweenManager manager;
    private TweenCallback cb, cb1;
    private boolean dead = false;
    private boolean growing = false;
    private ArrayList<String> colors = new ArrayList<String>();
    private String color;
    private int mode;
    private boolean inCenter = false;

    private int savedVelocity = 0;

    private ArrayList<Sprite> tail = new ArrayList<Sprite>();

    public Ball(GameWorld world, Vector2 position, int radius) {
        this.position = new Vector2(position.x - radius, position.y - radius);
        this.world = world;
        this.point = position;
        this.radius = radius;
        this.initialRadius = radius;
        sprite = new Sprite(AssetLoader.dot);
        sprite.setBounds(position.x - radius, position.y - radius * 2,
                radius * 2, radius * 2);
        sprite.setOriginCenter();

        this.velocity = new Vector2(world.getCenter().getPosition().x
                - position.x, world.getCenter().getPosition().y - position.y);
        this.velocity = new Vector2(this.velocity.x / this.velocity.len(),
                this.velocity.y / this.velocity.len()).scl((float) 600);
        savedVelocity = 600;
        colCircle = new Circle();

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        alpha.setValue(0.2f);
        radiusValue.setValue(this.radius);
        colors.add(C.colorB1.toString());
        colors.add(C.colorB2.toString());

        color = colors.get((int) Math.floor(Math.random() * colors.size()));
        if (color.equals(colors.get(0))) {
            setMode(0);
        } else if (color.equals(colors.get(1))) {
            setMode(1);
        }

        for (int i = 0; i < 100; i++) {
            tail.add(new Sprite(AssetLoader.dot));
        }
    }

    public void update(float delta) {

        manager.update(delta);
        if (!world.getMenu().isSet()) {
            position.add(velocity.cpy().scl(delta));
        }

        radius = radiusValue.getValue();
        sprite.setOriginCenter();

        sprite.scale(w);
        sprite.setPosition(position.cpy().x, position.cpy().y);
        sprite.setScale(radiusValue.getValue() / initialRadius);
        colCircle.set(sprite.getX() + radius, sprite.getY() + radius, radius);

    }

    public void render(SpriteBatch batch, ShapeRenderer sR) {
        batch.end();

        sR.begin(ShapeType.Filled);

        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (inCenter) {
            sR.setColor(world.parseColor(color, alpha.getValue() - 0.7f));

            sR.rectLine(point.x, point.y, world.getCenter().getPosition().x,
                    world.getCenter().getyValue(), radius * 2);
            sR.setColor(Color.WHITE);
        } else if (!isGrowing()) {
            sR.setColor(world.parseColor(color, alpha.getValue() - 0.7f));

            sR.rectLine(point.x, point.y, position.x + radius, position.y
                    + radius, radius * 2);
            sR.setColor(Color.WHITE);
        }

        sR.end();
        batch.begin();

        sprite.setColor(world.parseColor(color, !isDead() ? alpha.getValue()
                : alpha.getValue()));
        sprite.draw(batch);
    }

    public Circle getColCircle() {
        return colCircle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        // Gdx.app.log("Position", this.position.toString());
        reset();
        this.position = new Vector2(position.cpy().x - radius, position.cpy().y
                - radius);
        this.point = position.cpy();

        velocity.set(new Vector2(
                world.getCenter().getPosition().x - position.x, world
                .getCenter().getyValue() - position.y));
        velocity = new Vector2(velocity.x / velocity.len(), velocity.y
                / velocity.len()).scl(savedVelocity);

    }

    public void setPosition1(Vector2 position) {
        this.position = new Vector2((int) (position.cpy().x - radius),
                (int) (position.cpy().y - radius));
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity.cpy();
    }

    public void die(final Vector2 vector2) {
        growing = true;
        dead = true;
        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                world.setScore0();
                radiusValue.setValue(initialRadius);
                radius = radiusValue.getValue();
                setPosition(vector2);
                alpha.setValue(0.8f);
                dead = false;

            }
        };
        Tween.to(alpha, -1, 1f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
        Tween.to(radiusValue, -1, 1f).target(1000).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).setCallback(cb)
                .setCallbackTriggers(TweenCallback.COMPLETE).start(manager);

    }

    public void dieCenter(final Vector2 vector2, Vector2 pos) {

        velocity.setZero();
        setPosition1(new Vector2((int) pos.x, (int) pos.y));

        inCenter = true;
        // Gdx.app.log("Pos", pos.toString());
        cb1 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                radiusValue.setValue(initialRadius);
                radius = radiusValue.getValue();
                setPosition(vector2);
                inCenter = false;
                alpha.setValue(0.2f);
                velocity.setZero();
                dead = false;

            }
        };
        // Tween.to(alpha, -1, .3f).target(0).repeatYoyo(0, 0)
        // .ease(TweenEquations.easeOutSine).start(manager);
        Tween.to(radiusValue, -1, .3f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).setCallback(cb1)
                .setCallbackTriggers(TweenCallback.COMPLETE).start(manager);

        if(getMode()==1){
            AssetLoader.sound1.play();
        }else{
            AssetLoader.sound2.play();
        }

    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean b) {
        dead = b;
    }

    public boolean isInCenter() {
        return (int) (position.x + initialRadius) == (int) world.getCenter()
                .getPosition().x
                && (int) (position.y + initialRadius) == (int) world
                .getCenter().getPosition().y;
    }

    public void setVelocityZero() {
        velocity.set(0, 0);
    }

    public void reset() {
        dead = false;
        alpha.setValue(0.8f);
        radiusValue.setValue(initialRadius);
        radius = radiusValue.getValue();
        growing = false;
        color = colors.get((int) Math.floor(Math.random() * colors.size()));
        if (color.equals(colors.get(0))) {
            setMode(0);
        } else if (color.equals(colors.get(1))) {
            setMode(1);
        }
    }

    public void printInfo(int i) {
        Gdx.app.log("ID: ", "" + i);
        Gdx.app.log("Position: ", position.toString());
        Gdx.app.log("Velocity: ", velocity.toString());
        Gdx.app.log("Radius: ", radius + "");
        Gdx.app.log("Sprite", sprite.toString());
    }

    public boolean isGrowing() {
        return growing;
    }

    public void dieGameOver() {

        cb1 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                radiusValue.setValue(initialRadius);
                radius = radiusValue.getValue();
                alpha.setValue(0.8f);
                dead = false;
                setPosition(new Vector2(-30, -30));
                velocity.setZero();

            }
        };
        Tween.to(alpha, -1, .5f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
        Tween.to(radiusValue, -1, .5f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).setCallback(cb1)
                .setCallbackTriggers(TweenCallback.COMPLETE).start(manager);
    }

    public String getColor() {
        return color;
    }

    public void dieFirst(final Vector2 vector2) {
        growing = true;
        dead = true;
        velocity.setZero();
        colCircle = new Circle();
        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                radiusValue.setValue(initialRadius);
                growing = false;
                setPosition(vector2);
                radius = radiusValue.getValue();
                alpha.setValue(0.8f);
                dead = false;

            }
        };

        Tween.to(alpha, -1, .3f * 2).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
        Tween.to(radiusValue, -1, .3f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).setCallback(cb)
                .setCallbackTriggers(TweenCallback.COMPLETE).start(manager);
        printInfo(0);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

}
