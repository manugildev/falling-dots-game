package com.gikdew.helpers;

import com.badlogic.gdx.graphics.Color;
import com.gikdew.tweenaccessors.Value;
import com.gikdew.tweenaccessors.ValueAccessor;

import java.util.ArrayList;
import java.util.Random;

import C.C;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class ColorManager {
    private TweenManager manager;
    private Value c1 = new Value();
    private Value c2 = new Value();
    private Value c3 = new Value();
    private TweenCallback cb, cb1, cb2;
    private Color color, targetColor;
    private ArrayList<String> colors1 = new ArrayList<String>();
    private float target1, target2, target3;

    private Random randomGenarator;
    private Integer random, rtime;

    public ColorManager() {
        // COLORS

        colors1.add(C.colorB1.toString());
        colors1.add(C.colorB2.toString());

        color = parseColor(colors1.get(0), 1f);
        c1.setValue(color.r);
        c2.setValue(color.g);
        c3.setValue(color.b);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

        randomGenarator = new Random();
        targetColor = parseColor(colors1.get(0), 1f);

        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                random = randomGenarator.nextInt(colors1.size());
                if (type == 2) {
                    rtime = randomGenarator.nextInt(6) + 6;
                } else {
                    rtime = randomGenarator.nextInt(6) + 3;
                }

                // Gdx.app.log("Random Number", random.toString());
                target1 = parseColor(colors1.get(random), 1f).r;
                Tween.to(c1, -1, rtime).target(target1).repeatYoyo(0, 0)
                        .setCallback(cb)
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .ease(TweenEquations.easeInOutSine).start(manager);
            }
        };

        cb1 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                // Gdx.app.log("Random Number", random.toString());
                target2 = parseColor(colors1.get(random), 1f).g;
                Tween.to(c2, -1, rtime + 0.00001f).target(target2)
                        .repeatYoyo(0, 0).setCallback(cb1)
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .ease(TweenEquations.easeInOutSine).start(manager);
            }
        };

        cb2 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                // Gdx.app.log("Random Number", random.toString());
                target3 = parseColor(colors1.get(random), 1f).b;
                Tween.to(c3, -1, rtime + 0.00002f).target(target3)
                        .repeatYoyo(0, 0).setCallback(cb2)
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .ease(TweenEquations.easeInOutSine).start(manager);
            }
        };

    }

    public Color getColor() {
        return color;
    }

    public void oneToTwo() {
        color = new Color(parseColor(colors1.get(0), 1f));
        targetColor = parseColor(colors1.get(1), 1f);
        Tween.to(c1, -1, 0.1f).target(targetColor.r).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(c2, -1, 0.1f).target(targetColor.g).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(c3, -1, 0.1f).target(targetColor.b).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }

    public void twoToOne() {
        color = new Color(parseColor(colors1.get(1), 1f));
        targetColor = parseColor(colors1.get(0), 1f);
        Tween.to(c1, -1, 0.06f).target(targetColor.r).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(c2, -1, 0.06f).target(targetColor.g).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(c3, -1, 0.06f).target(targetColor.b).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }

    public void update(float delta) {
        manager.update(delta);
        color = new Color(c1.getValue(), c2.getValue(), c3.getValue(), 1);
        // Gdx.app.log("Color", color.toString());
    }

    public static Color parseColor(String hex, float alpha) {
        String hex1 = hex;
        if (hex1.indexOf("#") != -1) {
            hex1 = hex1.substring(1);
            // Gdx.app.log("Hex", hex1);
        }
        Color color = Color.valueOf(hex1);
        color.a = alpha;
        return color;
    }

    public void reset() {
        color = parseColor(colors1.get(0), 1f);
        c1.setValue(color.r);
        c2.setValue(color.g);
        c3.setValue(color.b);
    }
}
