package com.daoliangshu.japonaischinois.core.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.MainActivity;
import com.daoliangshu.japonaischinois.core.db.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daoliangshu on 4/9/17.
 * Fragment View listing the vocabulary available for the given lesson or category
 */


public class VocListSlidePageFragment extends Fragment {
    private String txt;
    private int position;
    private ArrayList<VocUnit> countryList = null;
    private ViewGroup rootView;
    RecyclerView mVocList_RecyclerView;
    private VocabularyDisplayAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_voclist, container, false);

        Button btnSelectAll = (Button)rootView.findViewById(R.id.select_all);
        Button btnSelectNone = (Button)rootView.findViewById(R.id.select_none);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectAll();
            }
        });

        btnSelectNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
            }
        });
        displayListView(rootView);

        return rootView;
    }


    public Integer[] getSelectedVocsCodeAsArray(){
        ArrayList<Integer> res = new ArrayList<>();
        for(VocUnit vu: countryList){
            if(vu.isSelected()){
                res.add(vu.getCode());
            }
        }
        return res.toArray(new Integer[res.size()]);
    }

    public void setText(String text) {
        this.txt = text;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }



    public void selectAll(){
        //TODO rewrite this method for the recyclingView
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*
                for(int i=0; i< listView.getAdapter().getCount(); i++){
                    ((VocUnit)listView.getAdapter().getItem(i)).setSelected(true);
                }
                for(int i=0; i< listView.getChildCount(); i++){
                    ((CheckBox)listView.getChildAt(i).findViewById(R.id.checkBox1)).setChecked(true);
                }*/
            }
        });

    }

    public void unselectAll(){
        //TODO rewrite this method for the recyclingView
        getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
            /*
            for(int i=0; i< listView.getAdapter().getCount(); i++){
                ((VocUnit)listView.getAdapter().getItem(i)).setSelected(false);

            }
            for(int i=0; i< listView.getChildCount(); i++){
                ((CheckBox)listView.getChildAt(i).findViewById(R.id.checkBox1)).setChecked(false);
            }*/
        }
    });
    }

    private void displayListView(final ViewGroup container) {

        mVocList_RecyclerView = (RecyclerView)container.findViewById(R.id.rv_vocabulary_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        mVocList_RecyclerView.setLayoutManager(layoutManager);
        mVocList_RecyclerView.setHasFixedSize(true);
        countryList = new ArrayList<>();
        ArrayList<HashMap<String, String>> res = ((MainActivity)getActivity()).getCurrentVocList();
        if(res == null)return;
        Log.d("VOCLIST", "Countr:  " + res.size());
        for(HashMap<String, String> mMap: res){
            if(mMap == null)continue;
            String src1 = mMap.containsKey(DBHelper.source1)?mMap.get(DBHelper.source1):"";
            String src2 = mMap.containsKey(DBHelper.source2)?mMap.get(DBHelper.source2):"";
            String target = mMap.containsKey(DBHelper.target)?mMap.get(DBHelper.target):"";
            VocUnit vUnit = new VocUnit( src1, src2, target,
                                                Integer.valueOf(mMap.get(DBHelper.COL_ID)),
                                                true);
            countryList.add(vUnit);
        }
        mAdapter = new VocabularyDisplayAdapter(countryList, new VocabularyDisplayAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                Log.d("TAG", "itemIndex: " + clickedItemIndex);
            }
        });
        mVocList_RecyclerView.setAdapter(mAdapter);

        /*
        dataAdapter = new VocUnitListAdapter(getActivity().getApplicationContext(),
                R.layout.voc_info, countryList);
        listView = (ListView) rootView.findViewById(R.id.listView1);
        if(listView.getChildCount() > 0)listView.removeAllViews();
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                VocUnit country = (VocUnit) parent.getItemAtPosition(position);
            }
        });*/

    }
    




    private class VocUnitListAdapter extends ArrayAdapter<VocUnit> {
        private ArrayList<VocUnit> vocList;
        public VocUnitListAdapter(Context context, int textViewResourceId,
                                  ArrayList<VocUnit> vocList){
            super(context, textViewResourceId, vocList);
            this.vocList = new ArrayList<>();
            this.vocList.addAll(vocList);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.voc_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.code2 = (TextView) convertView.findViewById(R.id.code2);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

                final ViewFlipper vf = (ViewFlipper)convertView.findViewById(R.id.flipper);
                vf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.setDisplayedChild( (vf.getDisplayedChild()+1)%vf.getChildCount());
                    }
                });

                convertView.setTag(holder);
                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        VocUnit country = (VocUnit) cb.getTag();
                        country.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            VocUnit country = vocList.get(position);
            holder.code.setText(" " + country.getSource2());
            holder.code2.setText(country.getTarget());
            holder.name.setText(country.getSource1() +  " (" +  country.getCode() + ")");
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;

        }

    }

    
}


class VocUnit{
    private String source1;
    private String source2;
    private String tartget;
    int vocId = -1;
    boolean selected = false;

    public VocUnit(String source1, String source2, String target, int vocId, boolean selected){
        this.source1 = source1;
        this.source2 = source2;
        this.tartget = target;
        this.vocId = vocId;
        this.selected = selected;
    }


    public String getSource2(){ return this.source2; }

    public String getTarget(){ return this.tartget; }

    public int getCode(){ return this.vocId; }

    public String getSource1(){ return this.source1; }

    public void setName(String name){ this.source1 = name; }

    public boolean isSelected(){ return this.selected; }

    public void setSelected(boolean selected){
        this.selected = selected;
    }



}


class ViewHolder{
    TextView code;
    TextView code2;
    CheckBox name;
}





