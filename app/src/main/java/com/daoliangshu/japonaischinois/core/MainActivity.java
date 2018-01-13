package com.daoliangshu.japonaischinois.core;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.ZoomOutPageTransformer;
import com.daoliangshu.japonaischinois.core.data.DataManager;
import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.core.data.SettingsActivity;
import com.daoliangshu.japonaischinois.core.data.StaticUtils;
import com.daoliangshu.japonaischinois.core.data.TTSManager;
import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.core.db.StatisticalDatabase;
import com.daoliangshu.japonaischinois.core.fragments.InfoSlidePageFragment;
import com.daoliangshu.japonaischinois.core.fragments.MainSlidePageFragment;
import com.daoliangshu.japonaischinois.core.fragments.SettingSlidePageFragment;
import com.daoliangshu.japonaischinois.core.fragments.VocListSlidePageFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private MediaPlayer mp = null;
    private MainSlidePageFragment flashCardFragment;
    private SettingSlidePageFragment settingsFragment;
    private int currentLesson = 1;
    private EntryManager entryManager;
    private ViewFlipper flipper;
    private static final int NUM_MAIN_PAGES = 2;
    private ViewPager mPagerMain;

    public static final int NUM_INFO_PAGES = 8;
    private ViewPager mPagerInfo;
    private TTSManager ttsManager;
    private  int stepBackTriggerCount = 0; // If two time click back => finish app

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //private class
    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        int mode = 0;
        private Fragment mCurrentFragment;
        HashMap<Integer, Fragment> mFragmentList;

        public SlidePagerAdapter(FragmentManager fm, int mode) {
            super(fm);
            mFragmentList = new HashMap<>();
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
                        case 0:
                            MainSlidePageFragment mspg = new MainSlidePageFragment();
                            mFragmentList.put(1, mspg );
                            return mspg;
                        case 1:
                            VocListSlidePageFragment vlspg = new VocListSlidePageFragment();
                            mFragmentList.put(2, vlspg);
                            return vlspg;
                        default:
                            return new SettingSlidePageFragment();
                    }
                default:
                    return new VocListSlidePageFragment();
            }
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public Integer[] getCurVocListCodesAsArray(){
            try{
                return ((VocListSlidePageFragment)mFragmentList.get(2)).getSelectedVocsCodeAsArray();
            }catch(Exception ex){
                return null;
            }

        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mode == StaticUtils.PAGE_INFO &&
                    getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
                ((InfoSlidePageFragment) mCurrentFragment).reloadIfDisplayModeChanged();
            }
            stepBackTriggerCount = 0;
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


    /*-------------------------------------------------*/
    /*------------------EVENTS-------------------------*/
    /*-------------------------------------------------*/
    //Override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        forceLocale();
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
            DataManager.copyAssetFolder(getAssets(), "gram",
                    this.getFilesDir().getPath() + "/gram");
            Log.i("PATH__ : ", this.getFilesDir().getPath());
        }
        try {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            StatisticalDatabase statisticalDatabase = new StatisticalDatabase(getApplicationContext());
            Settings.dbEntryManager = new EntryManager(dbHelper, statisticalDatabase);
            entryManager = Settings.dbEntryManager;

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
    @Override
    public void onBackPressed() {

        if (flipper.getDisplayedChild() == 1) {
            flipper.setDisplayedChild(0);

        } else {
            if (mPagerMain.getCurrentItem() == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                stepBackTriggerCount += 1;
                if(stepBackTriggerCount >= 2) {this.finish(); return;}
                Toast.makeText(getApplicationContext(),
                        getString(R.string.confirm_quit),
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Otherwise, select the previous step.
                mPagerMain.setCurrentItem(mPagerMain.getCurrentItem() - 1);
            }
        }
        stepBackTriggerCount = 0;
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
        Settings.dbEntryManager.close();
        ttsManager.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (flashCardFragment != null) {
            flashCardFragment.resumeInterval();
            stepBackTriggerCount = 0;
        }

    }
    //Class events
    public void pronounceLetter(String phonOrLetter) {
        ttsManager.speak(phonOrLetter);
    }
    public void forceLocale(){
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(Locale.FRENCH);
        resources.updateConfiguration(configuration,displayMetrics);
    }
    public void speak() {
        ttsManager.speak(getCurrent(DBHelper.target));
    }
    public void flipView(int childIndexToDisplay) {
        flipper.setDisplayedChild(childIndexToDisplay);
    }

    /*-------------------------------------------------*/
    /*------------------INIT, RESET--------------------*/
    /*-------------------------------------------------*/
    public void resetCumul(){ stepBackTriggerCount = 0; }
    /*-------------------------------------------------*/
    /*------------------UPDATE------------------------*/
    /*-------------------------------------------------*/
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
    public void updateInterval() {
        if (flashCardFragment != null) {
            flashCardFragment.updateInterval();
        }
    }
    public void saveSettings() {
        DataManager.saveSettings(getFilesDir().getAbsolutePath() +
                "/settings.conf");
    }
    public void nextWord() {
        Settings.dbEntryManager.next();
        if (Settings.isAutoSpeak) speak();
        updateInterval();
    }
    public void previousWord() {
        Settings.dbEntryManager.previous();
        if (Settings.isAutoSpeak) speak();
        updateInterval();
    }
    /*-------------------------------------------------*/
    /*------------------SETTERS------------------------*/
    /*-------------------------------------------------*/
    public void setFlashCardFragment(MainSlidePageFragment fragment) {
        flashCardFragment = fragment;
    }
    public void setSettingsFragment(SettingSlidePageFragment fragment) {
        settingsFragment = fragment;

    }
    public void setVocList(int lessonIndex, int thematicIndex, int level) {
        entryManager.setVocList(lessonIndex, thematicIndex, level);
        currentLesson = lessonIndex;
    }
    /*-------------------------------------------------*/
    /*------------------GETTERS------------------------*/
    /*-------------------------------------------------*/
    /**
     * Get the current value of the specifed columm. (name defined in DBHelper)
     * Choices: PHON, WORD, TRANS
     *
     * @param dbColumn: The column for which to ask value
     * @return Value of the column
     */
    public String getCurrent(String dbColumn) {
        HashMap<String, String> res = Settings.dbEntryManager.getCurrentWord();
        if (res != null && res.containsKey(dbColumn)) {
            return res.get(dbColumn);
        }else{
            return "[]";
        }
    }
    public ArrayList<HashMap<String, String>> getCurrentVocList(){
        return entryManager.getVocList();
    }
    public Integer[] getCurVocListCodesAsArray(){
        return ((SlidePagerAdapter)mPagerMain.getAdapter()).getCurVocListCodesAsArray();
    }
}