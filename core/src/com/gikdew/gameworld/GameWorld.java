package com.gikdew.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.gikdew.gameobjects.BallManager;
import com.gikdew.gameobjects.Center;
import com.gikdew.gameobjects.Menu;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.shapes.ActionResolver;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class GameWorld {

    public float gameWidth;
    public float gameHeight;
    public GameState currentState;
    public ActionResolver actionResolver;
    private Menu menu;
    private int score = 0;
    private TweenCallback cb;
    private TweenManager manager;
    private Value distance = new Value();
    private Center center;
    // private Dot dot;
    private BallManager ballManager;
    private Value second1 = new Value();
    private int counterLevel = 0;
    private int level;

    public GameWorld(final ActionResolver actionResolver, float gameWidth,
                     float gameHeight) {

        this.actionResolver = actionResolver;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        menu = new Menu(this, 0, 0, gameWidth, gameHeight);
        center = new Center(this, gameWidth / 2, gameHeight / 2, 50, actionResolver);
        // dot = new Dot(this, gameWidth / 2, gameHeight / 2, 25);
        ballManager = new BallManager(this);
        currentState = GameState.MENU;
        distance.setValue(0);
        second1.setValue(0);
        menu.fadeInAll();

        actionResolver.viewAd(false);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                actionResolver.showOrLoadInterstital();
                distance.setValue(0);
            }
        };
    }

    public void update(float delta) {
        manager.update(delta);
        menu.update(delta);
        center.update(delta);
        collisions();
        switch (currentState) {
            case READY:
                break;
            case RUNNING:
                ballManager.update(delta);
                break;
            case GAMEOVER:
                ballManager.update(delta);
                break;
            default:

                break;
        }
    }

    private void collisions() {
        ballManager.collisions();
    }

    public Menu getMenu() {
        return menu;
    }

    public int getScore() {
        return score;
    }

    public void setScore0() {
        ballManager.reset();
        counterLevel = 0;
        level = 0;
        AssetLoader.addGamesPlayed();
        int gamesPlayed = AssetLoader.getGamesPlayed();
        // GAMES PLAYED ACHIEVEMENTS!
        menu.fadeInAll();
        actionResolver.submitScore(score);
        actionResolver.submitGamesPlayed(gamesPlayed);

        if (score > AssetLoader.getHighScore()) {
            AssetLoader.setHighScore(score);
        } else {
            // ADS
            if (Math.random() < 0.8f) {
                Tween.to(distance, -1, .9f).target(1).repeatYoyo(0, 0)
                        .setCallback(cb)
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .ease(TweenEquations.easeInOutBounce).start(manager);
            }
        }

        checkAchievements();
    }

    private void checkAchievements() {
        if (actionResolver.isSignedIn()) {
            if (score >= 5)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQAw");
            if (score >= 10)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQBQ");
            if (score >= 15)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQDQ");
            if (score >= 19)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQCQ");
            if (score >= 25)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQBg");
            if (score >= 30)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQCg");
            if (score >= 50)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQBw");
            if (score >= 75)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQCA");
            if (score >= 100)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQCw");
            if (score >= 150)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQDA");

            int gamesPlayed = AssetLoader.getGamesPlayed();
            // GAMES PLAYED
            if (gamesPlayed >= 10)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQDg");
            if (gamesPlayed >= 25)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQDw");
            if (gamesPlayed >= 50)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQEA");
            if (gamesPlayed >= 75)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQEQ");
            if (gamesPlayed >= 100)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQEg");
            if (gamesPlayed >= 150)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQEw");
            if (gamesPlayed >= 200)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQFA");
            if (gamesPlayed >= 300)
                actionResolver.unlockAchievementGPGS("CgkI4N3Y4ckOEAIQFQ");
        }
    }

    public void addScore(int increment) {
        score += increment;
        counterLevel++;
        if (counterLevel > 4) {
            counterLevel = 0;
            level++;
            if (level < 5) {
                getBallManager().createPoints((int) Math.pow(3, level));
            }
        }

        getCenter().addPointCounter();
        Gdx.app.log("Score", score + "");
        // ACHIEVEMENTS

        if (actionResolver.isSignedIn()) {
            if (score >= 5)
                actionResolver.unlockAchievementGPGS("CgkIxYr7wJ0eEAIQAw");

        }
    }

    public void restart() {
        score = 0;
        counterLevel = 0;
        getBallManager().reset();
    }

    public boolean isReady() {
        return currentState == GameState.READY;
    }

    public boolean isGameover() {
        return currentState == GameState.GAMEOVER;
    }

    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

    public boolean isMenu() {
        return currentState == GameState.MENU;
    }

    public Center getCircle() {
        return center;
    }

    public Center getCenter() {
        return center;
    }

    public BallManager getBallManager() {
        return ballManager;
    }

    public int getLevel() {
        return level;
    }

    // Helper functions
    public Color parseColor(String hex, float alpha) {
        String hex1 = hex;
        if (hex1.indexOf("#") != -1) {
            hex1 = hex1.substring(1);
        }
        Color color = Color.valueOf(hex1);
        color.a = alpha;
        return color;
    }

    public Color parseColorS(Color color1, float f) {
        Color color2 = Color.CYAN;
        color2.set(color1.r, color1.g, color1.b, 0f);

        color2.set(color1.r, color1.g, color1.b, f);
        return color2;
    }

    public enum GameState {
        READY, RUNNING, GAMEOVER, MENU
    }

}
