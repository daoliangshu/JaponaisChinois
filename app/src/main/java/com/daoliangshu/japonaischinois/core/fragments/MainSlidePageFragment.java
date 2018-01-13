package com.daoliangshu.japonaischinois.core.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.MainActivity;
import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.core.data.StaticUtils;
import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.core.grammar.GrammarActivity;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.OpenGLES20Activity;
import com.daoliangshu.japonaischinois.sentencereordering.SentenceReorderingActivity;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 2/5/17.
 */

public class MainSlidePageFragment extends Fragment {

    private MainActivity parentActivity;
    private TextView mWordView;
    private TextView mWordView2;
    private TextView mTransView;
    private TextView mPhonView;
    private ViewGroup rootView;

    //Handling timer
    private Handler handler = new Handler();


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_flashcard, container, false);
        parentActivity = (MainActivity) getActivity();
        mWordView = (TextView) rootView.findViewById(R.id.word_content);
        mWordView2 = (TextView) rootView.findViewById(R.id.word_content2);
        mWordView.setMovementMethod(new ScrollingMovementMethod());
        mWordView2.setMovementMethod(new ScrollingMovementMethod());

        mTransView = (TextView) rootView.findViewById(R.id.trans_content);
        mPhonView = (TextView) rootView.findViewById(R.id.phonetic_content);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        rootView.findViewById(R.id.word_flipper).setMinimumHeight(height / 3);
        rootView.findViewById(R.id.word_flipper).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int curChild = ((ViewFlipper) v).getDisplayedChild();
                ((ViewFlipper) v).setDisplayedChild((curChild + 1) % 2);
            }
        });

        rootView.findViewById(R.id.trans_content).setMinimumHeight(height / 3);
        rootView.findViewById(R.id.trans_flipper).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int curChild = ((ViewFlipper) v).getDisplayedChild();
                ((ViewFlipper) v).setDisplayedChild((curChild + 1) % 2);
            }
        });

        rootView.findViewById(R.id.phon_flipper).setMinimumHeight(height / 3);
        rootView.findViewById(R.id.phon_flipper).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int curChild = ((ViewFlipper) v).getDisplayedChild();
                ((ViewFlipper) v).setDisplayedChild((curChild + 1) % 2);
            }
        });


        Button nextButton = (Button) rootView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.nextWord();
                getCurrent();

            }
        });
        Button prevButton = (Button) rootView.findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.previousWord();
                getCurrent();
            }
        });

        Button goToGameButton = (Button) rootView.findViewById(R.id.button_back0);
        if(Settings.dbEntryManager.getVocCount() <= 0){
            //Disable button if no vocabulary
            goToGameButton.setEnabled(false);
        }
        goToGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer[] vocIdsAsArray = ((MainActivity)getActivity()).
                                getCurVocListCodesAsArray();
               Intent intent = new Intent(getContext(), OpenGLES20Activity.class);
                ArrayList<Integer> bundledVocIds =  new ArrayList<Integer>();
                 for(int i=0; i< vocIdsAsArray.length; i++){
                    bundledVocIds.add(vocIdsAsArray[i]);
                }
                intent.putIntegerArrayListExtra("vocCodes",bundledVocIds);
                startActivity(intent);

            }
        });

        Button goToReorderingSentenceGame = (Button) rootView.findViewById(R.id.button_bottom_1);
        goToReorderingSentenceGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SentenceReorderingActivity.class);
                startActivity(intent);
            }
        });

        Button goToInfoButton = (Button) rootView.findViewById(R.id.button_info);
        goToInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.flipView(1);

            }
        });

        goToGameButton.setEnabled(true);

        Button mSpeakButton = (Button) rootView.findViewById(R.id.speak_button);
        mSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.speak();
            }
        });

        Button mGrammarButton = (Button) rootView.findViewById(R.id.grammar_button);
        mGrammarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GrammarActivity.class);
                intent.putExtra("lessonIndex", Settings.curLesson);
                startActivity(intent);
            }
        });

        //getCurrent();

        handler.postAtTime(runnable, System.currentTimeMillis() + Settings.curInterval);
        handler.postDelayed(runnable, Settings.curInterval);
        parentActivity.setFlashCardFragment(this);
        updateNightDayMode();
        return rootView;
    }

    private void getCurrent() {
        updateHiddenState();
        mWordView.setText(parentActivity.getCurrent(DBHelper.source1));
        mWordView2.setText(parentActivity.getCurrent(DBHelper.source2));

        mTransView.setText(parentActivity.getCurrent(DBHelper.target));
        mPhonView.setText(parentActivity.getCurrent(DBHelper.getPhoneticTable(DBHelper.target)));


    }

    private Runnable runnable = new Runnable() {
        public void run() {
            parentActivity.nextWord();
            getCurrent();
            handler.postAtTime(runnable, System.currentTimeMillis() + Settings.curInterval);
            handler.postDelayed(runnable, Settings.curInterval);

        }
    };

    public void updateInterval() {
        handler.removeCallbacks(runnable);
        handler.postAtTime(runnable, System.currentTimeMillis() + Settings.curInterval);
        handler.postDelayed(runnable, Settings.curInterval);

    }
    public void updateNightDayMode() {
        boolean mode = Settings.isNightMode;
        int textColor = Settings.isNightMode ? R.color.colorTextNight1 : R.color.colorTextDay1;
        mWordView.setTextColor(StaticUtils.getColor(getContext(), textColor));
        mWordView2.setTextColor(StaticUtils.getColor(getContext(), textColor));
        mTransView.setTextColor(StaticUtils.getColor(getContext(), textColor));
        mPhonView.setTextColor(StaticUtils.getColor(getContext(), textColor));

        int styles[] = {R.drawable.button, R.drawable.button2};
        int colors[] = {R.color.colorBlueLight, R.color.dark_dark_blue};
        ((Button) rootView.findViewById(R.id.next_button)).setBackgroundResource(mode ? styles[0] : styles[1]);
        ((Button) rootView.findViewById(R.id.next_button)).
                setTextColor(ContextCompat.getColor(getContext(), mode ? colors[0] : colors[1]));
        (rootView.findViewById(R.id.prev_button)).setBackgroundResource(mode ? styles[0] : styles[1]);
        ((Button) rootView.findViewById(R.id.prev_button)).
                setTextColor(ContextCompat.getColor(getContext(), mode ? colors[0] : colors[1]));
        ((Button) rootView.findViewById(R.id.speak_button)).
                setTextColor(ContextCompat.getColor(getContext(), mode ? colors[0] : colors[1]));
        (rootView.findViewById(R.id.grammar_button)).setBackgroundResource(mode ? styles[0] : styles[1]);
        ((Button) rootView.findViewById(R.id.grammar_button)).
                setTextColor(ContextCompat.getColor(getContext(), mode ? colors[0] : colors[1]));
    }
    public void updateHiddenState() {
        final int states[] = {
                Settings.hideWord ? 1 : 0,
                Settings.hidePron ? 1 : 0,
                Settings.hideTrans ? 1 : 0
        };

        final int resId[] = {R.id.word_flipper, R.id.phon_flipper, R.id.trans_flipper};
        for (int i = 0; i < resId.length; i++) {
            ((ViewFlipper) rootView.findViewById(resId[i])).setDisplayedChild(states[i]);
        }

    }
    public void pauseInterval() {
        if (handler != null) handler.removeCallbacks(runnable);

    }
    public void resumeInterval() {
        if (handler != null) {
            handler.postAtTime(runnable, System.currentTimeMillis() + Settings.curInterval);
            handler.postDelayed(runnable, Settings.curInterval);
        }
    }
}
