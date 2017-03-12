package C;

import com.badlogic.gdx.graphics.Color;

public class C {

    // ALWAYS SET TO TRUE
    public static final boolean LEADERBOARDS = true;

    // NAME YOUR GAME
    public static CharSequence gameName = "Falling Dots";

    // BANNER ID FROM ADMOB
    public static String AD_UNIT_ID_BANNER = "ca-app-pub-6147578034437241/9354877019";

    // INTERSTITIAL ID FROM ADMOB
    public static String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-6147578034437241/1831610213";

    // LEADERBOARD ID (you should also set the app_id in res>values>strings.xml)
    public static String LEADERBOARD_ID = "CgkI4N3Y4ckOEAIQAQ";

    // BALL1 COLOR
    public static Color colorB1 = parseColor("#27ae60", 1f);
    // BALL2 COLOR
    public static Color colorB2 = parseColor("#c0392b", 1f);
    // BACKGROUND COLOR
    public static Color backColor = parseColor("#273A48", 1f);

    // MENU COLORS
    // BUTTON COLORS
    public static Color nameBackMenu = parseColor("#8e44ad", 1f);
    public static Color playButtonMenu = parseColor("#27ae60", 1f);
    public static Color scoreButtonMenu = parseColor("#e74c3c", 1f);
    public static Color shareButtonMenu = parseColor("#3498db", 1f);

    // DONT TOUCH THIS
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

}
