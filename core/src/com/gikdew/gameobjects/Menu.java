package com.gikdew.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.gameworld.GameWorld.GameState;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.tweenaccessors.SpriteAccessor;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.ui.SimpleButton;

import java.util.ArrayList;

import C.C;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class Menu {
    private Vector2 velocity;

    private GameWorld world;
    private Rectangle rectangle;

    private boolean goingUp = false;
    private boolean goingDown = false;
    private boolean isSet = true;
    private boolean moving = false;
    private TweenManager manager;

    private Sprite spriteRectangle, fontSprite;
    private float duration = 0.5f;
    private float delay = 0f;

    private ArrayList<SimpleButton> menuButtons;
    private TweenCallback cbIn, cbOut;

    private SimpleButton playButton, rankButton, shareButton;
    private int buttonSize = 90;
    private Value r1 = new Value();
    private Value r6 = new Value();
    private Value r7 = new Value();
    private Value r5 = new Value();

    public Menu(GameWorld world, float x, float y, float width, float height) {
        this.world = world;

        // GETTING ASSETS FROM ASSETLOADER
        rectangle = new Rectangle(x, y, width, height);
        spriteRectangle = new Sprite(AssetLoader.square);
        spriteRectangle.setBounds(x, y, width, height);
        fontSprite = new Sprite(AssetLoader.square);
        fontSprite.setBounds(x, y, width, height);

        // SETUP TWEENS FOR THE ANIMATION OF THE MENU
        setupTween();

        // MENU BUTTONS
        menuButtons = new ArrayList<SimpleButton>();

        playButton = new SimpleButton(world, 0, world.gameHeight - buttonSize
                * 3, world.gameWidth, buttonSize, world.parseColor(
                C.playButtonMenu.toString(), 0.85f), "Play");
        rankButton = new SimpleButton(world, 0, world.gameHeight - buttonSize
                * 2, world.gameWidth, buttonSize, world.parseColor(
                C.scoreButtonMenu.toString(), 0.85f), "Scores >");
        shareButton = new SimpleButton(world, 0, world.gameHeight - buttonSize
                * 1, world.gameWidth, buttonSize, world.parseColor(
                C.shareButtonMenu.toString(), 0.85f), "Share");

        menuButtons.add(playButton);
        menuButtons.add(rankButton);
        menuButtons.add(shareButton);

        // VALUES FOR ANIMATIONS OF THE MENU
        r1.setValue(-100);
        r5.setValue(world.gameHeight + width * 2);
        r6.setValue(world.gameHeight + width * 2);
        r7.setValue(world.gameHeight + width * 2);
    }

    public void update(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        manager.update(delta);
        playButton.update(delta);
        rankButton.update(delta);
        shareButton.update(delta);
        // achieveButton.update(delta);

        // Gdx.app.log("Sprite Alpha", fontSprite.getColor().a + "");
    }

    public void render(SpriteBatch batch, ShaderProgram fontShader,
                       ShapeRenderer sR) {
        batch.setShader(fontShader);

        if (isSet) {
            spriteRectangle.setColor(C.backColor);
            spriteRectangle.draw(batch);
            for (int i = 0; i < menuButtons.size(); i++) {
                menuButtons.get(i).draw(batch, sR);
            }
            // spritelogo.draw(batch);
            batch.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sR.begin(ShapeType.Filled);
            sR.setColor(world.parseColor(C.nameBackMenu.toString(), 0.85f));
            sR.rect(0, r1.getValue() - buttonSize * 1.1f, world.gameWidth,
                    buttonSize * 3);
            sR.end();
            batch.begin();
            AssetLoader.font1.setColor(Color.WHITE);
            AssetLoader.font1.draw(batch, C.gameName, (world.gameWidth / 2)
                    - (16 * (C.gameName.length())), r1.getValue());
            batch.setShader(null);
            if (world.currentState == GameState.GAMEOVER) {
                batch.setShader(fontShader);
                AssetLoader.font2.drawWrapped(batch,
                                              "Score: " + world.getScore(), 0,
                                              r5.getValue(), world.gameWidth,
                                              BitmapFont.HAlignment.CENTER);
                AssetLoader.font2.drawWrapped(batch, "Highscore: " + AssetLoader.getHighScore(),
                                              0, r6.getValue(), world.gameWidth,
                                              BitmapFont.HAlignment.CENTER);
                AssetLoader.font2.drawWrapped(batch,
                                              "Games Played: " + AssetLoader
                                                      .getGamesPlayed(), 0,
                                              r7.getValue(), world.gameWidth,
                                              BitmapFont.HAlignment.CENTER);
                batch.setShader(null);
            }
            fontSprite.setColor(C.backColor.r, C.backColor.g, C.backColor.b,
                                fontSprite.getColor().a);
            fontSprite.draw(batch);
        }
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void stop() {
        goingUp = false;
        goingDown = false;
        velocity.y = 0;
    }

    public boolean isMoving() {
        return goingDown || goingUp;
    }

    public ArrayList<SimpleButton> getMenuButtons() {
        return menuButtons;
    }

    public void fadeOutAll() {
        // world.getCenter().backToPos();
        // world.getCenter().clickPlay();
        // isSet = false;
        moving = true;
        Tween.to(spriteRectangle, SpriteAccessor.ALPHA, duration).target(0)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
             .start(manager);
        Tween.to(fontSprite, SpriteAccessor.ALPHA, duration).target(1)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 1f)
             .setCallback(cbOut).setCallbackTriggers(TweenCallback.COMPLETE)
             .start(manager);

        Tween.to(r1, -1, duration).target(-buttonSize * 2)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.2f).start(manager);

        Tween.to(r7, -1, duration + .2f)
             .target(world.gameHeight + playButton.bounds.width * 2)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.2f).start(manager);

        Tween.to(r6, -1, duration + .2f)
             .target(world.gameHeight + playButton.bounds.width * 2)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.25f).start(manager);

        Tween.to(r5, -1, duration + .2f)
             .target(world.gameHeight + playButton.bounds.width * 2)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.3f).start(manager);

        playButton.fadeOut(duration + .1f, duration, delay, false, "");
        // achieveButton.fadeOut(duration, delay);
        rankButton.fadeOut(duration, duration, delay, false, "");
        shareButton.fadeOut(duration - .1f, duration, delay, false, "");
        world.getCenter().clickPlay();
    }

    public void fadeInAll() {
        isSet = true;
        moving = true;
        spriteRectangle.setAlpha(0);
        Tween.to(spriteRectangle, SpriteAccessor.ALPHA, duration + .2f)
             .target(1).ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0)
             .setCallback(cbIn).setCallbackTriggers(TweenCallback.COMPLETE)
             .start(manager);
        fontSprite.setAlpha(1);
        Tween.to(fontSprite, SpriteAccessor.ALPHA, duration + .2f).target(0)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0)
             .start(manager);
        Tween.to(r1, -1, duration - .2f).target(buttonSize * 1.1f)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.2f).start(manager);

        r5.setValue(world.gameHeight + playButton.bounds.width * 2);
        r6.setValue(world.gameHeight + playButton.bounds.width * 2);
        r7.setValue(world.gameHeight + playButton.bounds.width * 2);

        Tween.to(r5, -1, duration).target(world.gameHeight / 2 - 90 - 10)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f).delay(0f)
             .start(manager);
        Tween.to(r6, -1, duration).target(world.gameHeight / 2 - 45f)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.1f).start(manager);
        Tween.to(r7, -1, duration).target(world.gameHeight / 2 + 10)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, 0f)
             .delay(.2f).start(manager);

        playButton.fadeIn(duration - .1f);
        rankButton.fadeIn(duration);
        shareButton.fadeIn(duration + .1f);
    }

    public Sprite getSpriteRectangle() {
        return spriteRectangle;
    }

    private void setupTween() {
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        manager = new TweenManager();

        // CALLBACKS
        cbOut = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                isSet = false;
                world.currentState = GameState.READY;
                moving = false;
                world.restart();
            }
        };
        cbIn = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                isSet = true;
                moving = false;
            }
        };

        // FIRST TWEEN
        Tween.to(fontSprite, SpriteAccessor.ALPHA, duration).target(0)
             .ease(TweenEquations.easeInOutQuad).repeatYoyo(0, .4f)
             .start(manager);

    }

    public boolean isSet() {
        return isSet;
    }

    public boolean isMoving1() {
        return moving;
    }

    public void clickLeaderboards() {
        playButton
                .fadeOut(duration + .1f, duration - .1f, -.1f, true, "< Back");
        rankButton.fadeOut(duration, duration, 0, true, "Achievements");
        shareButton.fadeOut(duration - .1f, duration + .1f, +.1f, true,
                            "Leaderboards");
    }

    public void clickBack() {
        playButton.fadeOut(duration + .1f, duration - .1f, -.1f, true, "Play");
        rankButton.fadeOut(duration, duration, 0, true, "Scores >");
        shareButton
                .fadeOut(duration - .1f, duration + .1f, +.1f, true, "Share");
    }
}
