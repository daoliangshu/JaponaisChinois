package com.daoliangshu.japonaischinois.sentencereordering;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.ZoomOutPageTransformer;
import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.lettrabulle.manager.GameSettingsSlideFragment;
import com.daoliangshu.japonaischinois.lettrabulle.manager.GameStatusSlideFragment;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by daoliangshu on 6/8/17.
 */

public class SentenceReorderingActivity extends AppCompatActivity {
    //private LettrabulleView gameView;
    private DBHelper dbHelper;
    private ViewPager mGameMenuPager;
    private ArrayList<String> vocList;
    private SenceReorderingView gameView;
    private GameSettingsSlideFragment gameSettings;
    private GameStatusSlideFragment gameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        //SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.sentencereordering_layout);//change
        Bundle extra = getIntent().getExtras();

        try {
            dbHelper = new DBHelper(getApplicationContext());
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

        if (extra != null) {


        } else {

        }

        gameView = (SenceReorderingView) findViewById(R.id.board_view);
        gameView.setActivity(this);



        /*---- Flip View ---*/
        Button retryButton = (Button) findViewById(R.id.retry_btn);
        Button quitButton = (Button) findViewById(R.id.quit_btn);
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
                        v.setVisibility(View.GONE);
                        gameView.setRunning(true);
                        v1.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        mGameMenuPager = (ViewPager) findViewById(R.id.game_menu_pager);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager()
        );
        mGameMenuPager.setAdapter(mPagerAdapterMain);
        mGameMenuPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }


    String str;
    String strZh;
    String strSecond;

    Thread thread = new Thread(new Runnable() {
        public void run() {
            if (gameStatus != null) {
                gameStatus.setWord(strZh);
                gameStatus.setTrans(strSecond);
            }
        }
    });

    public void updateInfoWord(String str, String strZh, String strSecond) {
        this.str = str;
        this.strZh = strZh;
        this.strSecond = strSecond;
        runOnUiThread(thread);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.setRunning(true);
        //gameView.getThread();
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameView.getThread().setRunning(false, false);
    }

    @Override
    protected void onDestroy() {
        gameView.leaveThread();
        //gameView.getThread().leaveThread();
        super.onDestroy();
        this.finish();
    }

    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameStatus != null) gameStatus.pauseTimer();

                View v = findViewById(R.id.lettrabulle_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(View.GONE);

                v.setVisibility(View.VISIBLE);


                TextView tvScore = (TextView) findViewById(R.id.score_gameover);
                if (gameStatus != null)
                    tvScore.setText(String.valueOf(String.valueOf(gameStatus.getScore())));
                if (gameStatus != null) gameStatus.setScore(0);
            }
        });
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
                    GameSettingsSlideFragment res2 = new GameSettingsSlideFragment();
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

    public void settingViewMoveLeft(){
        this.mGameMenuPager.arrowScroll(View.FOCUS_LEFT);
    }
}
