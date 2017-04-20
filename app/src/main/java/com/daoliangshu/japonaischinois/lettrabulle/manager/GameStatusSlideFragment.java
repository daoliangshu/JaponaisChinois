package com.daoliangshu.japonaischinois.lettrabulle.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.DBHelper;
import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.lettrabulle.LB_Config;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by daoliangshu on 2/5/17.
 */

public class GameStatusSlideFragment extends Fragment {

    private Activity parentActivity;
    private TextView mWordView;
    private TextView mTransView;
    private TextView mTime;
    private TextView mScore;
    private Button mBtnSwitchDstWord;
    private Timer timer;
    private boolean timeRunning = true;
    private int timerValue;
    private int maxTimerValue;
    private ViewGroup rootView;

    private Context context;
    private int timeValue = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.game_status_fragment, container, false);
        parentActivity = getActivity();
        timerValue = 0;
        maxTimerValue = 9000;

        mBtnSwitchDstWord = (Button)rootView.findViewById(R.id.btn_status_switch);
        mBtnSwitchDstWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDstMode();
            }
        });

        mWordView = (TextView) rootView.findViewById(R.id.bulle_word_to_guess);
        mTransView = (TextView) rootView.findViewById(R.id.bulle_trans_of_word);
        mTime = (TextView) rootView.findViewById(R.id.bulle_time);
        mScore = (TextView) rootView.findViewById(R.id.bulle_score);
        mTransView.setMovementMethod(new ScrollingMovementMethod());
        mWordView.setMovementMethod(new ScrollingMovementMethod());
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Thread timerUpdateThread = new Thread(new Runnable() {
            public void run() {
                if (timeRunning) {
                    timerValue += 1;
                    if (timerValue >= maxTimerValue) {
                        timerValue = 0;
                        setTimer(timerValue);
                    } else {
                        incrementTimer();
                    }
                }

            }
        });
        timer = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                if (getActivity() != null)
                    getActivity().runOnUiThread(timerUpdateThread);
            }
        };
        timer.schedule(timerTaskObj, 0, 1500);
        toggleDstMode();
        return rootView;
    }


    public void toggleDstMode() {
        int modes[] = {LB_Config.DST_FROM_TRANS_1, LB_Config.DST_FROM_TRANS_MIXED };
        int current = 0;
        for(int i=0; i < modes.length; i++){
            if(modes[i] == LB_Config.curDstType){
                current = i;
                break;
            }
        }
        LB_Config.curDstType = modes[ (current + 1) % modes.length ];
        String mArray[] = getContext().getResources().getStringArray(R.array.dst_word_mode);
        switch( modes[ (current + 1) % modes.length]){
            case LB_Config.DST_FROM_TRANS_1:
                mBtnSwitchDstWord.setText(mArray[0]);
                break;
            case LB_Config.DST_FROM_TRANS_2:
                mBtnSwitchDstWord.setText(mArray[1]);
                break;
            case LB_Config.DST_FROM_TRANS_MIXED:
                mBtnSwitchDstWord.setText(mArray[2]);
                break;
            default:
        }
    }


    public void setWord(String newWord) {
        this.mWordView.setText(newWord);
        this.mWordView.postInvalidate();
    }

    public void setWord(String newWord, boolean fetchTrans, DBHelper dbHelper) {
        if (fetchTrans && newWord.trim().length() > 0) {
            String content;
            String trans;
            trans = dbHelper.getWordTrans(newWord.trim());
            content = "";
            if (trans != null) {
                this.setTrans(trans);
            } else {
                this.setTrans("");
                this.setWord(newWord);
            }
        } else {
            setWord(newWord);
        }
    }



    public void setTrans(String newTrans) {
        this.mTransView.setText(newTrans);
        this.mTransView.postInvalidate();
    }

    public void setTimer(int newValue) {
        this.timeValue = newValue;
        int minutes = (newValue % 3600) / 60;
        int seconds = newValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.mTime.setText(timeString);
    }

    public void setScore(int score) {
        this.mScore.setText(String.valueOf(score));
    }

    public void incrementScore(int inc) {
        String scoreText = this.mScore.getText().toString();
        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException nfe) {
            score = 0;
        }
        score += inc;
        this.mScore.setText(String.valueOf(score));
    }

    public void incrementTimer() {
        this.timeValue += 1;
        int minutes = (this.timeValue % 3600) / 60;
        int seconds = this.timeValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.mTime.setText(timeString);
    }


    /*------------------------------------------------*/
    /*-------------------GETTERS----------------------*/
    /*------------------------------------------------*/
    public int getScore() {
        try {
            return Integer.parseInt(this.mScore.getText().toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public void pauseTimer() {
        timeRunning = false;
        timerValue = 0;
    }

    public void restartTimer() {
        timeRunning = true;
        timerValue = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeRunning = false;
        timer.purge();
    }
}
