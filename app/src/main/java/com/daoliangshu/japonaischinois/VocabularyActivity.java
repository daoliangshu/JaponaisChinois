package com.daoliangshu.japonaischinois;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


public class VocabularyActivity extends AppCompatActivity {

    private MediaPlayer mp = null;
    private MainSlidePageFragment flashCardFragment;
    private SettingSlidePageFragment settingsFragment;
    private Random rand = new Random();

    private DBHelper dbHelper = null;
    private Spinner listView;
    private int currentLesson = 1;
    private boolean mVisible;
    private ArrayList<HashMap<String, String>> vocList;

    private VocabularyManager vocManager;




    private ViewFlipper flipper;

    private static final int NUM_MAIN_PAGES = 2;
    private ViewPager mPagerMain;

    //pager info
    public static final int NUM_INFO_PAGES = 8;
    private ViewPager mPagerInfo;

    private TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vocabulary);
        ttsManager = new TTSManager();
        ttsManager.init(this);
        if (!PreferenceManager.getDefaultSharedPreferences(
                //Copy assets phonetic resources in data directory
                getApplicationContext())
                .getBoolean("installed", false) || Settings.REQUEST_UPDATE) {
            PreferenceManager.getDefaultSharedPreferences(
                    getApplicationContext())
                    .edit().putBoolean("installed", true).apply();
            DataManager.copyAssetFolder(getAssets(), "phon_info",
                    this.getFilesDir().getPath() + "/phon_info");
            Log.i("PATH__ : ", this.getFilesDir().getPath());
        }
        dbHelper = null;
        try {
            dbHelper = new DBHelper(getApplicationContext());
            vocList = dbHelper.getTransByStartWith("", 1, 1);
            vocManager = new VocabularyManager(vocList.size());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        //Load Settings
        DataManager.loadSettings(getFilesDir().getAbsolutePath() +
                "/settings.conf");

        mp = new MediaPlayer();

        //Main flipper
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);

        //View Pagers
        //Flash card + Setting pager
        mPagerMain = (ViewPager) findViewById(R.id.pager_main);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager(),
                StaticUtils.PAGE_MAIN);
        mPagerMain.setAdapter(mPagerAdapterMain);
        mPagerMain.setPageTransformer(true, new ZoomOutPageTransformer());

        //Info Pager
        mPagerInfo = (ViewPager) findViewById(R.id.pager_info);
        PagerAdapter mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(),
                StaticUtils.PAGE_INFO);


        mPagerInfo.setAdapter(mPagerAdapter);
        mPagerInfo.setPageTransformer(true, new ViewPager.PageTransformer(){
            @Override
            public void transformPage(View page, float position) {
                page.setRotationY(position * -30);
            }
        });
        mPagerInfo.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            float sumPositionAndPositionOffset;
            float prevPositionOffset;
            int equalCumulate;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("pos", String.format(Locale.ENGLISH,
                        "%d  --  %f --  %d  ", position, positionOffset, positionOffsetPixels));
                if (position + positionOffset <= sumPositionAndPositionOffset && equalCumulate >= 5) {
                    equalCumulate = 0;
                    if (mPagerInfo.getCurrentItem() == 0) {
                        flipper.setDisplayedChild(0); //Return to main view
                    }
                }
                if (mPagerInfo.getCurrentItem() == 0 && positionOffset == prevPositionOffset)
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
        if(flashCardFragment != null)flashCardFragment.updateNightDayMode();
        updateNightDayMode();
    }

    public void flipView(int childIndexToDisplay) {
        flipper.setDisplayedChild(childIndexToDisplay);
    }



    public void speak() {
        ttsManager.speak(getCurrent(DBHelper.COL_TRANS));
    }

    public DBHelper getDb() {
        return dbHelper;
    }

    public void setVocList(int lessonIndex) {
        try {
            if (dbHelper == null)
                dbHelper = new DBHelper(getApplicationContext());
            vocList = dbHelper.getTransByStartWith("", 1, lessonIndex);
            vocManager = new VocabularyManager(vocList.size());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void nextWord() {
        vocManager.next();
        if (Settings.isAutoSpeak) speak();
        updateInterval();
    }

    public void previousWord() {
        vocManager.previous();
        if (Settings.isAutoSpeak) speak();
        updateInterval();
    }


    /**
     * Get the current value of the specifed columm. (name defined in DBHelper)
     * Choices: PHON, WORD, TRANS
     *
     * @param dbColumn: The column for which to ask value
     * @return Value of the column
     */
    public String getCurrent(String dbColumn) {
        boolean mCondition = false;
        ArrayList<HashMap<String, String>> mList = null;
            mCondition = vocList != null && vocManager.getIndex() >= 0;
            mList = vocList;


        if (mCondition) {
            //There is a word to display
            return mList.get(vocManager.getIndex()).get(dbColumn);
        } else {
            // Can't display anything
            return getString(R.string.no_retrieved_value);
        }
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

        if (flipper.getDisplayedChild() == 1) {
            flipper.setDisplayedChild(0);

        } else {
            if (mPagerMain.getCurrentItem() == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                backCumul += 1;
                if(backCumul >= 2) {this.finish(); return;}
                Toast.makeText(getApplicationContext(),
                        getString(R.string.confirm_quit),
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Otherwise, select the previous step.
                mPagerMain.setCurrentItem(mPagerMain.getCurrentItem() - 1);
            }
        }
        backCumul = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (flashCardFragment != null) {
            flashCardFragment.pauseInterval();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ttsManager.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flashCardFragment != null) {
            flashCardFragment.resumeInterval();
            backCumul = 0;
        }

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
                    InfoSlidePageFragment res = new InfoSlidePageFragment();
                    //res.setText(getHtmlAsString(position));
                    res.setPosition(position);
                    res.setText(StaticUtils.getHtmlAsString(getApplicationContext(), position));
                    return res;
                case StaticUtils.PAGE_MAIN:
                    switch (position) {
                        case 1:
                            return new MainSlidePageFragment();
                        case 0:
                            SettingSlidePageFragment f = new SettingSlidePageFragment();
                            return f;
                        default:
                            return new SettingSlidePageFragment();
                    }
                default:
                    return new SettingSlidePageFragment();
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
                ((InfoSlidePageFragment) mCurrentFragment).reloadIfDisplayModeChanged();
            }
            backCumul = 0;
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            if (this.mode == StaticUtils.PAGE_INFO) {
                return NUM_INFO_PAGES;
            }
            return NUM_MAIN_PAGES;
        }
    }

    public void resetCumul(){ backCumul = 0; }

    /**
     * Ask the flashcard fragment to update the interval between words.
     * Need to be called after interval change, so that it can current schedulled interval.
     */
    public void updateInterval() {
        if (flashCardFragment != null) {
            flashCardFragment.updateInterval();
        }
    }

    public void setFlashCardFragment(MainSlidePageFragment fragment) {
        flashCardFragment = fragment;
    }

    public void setSettingsFragment(SettingSlidePageFragment fragment) {
        settingsFragment = fragment;

    }

    public void updateNightDayMode() {
        int colorResIndex;
        if (Settings.isNightMode) colorResIndex = R.color.dark_blue;
        else colorResIndex = R.color.gray_mid;
        mPagerInfo.setBackgroundColor(StaticUtils.getColor(getApplicationContext(), colorResIndex));
        mPagerMain.setBackgroundColor(StaticUtils.getColor(getApplicationContext(), colorResIndex));
        if (flashCardFragment != null) flashCardFragment.updateNightDayMode();
    }

    public void updateHiddenState() {
        if (flashCardFragment != null)
            flashCardFragment.updateHiddenState();
    }


}





