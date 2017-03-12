package com.gikdew.helpers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.gameworld.GameWorld.GameState;
import com.gikdew.shapes.ActionResolver;
import com.gikdew.ui.SimpleButton;

import java.util.ArrayList;

public class InputHandler implements InputProcessor {

    private GameWorld world;
    private ArrayList<SimpleButton> menuButtons;
    private float scaleFactorX;
    private float scaleFactorY;

    private ActionResolver actionResolver;

    public InputHandler(GameWorld world, float scaleFactorX,
                        float scaleFactorY, ActionResolver actionResolver) {
        this.world = world;
        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;
        menuButtons = world.getMenu().getMenuButtons();
        this.actionResolver = actionResolver;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (world.isRunning()) {
            if (keycode == Keys.SPACE) {

                world.getCenter().click();
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (world.isRunning()) {
            if (keycode == Keys.SPACE) {
                world.getCenter().stop();
            }
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        if (world.isReady()) {
            world.getCenter().clickReady();
        } else if (world.isRunning()) {
            if (screenX < world.gameWidth / 2 && !world.getMenu().isMoving1()) {
                world.getCenter().click();
            } else {
                world.getCenter().click();
            }

        } else if ((world.isMenu() || world.isGameover())
                && !world.getMenu().isMoving1()) {
            menuButtons.get(0).isTouchDown(screenX, screenY);
            menuButtons.get(1).isTouchDown(screenX, screenY);
            menuButtons.get(2).isTouchDown(screenX, screenY);

        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        if ((world.isMenu() || world.isGameover())
                && !world.getMenu().isMoving()) {
            if (!world.getMenu().isMoving1()) {
                if (menuButtons.get(0).isTouchUp(screenX, screenY)) {
                    if (menuButtons.get(0).getText().equals("< Back")) {
                        world.getMenu().clickBack();
                        AssetLoader.sound1.play();

                    } else {
                        world.getMenu().fadeOutAll();
                        world.getCenter().fallDownEffect();
                        AssetLoader.sound1.play();
                    }

                } else if (menuButtons.get(1).isTouchUp(screenX, screenY)) {

                    if (menuButtons.get(1).getText().equals("Scores >")) {
                        world.getMenu().clickLeaderboards();
                        AssetLoader.sound1.play();
                    } else {
                        if (actionResolver.isSignedIn())
                            actionResolver.showAchievement();
                        else
                            actionResolver.signIn();
                    }

                } else if (menuButtons.get(2).isTouchUp(screenX, screenY)) {
                    //Gdx.app.log("Share", "Share");
                    if (menuButtons.get(2).getText().equals("Leaderboards")) {
                        if (actionResolver.isSignedIn())
                            actionResolver.showScores();
                        else
                            actionResolver.signIn();
                    } else {
                        if (world.currentState == GameState.GAMEOVER) {
                            actionResolver.shareGame("Scored "
                                    + world.getScore() + " in " + C.C.gameName
                                    + "! You'll never beat me! ");
                        } else {
                            actionResolver.shareGame("Try " + C.C.gameName
                                    + "!! ");
                        }
                    }
                }
            }
        } else if (world.isRunning()) {
            if (screenX < world.gameWidth / 2) {
                world.getCenter().stop();
            } else {
                world.getCenter().stop();
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private int scaleX(int screenX) {
        return (int) (screenX / scaleFactorX);
    }

    private int scaleY(int screenY) {
        return (int) (screenY / scaleFactorY);
    }

}
