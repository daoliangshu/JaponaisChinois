package com.daoliangshu.japonaischinois.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.StaticUtils;
import com.daoliangshu.japonaischinois.VocabularyActivity;

/**
 * Created by daoliangshu on 2/5/17.
 */
public class SettingSlidePageFragment extends Fragment {

    private VocabularyActivity parentActivity;
    private Spinner lessonSpinner;
    private Spinner modeSpinner;
    private Spinner categorySpinner;
    private Spinner entryTypeSpinner;
    private Spinner emptyRatioSpinner;
    private Spinner chooserModeSpinner;
    ViewGroup rView;
    private static boolean vocIsLoaded = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_settings, container, false);
        rView = rootView;
        //List view
        parentActivity = (VocabularyActivity) getActivity();


        //mode
        modeSpinner = (Spinner) rootView.findViewById(R.id.mode_list);
        //Array to show in lessonSpinner:
        final String[] values2 = getResources().getStringArray(R.array.switch_mode_list);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, values2) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(values2[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        modeSpinner.setAdapter(adapter2);
        //listener listview click
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.resetCumul();
                //Clicked item value
                String itemValue = (String) modeSpinner.getItemAtPosition(position);
                switch (position) {
                    case StaticUtils.MODE_MANUAL:
                        Settings.curInterval = 9000000;
                        break;
                    case StaticUtils.MODE_3_SEC:
                        Settings.curInterval = 3000;
                        break;
                    case StaticUtils.MODE_5_SEC:
                        Settings.curInterval = 5000;
                        break;
                    case StaticUtils.MODE_10_SEC:
                        Settings.curInterval = 10000;
                        break;
                    case StaticUtils.MODE_15_SEC:
                        Settings.curInterval = 15000;
                        break;
                    case StaticUtils.MODE_30_SEC:
                        Settings.curInterval = 30000;
                        break;

                }
                parentActivity.updateInterval();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        modeSpinner.setSelection(Settings.getCurIntervalPos());
        //display mode
        entryTypeSpinner = (Spinner) rootView.findViewById(R.id.display_mode_list);
        //Array to show in lessonSpinner:
        final String[] entryTypeValues = getResources().getStringArray(R.array.entry_type_array);
        ArrayAdapter<String> entryTypeAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, entryTypeValues) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(entryTypeValues[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        entryTypeSpinner.setAdapter(entryTypeAdapter);
        //listener listview click
        entryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.resetCumul();
                Settings.entryType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //display mode
        emptyRatioSpinner = (Spinner) rootView.findViewById(R.id.empty_ratio);
        //Array to show in lessonSpinner:
        final String[] emptyRatioValues = getResources().getStringArray(R.array.empty_word_ratio);
        ArrayAdapter<String> emptyRatioAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, emptyRatioValues) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(emptyRatioValues[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        emptyRatioSpinner.setAdapter(emptyRatioAdapter);
        for(int i=0; i< emptyRatioValues.length; i++){
            if(Math.abs( Float.valueOf(emptyRatioValues[1]) - Settings.curEmptyRatio) < 1.0f){
                emptyRatioSpinner.setSelection(i);
            }
        }
        //listener listview click
        emptyRatioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.resetCumul();
                Settings.curEmptyRatio = Float.valueOf(emptyRatioValues[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Settings.curEmptyRatio = 0.5f;
            }
        });


        loadSettings(rootView);

        final int hideCheckResId[] = {R.id.hide_word_check,
                                        R.id.hide_trans_check,
                                            R.id.hide_pron_check,
                                                R.id.auto_speak_check,
                                                    };

        //Checkbox listeners
        for (int i = 0; i < hideCheckResId.length; i++) {
            CheckBox cb = (CheckBox) rootView.findViewById(hideCheckResId[i]);
            final int resId = hideCheckResId[i];
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    parentActivity.resetCumul();
                    switch (resId) {
                        case R.id.hide_word_check:
                            Settings.hideWord = isChecked;
                            break;
                        case R.id.hide_trans_check:
                            Settings.hideTrans = isChecked;
                            break;
                        case R.id.hide_pron_check:
                            Settings.hidePron = isChecked;
                            break;
                        case R.id.auto_speak_check:
                            Settings.isAutoSpeak = isChecked;
                            break;
                    }
                    parentActivity.updateHiddenState();
                }
            });
        }


        final Switch dayNightSwitch = (Switch) rootView.findViewById(R.id.switch_day_night);
        dayNightSwitch.setChecked(Settings.isNightMode);
        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                parentActivity.resetCumul();
                Settings.isNightMode = isChecked;
                if (isChecked) {
                    dayNightSwitch.setText(getString(R.string.night_active));
                } else {
                    dayNightSwitch.setText(getString(R.string.day_active));
                }
                if (parentActivity != null) parentActivity.updateNightDayMode();
                updateNightDayMode();
            }
        });

        ((Button) rootView.findViewById(R.id.save_config_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.saveSettings(getActivity().getFilesDir().getAbsolutePath() +
                        "/settings.conf");
            }
        });

        initLangSettings(rootView);
        initChooserModeSettings(rootView);


        updateNightDayMode();
        if(!vocIsLoaded){
            chooserModeSpinner.setSelection(0);
            lessonSpinner.setSelection(0);
        }
        return rootView;
    }

    public void loadSettings(ViewGroup rootView) {
        ((CheckBox) rootView.
                findViewById(R.id.hide_pron_check)).setChecked(Settings.hidePron);
        ((CheckBox) rootView.
                findViewById(R.id.hide_trans_check)).setChecked(Settings.hideTrans);

        ((CheckBox) rootView.
                findViewById(R.id.hide_word_check)).setChecked(Settings.hideWord);
        ((CheckBox) rootView.
                findViewById(R.id.auto_speak_check)).setChecked(Settings.isAutoSpeak);
        ((Switch) rootView.
                findViewById(R.id.switch_day_night)).setChecked(Settings.isNightMode);
        ((Spinner)rootView.findViewById(R.id.display_mode_list)).setSelection(Settings.entryType);

    }

    public void updateNightDayMode() {
        int textColor = Settings.isNightMode ? R.color.colorTextNight1 : R.color.colorTextDay1;
        ((CheckBox) rView.findViewById(R.id.hide_pron_check)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((CheckBox) rView.findViewById(R.id.hide_word_check)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((CheckBox) rView.findViewById(R.id.hide_trans_check)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((CheckBox) rView.findViewById(R.id.auto_speak_check)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((Switch) rView.findViewById(R.id.switch_day_night)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((TextView) rView.findViewById(R.id.label_lesson_list)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));
        ((TextView) rView.findViewById(R.id.label_mode_list)).
                setTextColor(StaticUtils.getColor(rView.getContext(), textColor));


        int styles[] = {R.drawable.button, R.drawable.button2};
        int colors[] = {R.color.colorBlueLight, R.color.dark_dark_blue};
        (rView.findViewById(R.id.save_config_button)).
                setBackgroundResource(Settings.isNightMode ? styles[0] : styles[1]);
        ((Button) rView.findViewById(R.id.save_config_button)).
                setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),
                        Settings.isNightMode ? colors[0] : colors[1]));

    }




    private static int prevTargetLang = -1;
    private static int prevSource1Lang = -1;
    private static int prevSource2Lang = -1;
    private static int prevCatSelected = -1;
    private static int prevLessonSelected = -1;

    /*--------------------------------------------*/
    /*-----------------INIT-----------------------*/
    /*--------------------------------------------*/
    /**
     * Initiation of the spinner for langage selection
     * @param rootView
     */
    public void initLangSettings(final ViewGroup rootView){
        Spinner sourceLangSpinner = (Spinner) rootView.findViewById(R.id.spinner_source_lang_chooser);
        Spinner sourceLangSpinner2 = (Spinner) rootView.findViewById(R.id.spinner_source_lang_chooser2);
        Spinner targetLangSpinner = (Spinner) rootView.findViewById(R.id.spinner_target_lang_chooser);
        //Array to show in lessonSpinner:

        final String[] choices = {getResources().getString(R.string.french),
                getResources().getString(R.string.japanese),
                getResources().getString(R.string.trad_chinese),
                getResources().getString(R.string.english)};

        Log.d("LANG_DEBUG", choices[0] + "   " + choices[1] +  "  "  +choices[2]);

        ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, choices) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(choices[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        sourceLangSpinner.setAdapter(customAdapter);
        sourceLangSpinner2.setAdapter(customAdapter);
        targetLangSpinner.setAdapter(customAdapter);

        final String selectedLang[] = { DBHelper.source1, DBHelper.source2, DBHelper.target};
        Spinner mSpinner[] = { sourceLangSpinner, sourceLangSpinner2, targetLangSpinner};

        for(int i=0; i<selectedLang.length; i++){
            final int index = i;
            final int selectedIndex;
            if(selectedLang[i].equals(DBHelper.COL_FR)){
                selectedIndex = 0;
            }else if(selectedLang[i].equals(DBHelper.COL_ZH)){
                selectedIndex = 2;
            }else if(selectedLang[i].equals(DBHelper.COL_JP)){
                selectedIndex = 1;
            }else{
                selectedIndex = 3;
            }
            mSpinner[i].setSelection(selectedIndex);

            mSpinner[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String newValue;
                    switch(position){
                        case 0:
                            newValue = DBHelper.COL_FR; break;
                        case 1:
                            newValue = DBHelper.COL_JP; break;
                        case 2:
                            newValue = DBHelper.COL_ZH; break;
                        case 3:
                            newValue = DBHelper.COL_EN_1; break;
                        default:
                            newValue = DBHelper.COL_FR;
                    }
                    boolean needListUpdate = false;
                    switch (index){
                        case 0: DBHelper.source1 = newValue;
                            if(prevSource1Lang != position)needListUpdate = true;
                            prevSource1Lang = position;
                            break;
                        case 1: DBHelper.source2 = newValue;
                            if(prevSource2Lang != position)needListUpdate = true;
                            prevSource2Lang = position;
                            break;
                        case 2: DBHelper.target = newValue;
                            if(prevTargetLang != position)needListUpdate = true;
                            prevTargetLang = position;
                            break;
                    }
                    if(needListUpdate)Settings.entryManager.updateList();



                    Log.d("LANGVALUE", DBHelper.source1 + "  " + DBHelper.source2 + "  " + DBHelper.target);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedLang[index] = DBHelper.COL_FR;
                }
            });


        }
    }
    public void initChooserModeSettings(ViewGroup rootView){
        chooserModeSpinner = (Spinner) rootView.findViewById(R.id.spinner_mode_choose);
        final ViewFlipper modeFlipper = (ViewFlipper)rootView.findViewById(R.id.flipper_voc_chooser_mode);
        modeFlipper.setDisplayedChild(Settings.curVocChooserMode);


        final String[] choices = getResources().getStringArray(R.array.array_mode_choose_voc);
        ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, choices) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(choices[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        chooserModeSpinner.setAdapter(customAdapter);
        chooserModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.curVocChooserMode = position;
                modeFlipper.setDisplayedChild(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initLessons(rootView);
        initCategories(rootView);
    }
    private void initLessons(ViewGroup rootView){
        lessonSpinner = (Spinner) rootView.findViewById(R.id.lesson_list);
        //Array to show in lessonSpinner:
        final String[] values = getResources().getStringArray(R.array.lesson_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, values) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(values[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        lessonSpinner.setAdapter(adapter);
        //listener listview click
        lessonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(Settings.curVocChooserMode != 0)return; // 0: By Lesson
                parentActivity.resetCumul();
                //Clicked item value
                String itemValue = (String) lessonSpinner.getItemAtPosition(position);
                //Load voc according to lesson selected
                int indexLesson = itemValue.lastIndexOf(" ");
                Settings.curLesson = Integer.parseInt(itemValue.substring(indexLesson).trim());
                if(prevLessonSelected == position)return;
                if (parentActivity != null)
                    parentActivity.setVocList(Settings.curLesson, -1, -1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lessonSpinner.setSelection(Settings.curLesson);
    }
    private void initCategories(ViewGroup rootView){
        categorySpinner = (Spinner) rootView.findViewById(R.id.spinner_category_list);
        //Array to show in lessonSpinner:
        final String[] values = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, values) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public boolean areAllItemsEnabled() {
                return super.areAllItemsEnabled();
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.support_simple_spinner_dropdown_item, null);
                }

                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setText(values[position]);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv.setBackgroundResource(R.drawable.button);
                return v;
            }
        };
        categorySpinner.setAdapter(adapter);
        //listener listview click
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(Settings.curVocChooserMode != 1)return;
                parentActivity.resetCumul();
                String itemValue = (String) categorySpinner.getItemAtPosition(position);
                Settings.curCategory = position;
                Log.i("CategoryIndex: ", String.valueOf(Settings.curCategory));
                if(position == prevCatSelected)return;
                prevCatSelected = position;
                if (parentActivity != null)
                    parentActivity.setVocList(-1, Settings.curCategory, -1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categorySpinner.setSelection(Settings.curCategory);
    }



}
