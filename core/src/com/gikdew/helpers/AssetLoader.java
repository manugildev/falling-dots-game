package com.gikdew.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {
    public static Texture logoTexture, hexa, square;
    public static TextureRegion logo, dot, menuBg;
    public static BitmapFont font, font1, font2;
    private static Preferences prefs;
    public static Sound sound, sound1, sound2, laserFin;

    public static void load() {
        logoTexture = new Texture(Gdx.files.internal("logo.png"));
        logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        logo = new TextureRegion(logoTexture, 0, 0, logoTexture.getWidth(),logoTexture.getHeight());
        logo.flip(false, false);

        hexa = new Texture(Gdx.files.internal("dot.png"));
        hexa.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        square = new Texture(Gdx.files.internal("square.png"));
        square.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // JUMPING DOT TEXTURE
        dot = new TextureRegion(hexa);
        dot.flip(false, true);

        Texture tfont = new Texture(Gdx.files.internal("font.png"), true);
        tfont.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);

        // FONT
        font = new BitmapFont(Gdx.files.internal("font.fnt"),
                new TextureRegion(tfont), false);
        font.setScale(2.2f, -2.2f);
        font.setColor(Color.WHITE);

        font1 = new BitmapFont(Gdx.files.internal("font.fnt"),
                new TextureRegion(tfont), false);
        font1.setScale(1.3f, -1.3f);
        font1.setColor(Color.WHITE);

        font2 = new BitmapFont(Gdx.files.internal("font.fnt"),
                new TextureRegion(tfont), false);
        font2.setScale(0.9f, -0.9f);
        font2.setColor(Color.WHITE);

        // MENU BG TEXTURE

        prefs = Gdx.app.getPreferences("Shapes");

        if (!prefs.contains("highScore")) {
            prefs.putInteger("highScore", 0);
        }

        if (!prefs.contains("games")) {
            prefs.putInteger("games", 0);
        }

        sound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));
        sound1 = Gdx.audio.newSound(Gdx.files.internal("sound1.wav"));
        sound2 = Gdx.audio.newSound(Gdx.files.internal("sound2.wav"));
        laserFin = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));

    }

    public static void setHighScore(int val) {
        prefs.putInteger("highScore", val);
        prefs.flush();
    }

    public static int getHighScore() {
        return prefs.getInteger("highScore");
    }

    public static void addGamesPlayed() {
        prefs.putInteger("games", prefs.getInteger("games") + 1);
        prefs.flush();
    }

    public static int getGamesPlayed() {
        return prefs.getInteger("games");
    }

    public static void dispose() {
        font.dispose();
        hexa.dispose();
        logoTexture.dispose();
        sound.dispose();
        sound1.dispose();
        sound2.dispose();

    }
}
