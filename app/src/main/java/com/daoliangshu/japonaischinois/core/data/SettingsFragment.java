package com.daoliangshu.japonaischinois.core.data;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daoliangshu.japonaischinois.R;

/**
 * Created by quentin on 1/13/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener{

    boolean vocSelectionChange = false;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);

        SharedPreferences sharedPreferences =
                getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for(int i =0; i< count; i++){
            Preference pref = prefScreen.getPreference(i);
            //TODO set the preference initial display

                pref.setOnPreferenceChangeListener(this);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        if(vocSelectionChange){
            if(Settings.curVocChooserMode == 0){
                Settings.dbEntryManager.setVocList(Settings.curLesson, -1, -1);
            }else if(Settings.curVocChooserMode == 1){
                Settings.dbEntryManager.setVocList(-1, Settings.curCategory, -1);
            }

        }
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if(null != pref){
            if(pref instanceof ListPreference){
                String value = sharedPreferences.getString(pref.getKey(), "");
                setPreferenceSummary(pref, value);
            }
        }
    }


    private void setPreferenceSummary(Preference preference,
                                      String value){
        preference.setSummary(value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("QCR", "onPrefChange: " + preference.toString() + "    newValue:" + newValue.toString());

        if (preference.getKey().equals(getString(R.string.pref_lesson_chooser_key))) {
            Settings.curLesson = Integer.parseInt(newValue.toString());
            vocSelectionChange = true;
        }
        return true;
    }
}
