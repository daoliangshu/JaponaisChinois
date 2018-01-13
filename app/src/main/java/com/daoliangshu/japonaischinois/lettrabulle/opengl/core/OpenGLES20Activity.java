package com.daoliangshu.japonaischinois.lettrabulle.opengl.core;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.EntryManager;
import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.lettrabulle.manager.GameStatusSlideFragment;
import com.daoliangshu.japonaischinois.lettrabulle.manager.ZoomOutPageTransformer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.menu_components.GameSettingTopSlideFragment;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

import static android.view.View.GONE;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;

/**
 * Created by daoliangshu on 2017/7/5.
 * Activity for the bubble game
 */
public class OpenGLES20Activity extends AppCompatActivity {

    public EntryManager mEntryManager;
    private ViewPager mGameMenuPager;
    private GameSettingTopSlideFragment gameSettings;
    private GameStatusSlideFragment gameStatus;
    private MyGLSurfaceView mGLView;
    private Handler mHandler;

    //Private Class
    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mCurrentFragment;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    GameStatusSlideFragment res = new GameStatusSlideFragment();
                    gameStatus = res;
                    return res;
                case 1:
                    GameSettingTopSlideFragment res2 = new GameSettingTopSlideFragment();
                    gameSettings = res2;
                    return res2;
            }
            return null;
        }


        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /*------------------------------------------------*/
    /*--------------------EVENTS----------------------*/
    /*------------------------------------------------*/
    //Override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager()
        );
        setContentView(R.layout.lettrabulle_gl);
        mHandler = new Handler();
        View v1 = findViewById(R.id.game_layout);
        mEntryManager = Settings.dbEntryManager;
        //Bundle extra = getIntent().getExtras();

        mGLView = (MyGLSurfaceView) findViewById(R.id.lettrabulle_view);
        mGLView.setActivity(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                SYSTEM_UI_FLAG_IMMERSIVE);


        /*---- Flip View ---*/
        Button retryButton = (Button) findViewById(R.id.lettrabule_retry_btn);
        Button quitButton = (Button) findViewById(R.id.lettrabule_quit_btn);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Go back to game */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameStatus.restartTimer();
                        View v1 = findViewById(R.id.game_layout);
                        View v = findViewById(R.id.lettrabulle_game_over_view);
                        v.setVisibility(GONE);
                        //gameView.setRunning(true);
                        mGLView.resetLinePositions();
                        Config.gameState = Constants.GAME_STATE_ACTIVE;
                        v1.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        mGameMenuPager = (ViewPager) findViewById(R.id.game_menu_pager);
        mGameMenuPager.setVisibility(View.GONE);
        mGameMenuPager.setAdapter(mPagerAdapterMain);
        mGameMenuPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mGameMenuPager.setVisibility(View.VISIBLE);
        v1.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy(){
        Config.gameState = Constants.GAME_STATE_PAUSED;
        super.onDestroy();
    }
    //Own
    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameStatus != null) gameStatus.pauseTimer();

                View v = findViewById(R.id.lettrabulle_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(GONE);

                v.setVisibility(View.VISIBLE);


                TextView tvScore = (TextView) findViewById(R.id.lettrabulle_score_gameover);
                if (gameStatus != null)
                    tvScore.setText(String.valueOf(String.valueOf(gameStatus.getScore())));
                if (gameStatus != null) gameStatus.setScore(0);
            }
        });
    }

    /*------------------------------------------------*/
    /*--------------------UPDATE----------------------*/
    /*------------------------------------------------*/
    public void updateInfoWord(final String str, final String strZh,final String strSecond) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            // Write your code here to update the UI.
                            gameStatus.setWord(strZh);
                            gameStatus.setTrans(strSecond);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        }).start();
    }
    public void computeAndAddScore(final int baseInt, final float weight) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int scoreToAdd = (int) (baseInt * weight);
                gameStatus.incrementScore(scoreToAdd);

            }
        });
    }
    public void updateSpeed(){ mGLView.updateSpeed();}
    public void settingViewMoveLeft(){
        this.mGameMenuPager.arrowScroll(View.FOCUS_LEFT);
    }

    /*------------------------------------------------*/
    /*--------------------SETTERS---------------------*/
    /*------------------------------------------------*/
    public void setCurrent( int vocIndex){
        updateInfoWord("dumb", mEntryManager.getSourceFromId(vocIndex, 1),
                mEntryManager.getSourceFromId(vocIndex, 2));
    }

}