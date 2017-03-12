package com.gikdew.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.gikdew.gameworld.GameWorld;
import com.gikdew.helpers.AssetLoader;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class BallManager {

    ArrayList<Ball> balls = new ArrayList<Ball>();
    private GameWorld world;
    private Ball ball;
    private ArrayList<Vector2> points;
    private TweenCallback cb1;
    private TweenManager manager;
    private Value second1 = new Value();
    private int length;
    private int initialBalls = 15;
    private Tween ballsTween;
    private int counter = 0;
    private float segundos = 1.1f;

    public BallManager(final GameWorld world) {
        this.world = world;
        createPoints(1);
        for (int i = 0; i < initialBalls; i++) {
            ball = new Ball(world, points.get((int) Math.floor(Math.random()
                    * points.size())), 20);
            ball.setVelocityZero();
            balls.add(ball);
        }
        length = balls.size();
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

        cb1 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                second1.setValue(0);
                balls.get(counter).setPosition(
                        points.get((int) Math.floor(Math.random()
                                * points.size())));
                counter++;
                // Gdx.app.log("Segundos", segundos + "");
                if (counter >= balls.size()) {
                    counter = 0;
                }
                if (segundos >= 0.43) {
                    segundos -= 0.015;
                    segundos = (float) round(segundos, 3);
                }

                ballsTween = Tween.to(second1, -1, segundos).target(1)
                        .repeatYoyo(0, 0).setCallback(cb1)
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .ease(TweenEquations.easeInOutBounce).start(manager);

            }
        };

        ballsTween = Tween.to(second1, -1, 0.1f).target(1).repeatYoyo(0, 0)
                .setCallback(cb1).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInOutBounce).start(manager);

    }

    public static double round(double numero, int digitos) {
        int cifras = (int) Math.pow(10, digitos);
        return Math.rint(numero * cifras) / cifras;
    }

    public void update(float delta) {
        manager.update(delta);

        for (int i = 0; i < length; i++) {
            balls.get(i).update(delta);
        }

    }

    public void render(SpriteBatch batch, ShapeRenderer sR) {
        for (int i = 0; i < length; i++) {
            balls.get(i).render(batch, sR);

        }

    }

    public void render1(SpriteBatch batch, ShapeRenderer sR) {

        for (int i = 0; i < length; i++) {
            if (balls.get(i).isGrowing()) {
                balls.get(i).render(batch, sR);
            }
        }

    }

    public void collisions() {

        for (int i = 0; i < length; i++) {
            if (!balls.get(i).isGrowing()) {
                if (Intersector.overlaps(balls.get(i).getColCircle(), world
                        .getCenter().getColCircle())) {
                    if (!balls.get(i).isInCenter()) {
                        if (balls.get(i).getMode() == world.getCenter()
                                .getShape()) {
                            world.addScore(1);
                            balls.get(i).dieCenter(
                                    points.get((int) Math.floor(Math.random()
                                            * points.size())),
                                    world.getCenter().getPosition());


                        } else {
                            balls.get(i).die(new Vector2());
                            world.getCenter().clickBye();
                            for (int j = 0; j < length; j++) {

                                balls.get(j).setDead(true);
                                balls.get(j).setVelocityZero();
                            }
                            ballsTween.kill();
                            deleteAllBalls();
                            AssetLoader.laserFin.play();
                        }

                    }
                }
            }

        }
    }

    public void addBall(float duration) {
        second1.setValue(0);
        Tween.to(second1, -1, duration).target(1).repeatYoyo(1000, 0)
                .setCallback(cb1).setCallbackTriggers(TweenCallback.END)
                .ease(TweenEquations.easeInOutBounce).start(manager);

    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void deleteAllBalls() {
        for (int i = 0; i < length; i++) {
            if (!balls.get(i).isGrowing()) {
                balls.get(i).setVelocityZero();
                balls.get(i).setPosition(points.get(0));
                balls.get(i).dieGameOver();
            }
        }
    }

    public void createPoints(int numOfS) {
        points = new ArrayList<Vector2>();
        points.clear();
        int distanceToCenter = (int) Math.sqrt(Math.pow(
                (-50 - world.gameWidth / 2), 2)
                + Math.pow((-50 - world.gameHeight / 2), 2));
        for (int i = 0; i < numOfS; i++) {
            float cx = world.gameWidth / 2;
            float cy = world.gameHeight / 2;
            float angle = (i * 80 / numOfS);
            if (angle > 40) {
                angle -= 80;
            }
            Gdx.app.log("Point " + i, angle + "");
            Vector2 point = new Vector2((int) (distanceToCenter * Math.cos(Math
                    .toRadians(angle - 90))) + cx, (int) (distanceToCenter
                    * Math.sin(Math.toRadians(angle - 90)) + cy));

            points.add(point);
        }

    }

    public ArrayList<Vector2> getPoints() {
        return points;
    }

    public void reset() {

        segundos = 1.2f;
        second1.setValue(0);
        counter = 0;
        createPoints(1);
        world.getCenter().stop();
        length = initialBalls;
        balls.clear();
        for (int i = 0; i < initialBalls; i++) {
            ball = new Ball(world, points.get((int) Math.floor(Math.random()
                    * points.size())), 20);
            ball.setVelocityZero();
            balls.add(ball);
        }
        length = balls.size();
        ballsTween.kill();
        ballsTween = Tween.to(second1, -1, 0.1f).target(1).repeatYoyo(0, 0)
                .setCallback(cb1).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInOutBounce).start(manager);

    }

    public float getSegundos() {
        return segundos;
    }
}
