package com.daoliangshu.japonaischinois.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.daoliangshu.japonaischinois.core.data.Settings;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * This class has several purpose:
 * 1)Take care of creating vocabulary sets according to language settings
 * 2)Store statistics on the use of these sets
 */
public class StatisticalDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "statistic_db";
    private static String dbPath;
    private SQLiteDatabase mDb;

    private static final String TB_SETS_OF_IDS = "sets_of_ids";

    //Table SET_INFOS
    private static final String TB_SET_INFOS = "set_infos";
    public static final String COL_TBINFO_SUBSET_COUNT = "subset_count";
    private static final String COL_TBINFO_IDS_COUNT = "total_ids";
    private static final String COL_TBINFO_LAST_USED = "last_used";
    private static final String COL_TBINFO_LAST_DATE = "last_date";

    private static final String COL_ID = "_id";

    private static final String COL_ELE_COUNT = "ele_count";
    private static final String COL_ROOT_NAME = "root_name";
    private static final String COL_INDEX = "set_index";
    private static final String COL_WORD_IDS = "word_ids";
    private static final String COL_FINISHED_CNT = "finished_count";

    private Random rand = new Random();

    /*---------------------------------------------*/
    /*--------------CONSTRUCTOR--------------------*/
    /*---------------------------------------------*/
    public StatisticalDatabase(Context context) throws SQLException {
        super(context, DB_NAME, null, 3);
        dbPath = context.getFilesDir().getPath();
        openDB();
        /*
        Cursor c = mDb.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Toast.makeText(context.getApplicationContext(), "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();
                c.moveToNext();
            }
        }
        c.close();
        */
    }

    /*---------------------------------------------*/
    /*--------------INIT---------------------------*/
    /*---------------------------------------------*/
    private void openDB() throws SQLException {

        String path = dbPath + "/" + DB_NAME;
        File dbFile = new File(path);

        if (!dbFile.exists() || Settings.REQUEST_UPDATE) {
            initDb(SQLiteDatabase.openOrCreateDatabase(path, null));
            mDb.close();
        }

        mDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        dropTable(TB_SET_INFOS);
        dropTable(TB_SETS_OF_IDS);
        initDb(SQLiteDatabase.openOrCreateDatabase(path, null));
    }
    private void dropTable(String table) {
        String q = "DROP TABLE IF EXISTS " + table;
        mDb = getWritableDatabase();
        mDb.execSQL(q);
    }
    private void initDb(SQLiteDatabase database) {
        try {
            mDb = database;
            dropTable(TB_SET_INFOS);
            dropTable(TB_SETS_OF_IDS);
            final String DATABASE_CREATE = "CREATE TABLE " + TB_SETS_OF_IDS +
                    " (" +
                    COL_ID + " integer primary key autoincrement unique, " +
                    COL_INDEX + " integer not null, " +
                    COL_ELE_COUNT + " integer not null, " +
                    COL_ROOT_NAME + " text not null, " +
                    COL_FINISHED_CNT + " integer not null," +
                    COL_WORD_IDS + " text " +
                    ")";
            mDb.execSQL(DATABASE_CREATE);

            final String INFO_TABLE_CREATE = "CREATE TABLE " + TB_SET_INFOS +
                    " (" +
                    COL_ROOT_NAME + " text not null, " +
                    COL_TBINFO_SUBSET_COUNT + " integer not null, " +
                    COL_TBINFO_IDS_COUNT + " integer not null, " +
                    COL_TBINFO_LAST_USED + " integer, " +
                    COL_TBINFO_LAST_DATE + " text " +
                    ")";
            mDb.execSQL(INFO_TABLE_CREATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------*/
    /*-------------------EVENTS--------------------*/
    /*---------------------------------------------*/
    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /*---------------------------------------------*/
    /*-----------UPDATE INSERT CREATE--------------*/
    /*---------------------------------------------*/
    private boolean insertInfoRow(String setName, int eleCount){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ROOT_NAME, setName);
        contentValues.put(COL_TBINFO_IDS_COUNT, eleCount);
        contentValues.put(COL_TBINFO_SUBSET_COUNT, 1);
        mDb = getWritableDatabase();
        return mDb.insert(TB_SET_INFOS, null, contentValues) != -1;
    }
    private void updateInfoRow(String setName, int eleCount){
        String params[] = new String[]{
                String.valueOf(eleCount),
                "1",
                setName
        };
        mDb = getWritableDatabase();
        final String q = "UPDATE " + TB_SET_INFOS + " SET " +
                COL_TBINFO_IDS_COUNT + "= " + COL_TBINFO_IDS_COUNT + " + ?, " +
                COL_TBINFO_SUBSET_COUNT + "= " + COL_TBINFO_SUBSET_COUNT + " + ? " +
                "WHERE " + COL_ROOT_NAME + "=?;";
        mDb.execSQL(q, params);
    }
    /**
     * Create a subset of word ids from a set of ids,
     *      where no common ids between subsets
     * @return true if insertion is successful
     */
    public boolean createSubSet(String setName,
                                int elementCount,
                                ArrayList<Integer> setValues) {
        ArrayList<Integer> res;
        if(getInfoRow(setName) != null){
            res = getNotInPreviousSubsetsIds(setName, setValues);
        }else{
            res = setValues;
        }
    //TODO (1) Create Subset Manager
        if(res == null || res.size() == 0)return false;
        int subsetIndex = getSubsetCount(setName);
        String setIds = getIdsNFromList(res, elementCount);
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ROOT_NAME, setName);
        contentValues.put(COL_ELE_COUNT, elementCount);
        contentValues.put(COL_INDEX,subsetIndex );
        contentValues.put(COL_FINISHED_CNT, 0);
        contentValues.put(COL_WORD_IDS, setIds);
        mDb = getWritableDatabase();
        if(mDb.insert(TB_SETS_OF_IDS, null, contentValues) != -1){
            if(subsetIndex == 0){
                return insertInfoRow(setName, elementCount);
            }else{
                updateInfoRow(setName, elementCount);
                return true;
            }
        }
        return false;
    }

    /*---------------------------------------------*/
    /*-------------------SETTERS-------------------*/
    /*---------------------------------------------*/
    public boolean updateLastUsedInfo(String rootName, int subsetIndex) {
        getWritableDatabase();
        DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm", Locale.ENGLISH);
        String date = df.format(Calendar.getInstance().getTime());

        String[] params = new String[]{
                date,
                String.valueOf(subsetIndex),
                rootName
        };
        String q = "UPDATE " + TB_SET_INFOS + " SET " +
                COL_TBINFO_LAST_DATE + "=?, " + COL_TBINFO_LAST_USED + "=? WHERE " + COL_ROOT_NAME + "=?;";
        mDb.execSQL(q, params);
        return true;
    }

    /*---------------------------------------------*/
    /*-------------------GETTERS-------------------*/
    /*---------------------------------------------*/
    public String[] getIdsFromSet(String setName, int setIndex) {
        mDb = getReadableDatabase();
        String[] params = new String[]{setName, String.valueOf(setIndex)};
        String q = "SELECT " + COL_WORD_IDS + " FROM " + TB_SETS_OF_IDS +
                " WHERE " + COL_ROOT_NAME + "=? AND " +
                COL_INDEX + "=" + setIndex;
        Cursor c = mDb.rawQuery(q, params);
        if (!c.moveToFirst()) return null;
        String[] res = (c.getString(0).split("|"));
        c.close();
        return res;
    }
    public ArrayList<Integer> getPreviousSubsetIds(String setName) {
        String q = "SELECT " + COL_WORD_IDS + " FROM " + TB_SETS_OF_IDS +
                " WHERE " + COL_ROOT_NAME + "=?";
        Cursor c = mDb.rawQuery(q, new String[]{setName});
        ArrayList<Integer> res = new ArrayList<>();
        while (c.moveToNext()) {
            String idsFromIndex = c.getString(0);
            Log.d("EEE", idsFromIndex);
            for (String idx : idsFromIndex.split("|")) {
                if(idx.contains("|") || idx.equals(""))continue;
                if (!res.contains(Integer.valueOf(idx))) {
                    res.add(Integer.valueOf(idx));
                }
            }
        }
        c.close();
        return res;
    }
    public ArrayList<Integer> getNotInPreviousSubsetsIds(String setName,
                                                         ArrayList<Integer> candidateIds) {
        ArrayList<Integer> subsetsIds = getPreviousSubsetIds(setName);
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer entry : candidateIds) {
            if (!subsetsIds.contains(entry)) {
                res.add(entry);
            }
        }
        return res;
    }
    public HashMap<String, String> getInfoRow(String setName){
        String params[] = new String[]{
                setName
        };
        final String q = "SELECT " + COL_ROOT_NAME + ", " +
                COL_TBINFO_IDS_COUNT + ", " +
                COL_TBINFO_SUBSET_COUNT + ", " +
                COL_TBINFO_LAST_DATE + ", " +
                COL_TBINFO_LAST_USED + " " +
                "FROM " + TB_SET_INFOS + " " +
                "WHERE " + COL_ROOT_NAME + "=?";
        mDb = getReadableDatabase();
        Cursor cursor = mDb.rawQuery(q, params);
        if(!cursor.moveToFirst())return null;
        HashMap<String, String> res = new HashMap<>();
        res.put(COL_ROOT_NAME, cursor.getString(0));
        res.put(COL_TBINFO_IDS_COUNT, cursor.getString(1));
        res.put(COL_TBINFO_SUBSET_COUNT, cursor.getString(2));
        res.put(COL_TBINFO_LAST_DATE, cursor.getString(3));
        res.put(COL_TBINFO_LAST_USED, cursor.getString(4));
        cursor.close();
        return res;
    }
    public ArrayList<Integer> getIdsFromSubset(String setName, int subsetIndex){
        HashMap<String, String> temp = getSubsetInformations(setName, subsetIndex);
        if(temp == null)return null;
        return getListSubsetIdsFromColumnFormatedContent(temp.get(COL_WORD_IDS));
    }
    private ArrayList<Integer> getListSubsetIdsFromColumnFormatedContent(String idsColumnIds){
        if(idsColumnIds == null || idsColumnIds.length() <= 0)return null;
        String[] temp = idsColumnIds.split("\\|");
        ArrayList<Integer> res= new ArrayList<>();
        for(String str: temp){
            if(!str.equals("") && !str.equals("|")){
                res.add(Integer.valueOf(str));
            }
        }
        return res;
    }
    /**
     * Select n random ids from a set given as parameters
     * @return String of ids in form "|n1|n2|...|n|"
     */
    private String getIdsNFromList(ArrayList<Integer> ids, int n) {
        String res = "|";
        if (ids.size() < n) {
            for (Integer mId : ids) {
                res += mId.toString() + "|";
            }
        } else {
            Collections.shuffle(ids);
            for (int i = 0; i < n; i++) {
                res += ids.get(i) + "|";
            }
        }
        return res;
    }
    public int getSubsetCount(String setName) {
        String q = "SELECT " + COL_TBINFO_SUBSET_COUNT + " FROM " + TB_SET_INFOS +
                " WHERE " + COL_ROOT_NAME + "=?";
        mDb = getReadableDatabase();
        Cursor c = mDb.rawQuery(q, new String[]{setName});
        c.moveToFirst();
        if (!c.moveToFirst()) return 0;
        int res = c.getInt(0);
        c.close();
        return res;
    }
    public HashMap<String, String> getSubsetInformations(String setName, int index) {
        String[] params;
        String q = "SELECT * FROM " + TB_SETS_OF_IDS + " WHERE " + COL_ROOT_NAME + "=?";
        if (index == -1) {
            params = new String[]{setName};
        } else {
            params = new String[]{setName, String.valueOf(index)};
            q += " AND " + COL_INDEX + "=?";
        }
        Cursor c = mDb.rawQuery(q, params);
        if(!c.moveToFirst()){
            c.close();
            return null;
        }
        HashMap<String, String> entry = new HashMap<>();
        entry.put(COL_ID, c.getString(0));
        entry.put(COL_INDEX, c.getString(1));
        entry.put(COL_ELE_COUNT, c.getString(2));
        entry.put(COL_ROOT_NAME, c.getString(3));
        entry.put(COL_FINISHED_CNT, c.getString(4));
        entry.put(COL_WORD_IDS, c.getString(5));
        c.close();
        return entry;
    }

    public void close(){
        if(mDb != null && mDb.isOpen()){
            mDb.close();
        }
    }
}