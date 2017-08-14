package com.daoliangshu.japonaischinois.lettrabulle.opengl.menu_components;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.OpenGLES20Activity;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;

/**
 * Created by daoliangshu on 2017/7/20.
 */

public class GameSettingTopSlideFragment extends Fragment {

    private Activity parentActivity;
    private TextView mWordView;
    private TextView mTransView;
    private TextView mTime;
    private TextView mScore;
    private ViewGroup rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.game_settings_fragment, container, false);
        parentActivity = getActivity();
        mWordView = (TextView) rootView.findViewById(R.id.bulle_word_to_guess);
        mTransView = (TextView) rootView.findViewById(R.id.bulle_trans_of_word);
        mTime = (TextView) rootView.findViewById(R.id.bulle_time);
        mScore = (TextView) rootView.findViewById(R.id.bulle_score);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        /*--------_Speed Settings -------------*/
        RadioGroup radioGroupSpeed = (RadioGroup) rootView.findViewById(R.id.radioGroupSpeed);
        int currSpeed = Config.getSpeedCode();
        int selectedRadio = 0;
        switch (currSpeed) {
            case 0:
                selectedRadio = R.id.radio_slow;
                break;
            case 2:
                selectedRadio = R.id.radio_fast;
                break;
            default:
                selectedRadio = R.id.radio_medium;
                break;
        }
        RadioButton selectedRdb = (RadioButton) rootView.findViewById(selectedRadio);



        radioGroupSpeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_slow:
                        Config.currentSpeed = Config.SPEED_SLOW;
                        break;
                    case R.id.radio_medium:
                        Config.currentSpeed = Config.SPEED_MEDIUM;
                        break;
                    case R.id.radio_fast:
                        Config.currentSpeed = Config.SPEED_FAST;
                        break;
                    default:
                        Config.currentSpeed = Config.SPEED_MEDIUM;
                }
                if(parentActivity instanceof OpenGLES20Activity)
                    ((OpenGLES20Activity)parentActivity).updateSpeed();
            }
        });

        Button mBtnBack = (Button)rootView.findViewById(R.id.lettrabulle_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof OpenGLES20Activity)
                    ((OpenGLES20Activity)getActivity()).settingViewMoveLeft();
            }
        });


        return rootView;
    }
}