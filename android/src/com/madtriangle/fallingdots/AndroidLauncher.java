package com.madtriangle.fallingdots;

import C.C;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gikdew.shapes.ActionResolver;
import com.gikdew.shapes.ShapesGame;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import java.util.InputMismatchException;

public class AndroidLauncher extends AndroidApplication implements
        ActionResolver, GameHelperListener {

    private static final String AD_UNIT_ID_BANNER = C.AD_UNIT_ID_BANNER;
    private static final String AD_UNIT_ID_INTERSTITIAL = C.AD_UNIT_ID_INTERSTITIAL;
    private final static int REQUEST_CODE_UNUSED = 9002;
    private static String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";
    protected AdView adView;
    protected View gameView;
    AdView admobView;
    private InterstitialAd interstitialAd;
    private GameHelper _gameHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useImmersiveMode = true;
        cfg.useAccelerometer = false;
        cfg.useCompass = false;

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id="
                + getPackageName();

        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        admobView = createAdView();

        View gameView = createGameView(cfg);
        layout.addView(gameView);
        layout.addView(admobView);
        _gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        _gameHelper.enableDebugLog(false);

        GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInSucceeded() {
            }

            @Override
            public void onSignInFailed() {
            }
        };

        if (C.LEADERBOARDS) {

            _gameHelper.setup(gameHelperListener);
        }

        setContentView(layout);
        startAdvertising(admobView);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
            }
        });
        showOrLoadInterstital();

    }

    private AdView createAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_UNIT_ID_BANNER);
        adView.setId(12345); // this is an arbitrary id, allows for relative
        // positioning in createGameView()
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.BOTTOM;

        adView.setLayoutParams(params);
        adView.setVisibility(View.INVISIBLE);
        adView.setBackgroundColor(Color.TRANSPARENT);
        return adView;
    }

    private View createGameView(AndroidApplicationConfiguration cfg) {
        gameView = initializeForView(new ShapesGame(this), cfg);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        gameView.setLayoutParams(params);
        return gameView;
    }

    private void startAdvertising(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }

    @Override
    public void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        _gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        _gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showOrLoadInterstital() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();

                    } else {
                        AdRequest interstitialRequest = new AdRequest.Builder()
                                .build();
                        interstitialAd.loadAd(interstitialRequest);

                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void signIn() {
        try {
            runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    _gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage()
                    + ".");
        }
    }

    @Override
    public void signOut() {
        try {
            runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    _gameHelper.signOut();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage()
                    + ".");
        }
    }

    @Override
    public void rateGame() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
    }

    @Override
    public boolean isSignedIn() {
        return _gameHelper.isSignedIn();
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }

    @Override
    public void submitScore(long score) {
        if (isSignedIn() == true) {
            Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
                    getString(R.string.leaderboard_id), score);
        } else {
            // signIn();
        }
    }

    @Override
    public void submitGamesPlayed(long score) {
        if (isSignedIn() == true) {
            Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
                    getString(R.string.games_played_id), score);
        } else {
            // signIn();
        }
    }

    @Override
    public void showScores() {
        if (isSignedIn() == true)
            startActivityForResult(
                    Games.Leaderboards.getAllLeaderboardsIntent(_gameHelper
                            .getApiClient()), REQUEST_CODE_UNUSED);
            // Games.Leaderboards.getLeaderboardIntent( _gameHelper.getApiClient(),
            // C.LEADERBOARD_ID),REQUEST_CODE_UNUSED)
        else {
            signIn();
        }
    }

    @Override
    public void showAchievement() {
        if (isSignedIn() == true)
            startActivityForResult(
                    Games.Achievements.getAchievementsIntent(_gameHelper
                            .getApiClient()), REQUEST_CODE_UNUSED);
        else {
            signIn();
        }
    }

    @Override
    public boolean shareGame(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg + GOOGLE_PLAY_URL);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share..."));
        return true;

    }

    @Override
    public void unlockAchievementGPGS(String string) {
        if (isSignedIn()) {
            Games.Achievements.unlock(_gameHelper.getApiClient(), string);
        }
    }

    @Override
    public void viewAd(boolean view) {
        if (view) {
            try {
                runOnUiThread(new Runnable() {
                    public void run() {

                        admobView.setVisibility(View.VISIBLE);


                    }
                });
            } catch (Exception e) {
            }

        } else {
            try {
                runOnUiThread(new Runnable() {
                    public void run() {

                        admobView.setVisibility(View.INVISIBLE);


                    }
                });
            } catch (Exception e) {
            }
        }
    }

}
