package com.gikdew.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.tweenaccessors.SpriteAccessor;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class SimpleButton {

    private float x, y, initialY;

    public Rectangle bounds;
    public TweenManager manager;
    private Sprite sprite;

    private boolean isPressed = false;
    private Color color, initialColor;
    private String text;
    private Value yValue = new Value();
    private TweenCallback cb;
    private GameWorld world;

    public SimpleButton(GameWorld world, float x, float y, float width,
                        float height, Color color, String text) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.initialY = y;

        this.color = color;
        this.text = text;
        this.initialColor = color;
        sprite = new Sprite(AssetLoader.square);
        sprite.setBounds(x, y, width, height);
        setupTween();
        bounds = new Rectangle(x, y, width, height);

    }

    private void setupTween() {
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

    }

    public boolean isClicked(int screenX, int screenY) {
        return bounds.contains(screenX, screenY);
    }

    public void update(float delta) {
        bounds.x = x;
        bounds.y = yValue.getValue();
        manager.update(delta);
    }

    public void draw(SpriteBatch batcher, ShapeRenderer sR) {
        batcher.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sR.begin(ShapeType.Filled);
        if (!isPressed) {
            sR.setColor(color);
        } else {
            sR.setColor(color.r, color.g, color.b, color.a - .1f);
        }
        sR.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        sR.end();
        batcher.begin();
        AssetLoader.font1.draw(batcher, text, bounds.x + 30, bounds.y + 8);
    }

    public boolean isTouchDown(int screenX, int screenY) {
        if (bounds.contains(screenX, screenY)) {
            isPressed = true;
            return true;
        }

        return false;
    }

    public void fadeOut(final float duration, final float duration2,
                        final float delay, final boolean b, final String string) {

        Tween.to(sprite, SpriteAccessor.ALPHA, duration).target(0).delay(delay)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
                .start(manager);

        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (b) {
                    // Gdx.app.log("Finish", "FISNIH");
                    text = string;
                    if (text.equals("< Back")) {
                        color = world.parseColor(
                                C.C.scoreButtonMenu.toString(), 0.85f);
                    } else if (text.equals("Achievements")) {
                        color = world.parseColor(
                                C.C.scoreButtonMenu.toString(), 0.75f);
                    } else if (text.equals("Leaderboards")) {
                        color = world.parseColor(
                                C.C.scoreButtonMenu.toString(), 0.65f);
                    } else {
                        color = initialColor;
                    }
                    yValue.setValue(world.gameHeight / 4 + initialY);
                    Tween.to(yValue, -1, duration2).target(initialY)
                            .ease(TweenEquations.easeInOutQuad).delay(delay)
                            .repeatYoyo(0, .4f).start(manager);

                }

            }
        };
        Tween.to(yValue, -1, duration).target(world.gameHeight / 4 + initialY)
                .setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
                .start(manager);

    }

    public void fadeIn(float duration) {
        Tween.to(sprite, SpriteAccessor.ALPHA, duration).target(1)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
                .start(manager);
        yValue.setValue(world.gameHeight / 4 + initialY);
        Tween.to(yValue, -1, duration + .2f).target(initialY)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
                .start(manager);
    }

    public boolean isTouchUp(int screenX, int screenY) {

        if (bounds.contains(screenX, screenY) && isPressed) {
            isPressed = false;
            return true;
        }

        // Whenever a finger is released, we will cancel any presses.
        isPressed = false;
        return false;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

}