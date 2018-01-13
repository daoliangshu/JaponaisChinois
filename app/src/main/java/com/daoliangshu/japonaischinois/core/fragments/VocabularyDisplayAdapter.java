package com.daoliangshu.japonaischinois.core.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.R;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 11/22/17.
 */

public class VocabularyDisplayAdapter extends RecyclerView.Adapter<VocabularyDisplayAdapter.VocabularyItemViewHolder> {

    private static final String TAG = VocabularyDisplayAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;
    private ArrayList<VocUnit> mVocList;
    private int nItemCount = 0;

    /*
        Constructor -Adapter-
     */
    public VocabularyDisplayAdapter(ArrayList<VocUnit> vocList, ListItemClickListener listener){
        mVocList = vocList;
        mOnClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public VocabularyItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.voclist_entry_element;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shoudAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, shoudAttachToParentImmediately);
        VocabularyItemViewHolder vocabularyItemViewHolder =
                new VocabularyItemViewHolder(view, nItemCount);
        nItemCount++;
        return  vocabularyItemViewHolder;
    }
    @Override
    public void onBindViewHolder(VocabularyItemViewHolder viewHolder, int position){
        viewHolder.bind(mVocList.get(position), position);
    }

    @Override
    public int getItemCount(){
        return mVocList==null?0:mVocList.size();
    }

    class VocabularyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView[] mContent_2x2_TextView;
        private CheckBox isSelected_CheckBox;
        public VocabularyItemViewHolder(View itemView, final int viewHolderPosition){
            super(itemView);
            mContent_2x2_TextView = new TextView[4];
            mContent_2x2_TextView[0] = (TextView)itemView.findViewById(R.id.tv_content_0_0);
            mContent_2x2_TextView[1] = (TextView)itemView.findViewById(R.id.tv_content_0_1);
            mContent_2x2_TextView[2] = (TextView)itemView.findViewById(R.id.tv_content_1_0);
            mContent_2x2_TextView[3] = (TextView)itemView.findViewById(R.id.tv_content_1_1);
            isSelected_CheckBox = (CheckBox)itemView.findViewById(R.id.cb_select_box);
        }

        void bind(VocUnit vocUnit, final int position){
            isSelected_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mVocList.get(position).setSelected(isChecked);
                }
            });
            mContent_2x2_TextView[0].setText(String.valueOf(vocUnit.getSource1()));
            mContent_2x2_TextView[1].setText(String.valueOf(vocUnit.getSource2()));
            mContent_2x2_TextView[2].setText(String.valueOf(vocUnit.getTarget()));
            if(mVocList.get(position).isSelected()){
                isSelected_CheckBox.setChecked(true);
            }else{
                isSelected_CheckBox.setChecked(false);
            }
        }
        @Override
        public void onClick(View view){
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
