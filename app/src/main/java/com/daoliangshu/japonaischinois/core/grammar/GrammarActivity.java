package com.daoliangshu.japonaischinois.core.grammar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.data.DataManager;
import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.core.data.StaticUtils;
import com.daoliangshu.japonaischinois.core.data.TTSManager;
import com.daoliangshu.japonaischinois.core.db.DBHelper;

import java.util.Locale;

/**
 * Created by daoliangshu on 4/5/17.
 */

public class GrammarActivity extends AppCompatActivity {

    private DBHelper dbHelper = null;
    private ViewPager mPagerGrammar;
    private int lessonIndex = 0;
    private String filePrefix = "";
    private int num_pages = 0;
    private TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.grammar_layout);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        Bundle mBundle = getIntent().getExtras();
        if(mBundle != null && mBundle.containsKey("lessonIndex")){

                lessonIndex = mBundle.getInt("lessonIndex");
                filePrefix = "gr_" + lessonIndex + "_";
                num_pages = Settings.grNumPages[lessonIndex];
        }else{
            finish();
        }


        mPagerGrammar = (ViewPager) findViewById(R.id.pager_main);
        PagerAdapter mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(),
                StaticUtils.PAGE_INFO);


        mPagerGrammar.setAdapter(mPagerAdapter);
        mPagerGrammar.setPageTransformer(true, new ViewPager.PageTransformer(){
            @Override
            public void transformPage(View page, float position) {
                page.setRotationY(position * -30);
            }
        });
        mPagerGrammar.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            float sumPositionAndPositionOffset;
            float prevPositionOffset;
            int equalCumulate;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("pos", String.format(Locale.ENGLISH,
                        "%d  --  %f --  %d  ", position, positionOffset, positionOffsetPixels));
                if (position + positionOffset <= sumPositionAndPositionOffset && equalCumulate >= 5) {
                    equalCumulate = 0;
                    if (mPagerGrammar.getCurrentItem() == 0) {
                        finish();
                    }
                }
                if (mPagerGrammar.getCurrentItem() == 0 && positionOffset == prevPositionOffset)
                    equalCumulate += 1;
                else {
                    equalCumulate = 0;
                }
                prevPositionOffset = positionOffset;
                sumPositionAndPositionOffset = position + positionOffset;

                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            public void onPageSelected(int position) {
                equalCumulate = 0;
                super.onPageSelected(position);
            }
        });
    }


    public void speak() {
        ttsManager.speak("rien");

    }

    public void pronounceLetter(String phonOrLetter) {
        ttsManager.speak(phonOrLetter);
    }

    public void saveSettings() {
        DataManager.saveSettings(getFilesDir().getAbsolutePath() +
                "/settings.conf");
    }



    private  int backCumul = 0;
    @Override
    public void onBackPressed() {
            if (mPagerGrammar.getCurrentItem() == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                finish();
            } else {
                // Otherwise, select the previous step.
                mPagerGrammar.setCurrentItem(mPagerGrammar.getCurrentItem() - 1);
            }

        backCumul = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ttsManager.close();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        int mode = 0;
        private Fragment mCurrentFragment;

        public SlidePagerAdapter(FragmentManager fm, int mode) {
            super(fm);
            this.mode = mode;
        }

        @Override
        public Fragment getItem(int position) {
            switch (this.mode) {
                case StaticUtils.PAGE_INFO:
                default:
                    GrammarSlidePageFragment res = new GrammarSlidePageFragment();
                    //res.setText(getHtmlAsString(position));
                    res.setPosition(position);
                    res.setText(StaticUtils.getGrammarHtmlAsString(getApplicationContext(),
                                                            filePrefix,
                                                            position));
                    return res;
            }
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mode == StaticUtils.PAGE_INFO &&
                    getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
                ((GrammarSlidePageFragment) mCurrentFragment).reloadIfDisplayModeChanged();
            }
            backCumul = 0;
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return num_pages;
        }
    }

    public void updateNightDayMode() {
        int colorResIndex;
        if (Settings.isNightMode) colorResIndex = R.color.dark_blue;
        else colorResIndex = R.color.gray_mid;
        mPagerGrammar.setBackgroundColor(StaticUtils.getColor(getApplicationContext(), colorResIndex));
    }
}
