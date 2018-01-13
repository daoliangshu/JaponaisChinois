package com.daoliangshu.japonaischinois.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.daoliangshu.japonaischinois.core.data.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Created by daoliangshu on 1/28/17.
 * Class containing the db connection,
 * provides methods to get data from db.
 */

public class DBHelper extends SQLiteOpenHelper {

    public final static String COL_ID = "_id";
    public final static String COL_ZH = "zh_1";
    public final static String COL_FR = "fr_1";
    public final static String COL_JP = "jp_1";
    private final static String COL_FR_2 = "fr_2"; // will store genre
    private final static String COL_ZH_2 = "zh_1"; //will store zhuyin
    private final static String COL_JAP_2 = "jp_2"; //store kanji
    public final static String COL_EN_1 = "en_1";
    private final static String COL_EN_2 = "en_1";

    public static String source1 = "zh_1";
    public static String source2 = "fr_1";
    public static String target = "jp_1";


    private final static String COL_LESSON = "lesson";
    private final static String COL_THEMATIC = "thematic";
    private final static String COL_LEVEL = "lv";
    public final static String TB_BASIC = "dico";
    private final static String TB_SENTENCES = "sentences";

    private final static String COL_DEST_WORD = "dst_word";
    private final static String COL_DEST_WORD2 = "dst_word2";

    private SQLiteDatabase myDB;
    private static String DB_PATH;
    private static final String DB_NAME = "db_dico";
    private final Context myContext;
    private Random rand;

    public DBHelper(Context context) throws SQLException {
        super(context, DB_NAME, null, 3);
        myContext = context;
        DB_PATH = myContext.getFilesDir().getPath();
        initDb();
        rand = new Random();
    }

    /*---------------------------------------------*/
    /*--------------INIT,CONTROL-------------------*/
    /*---------------------------------------------*/
    private void initDb() throws SQLException {
        String path = DB_PATH + "/" + DB_NAME;
        File dbFile = new File(path);
        if (!dbFile.exists() || Settings.REQUEST_UPDATE) {
            try {
                copyDB(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        if(!open()){ System.exit(-1);}
    }

    public boolean open(){
        String path = DB_PATH + "/" + DB_NAME;
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            myDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            Log.i("DB", "Opend db succesfully !!");
            return true;

        } else {
            Log.i("ERR", "File not found");
            return false;
        }
    }

    public void copyDB(File dbFile) throws IOException {
        //Open local db as input stream
        Log.e("Err0", "Could not open a stream");
        InputStream input = myContext.getAssets().open(DB_NAME);
        //Path to the new created empty db
        Log.e("Info", "Opening:" + dbFile.toString());


        OutputStream output = new FileOutputStream(dbFile);
        Log.e("Err", "Could not open a stream");

        //transfer bytes from inputfile to outputfile
        byte[] buffer = new byte[1024];
        Log.e("Err3", "Could not open a stream");
        while (input.read(buffer) > 0) {
            output.write(buffer);
        }
        Log.i("CopyDB", "OK");
        //close
        output.flush();
        output.close();
        input.close();
    }

    public synchronized void close() {
        if (myDB != null && myDB.isOpen()) myDB.close();
        super.close();
    }


    /*---------------------------------------------*/
    /*--------------EVENTS---------------------------*/
    /*---------------------------------------------*/
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    /*---------------------------------------------*/
    /*--------------GETTERS------------------------*/
    /*---------------------------------------------*/
    public static String getPhoneticTable(String table) {
        switch (table) {
            case DBHelper.COL_FR:
                return DBHelper.COL_FR_2;
            case DBHelper.COL_ZH:
                return DBHelper.COL_ZH_2;
            case DBHelper.COL_JP:
                return DBHelper.COL_JAP_2;
            case DBHelper.COL_EN_1:
                return DBHelper.COL_EN_2;
            default:
                return DBHelper.COL_FR_2;
        }
    }


    private boolean getEntryExist(final String table, String colName, String value) {
        try {
            String params[] = new String[]{table, colName, value};
            String q = "SELECT " + COL_ID + " FROM ? WHERE ?=?";
            Cursor c = myDB.rawQuery(q, params);
            if (c.moveToFirst()) {
                c.close();
                return true;
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param mode        : 1:startWith patter, 0: exact match
     * @return Array with Hashmaps corresponding to each entry
     */
    public ArrayList<HashMap<String, String>> getTargetWordByStartWith(String colWord,
                                                                       int mode,
                                                                       int lessonIndex) {
        String query = "SELECT " +
                COL_ID + ", " +
                target + ", " + getPhoneticTable(target) + ", " + source1 +
                ", " + source2 +
                " FROM " + TB_BASIC;
        if (mode == 1)
            query += " WHERE " + source1 + " LIKE '" + colWord + "%' ";
        else
            query += " WHERE " + source1 + "=\"" + colWord + "\"";
        if (lessonIndex > 0 && Settings.curVocChooserMode == 0) {
            query += " AND " + COL_LESSON + "=" + lessonIndex;
        } else if (lessonIndex >= 0 && Settings.curVocChooserMode == 1) {
            query += " AND " + COL_THEMATIC + "=" + lessonIndex;
        }
        query += " AND " + target + " IS NOT NULL";
        query += ";";
        Log.i("QUERY_", query);
        Cursor c = myDB.rawQuery(query, null);
        ArrayList<HashMap<String, String>> resList = new ArrayList<>();
        while (c.moveToNext()) {
            HashMap<String, String> res = new HashMap<>();
            res.put(COL_ID, c.getString(0));
            String formatedTarget = c.getString(2) != null ? c.getString(2).trim() : c.getString(2);

            String formatedTarget0 = c.getString(1) != null ? c.getString(1).trim() : c.getString(1);
            res.put(target, formatedTarget0);
            res.put(getPhoneticTable(target), formatedTarget);
            res.put(source1, c.getString(3) != null ? c.getString(3).trim() : c.getString(3));
            res.put(source2, c.getString(4) != null ? c.getString(4).trim() : c.getString(4));
            resList.add(res);
        }
        c.close();
        return resList;
    }


    // Compability methods
    public String getTransById(int tid) {
        String query = "SELECT " +
                target + " " +
                "FROM " + TB_BASIC +
                " WHERE " + COL_ID + "=" + String.format(Locale.ENGLISH, "%d", tid) + ";";
        Cursor c = myDB.rawQuery(query, null);
        while (c.moveToNext()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }

    public String getWordById(int tid) {
        String query = "SELECT " +
                source1 + " " +
                "FROM " + TB_BASIC +
                " WHERE " + COL_ID + "=" + String.format(Locale.ENGLISH, "%d", tid) + ";";
        Cursor c = myDB.rawQuery(query, null);
        while (c.moveToNext()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }

    public ArrayList<Integer> getWordIds() {
        String query = "SELECT " +
                COL_ID + " " +
                "FROM " + TB_BASIC + " " +
                "COUNT 100" + ";";
        Cursor c = myDB.rawQuery(query, null);
        ArrayList<Integer> results = new ArrayList<>();
        while (c.moveToNext()) {
            results.add(c.getInt(0));
        }
        c.close();
        return results;
    }

    public String getWordTrans(String zhWord) {
        String query = "SELECT " +
                target + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + source1 + "=\"" + zhWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        if (c.moveToFirst()) {

            return c.getString(0);
        }
        c.close();
        return null;
    }

    public String getSourceWord(String targetWord) {
        String query = "SELECT " +
                source1 + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + DBHelper.target + "=\"" + targetWord.trim() + "\" OR " +
                getPhoneticTable(target) + "=\"" + targetWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        while (c.moveToNext()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }

    public String getSourceWordFromTarget(String targetWord) {
        String query = "SELECT " +
                source1 + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + getPhoneticTable(target) + "=\"" + targetWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        while (c.moveToNext()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }

    public String getSourceWord2FromTarget(String targetWord) {
        String query = "SELECT " +
                source2 + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + DBHelper.target + "=\"" + targetWord.trim() + "\" OR " +
                getPhoneticTable(target) + "=\"" + targetWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        while (c.moveToNext()) {
            return c.getString(0);
        }
        c.close();
        return null;
    }


    public HashMap<String, String> getEntryById(int wid) {
        Cursor c = myDB.rawQuery("SELECT " +
                DBHelper.source1 + ", " +
                DBHelper.target + ", " +
                getPhoneticTable(DBHelper.target) + ", " +
                DBHelper.source2 + " " +
                " FROM " + TB_BASIC +
                " WHERE _id=" + wid + ";", null);
        if (c == null) return null;
        if (!c.moveToFirst()) return null;
        HashMap<String, String> res = new HashMap<>();
        res.put(DBHelper.source1, c.getString(0));
        res.put(DBHelper.target, c.getString(1));
        res.put(getPhoneticTable(DBHelper.target), c.getString(2));
        res.put(DBHelper.source2, c.getString(3));
        res.put(COL_ID, String.format(Locale.ENGLISH, "%d", wid));
        c.close();
        return res;
    }

    /**
     * Count the number of entries included in the corresponding lesson.
     *
     * @param lessonIndex:
     * @return number of rows in specidied lesson
     */
    public int getRowCount(int lessonIndex) {
        String params[] = new String[]{String.valueOf(lessonIndex)};
        String query = "SELECT COUNT(*) FROM " + TB_BASIC + " WHERE " +
                COL_LESSON + "=?";
        Cursor c = myDB.rawQuery(query, params);
        c.moveToFirst();
        final int res = c.getInt(0);
        c.close();
        return res;
    }

    /**
     * Note: Settings -1 for parameters indicates that it should not be considerate
     *
     * @param lessonIndex:   Index of the lesson from which to take the words
     * @param thematicIndex: Code of the thematic from which to take the words
     * @param levelIndex:    Japanese Word difficulty level,from 5 (easiest) to 1.
     * @return A random id: if failed->return -1
     */
    public int getRandId(int lessonIndex, int thematicIndex, int levelIndex) {
        ArrayList<Integer> filteredIds = getIdsByFilter(lessonIndex,thematicIndex,levelIndex);
        if(filteredIds == null)return -1;
        return filteredIds.get(Math.abs(rand.nextInt()) % filteredIds.size());
    }

    /**
     * Note:
     *  The ids returned corresponds to words where MANDATORY DBHelper(source1 and target) are not null
     *  target2 can be null
     * @param lessonIndex: -1 if no lesson filter
     * @param thematicIndex: -1 if no thematic filter
     * @param levelIndex: -1 if no level filter
     * @return A set of ids according to given constraints
     */
    public ArrayList<Integer> getIdsByFilter(int lessonIndex, int thematicIndex, int levelIndex){
        final int indexes[] = {lessonIndex, thematicIndex, levelIndex, -2, -2};
        final String corCol[] = new String[]{COL_LESSON,
                COL_THEMATIC,
                COL_LEVEL,
                DBHelper.target,
                DBHelper.source1};

        String q = "SELECT " + COL_ID + " FROM " + TB_BASIC;
        boolean hasCondition = false;
        ArrayList<String> params = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++) {
            if( indexes[i] == -2){
                if (!hasCondition) {
                    hasCondition = true;
                    q += " WHERE ";
                }else{
                    q += " AND ";
                }
                q += " " + corCol[i] + " NOT NULL AND LENGTH("+ corCol[i] + ")>0 ";
            }
            else if (indexes[i] != -1) {
                if (!hasCondition) {
                    hasCondition = true;
                    q += " WHERE ";
                }else{
                    q += " AND ";
                }
                q += corCol[i];
                q += "=? ";
                params.add(String.valueOf(indexes[i]));
            }
        }
        Log.d("SQL", q);
        Cursor c = myDB.rawQuery(q, params.toArray(new String[params.size()]));
        ArrayList<Integer> listIDs = new ArrayList<>();
        while (c.moveToNext()) {
            listIDs.add(c.getInt(0));
        }
        c.close();
        if (listIDs.size() <= 0) return null;
        return listIDs;
    }


    //Sentences
    private String getColName(String type, boolean mod) {
        return mod ? COL_DEST_WORD2 : COL_DEST_WORD;
    }


    /**
     * @param requestKanjiOrPhon: Indicate if the sentence fetch Kanji ( japanese),or prononciation
     * @return A string corresponding to the sentence
     */
    public String getSentenceTarget(int sentenceId, boolean requestKanjiOrPhon) {
        String params[] = new String[]{getColName("target", requestKanjiOrPhon),
                                    String.valueOf(sentenceId)};
        String query = "SELECT ? FROM " + TB_SENTENCES + " WHERE " + COL_ID + "=?";
        Cursor c = myDB.rawQuery(query, params);
        if (!c.moveToFirst()) return null;
        String res = c.getString(0);
        c.close();
        if (res == null && requestKanjiOrPhon) res = getSentenceTarget(sentenceId, false);
        return res;
    }

    public int updateRow(String[] row, final String tableName){
        try {
            if( !myDB.isOpen() ) open();
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(COL_FR, row[1]);
        values.put(COL_JP, row[2]);
        values.put(COL_JAP_2, row[3]);
        values.put(COL_ZH, row[4]);
        values.put(COL_EN_1, row[5]);
        values.put(COL_LESSON, row[6].equals("None")?null:Integer.parseInt(row[6]));
        values.put(COL_THEMATIC, row[7].equals("None")?-1:Integer.parseInt(row[7]));
        values.put(COL_LEVEL, row[8].equals("None")?-1: Integer.parseInt(row[8]));
        String whereClause = COL_ID + " = ?";
        String[] whereArgs = { row[0] };
         return myDB.update(tableName, values, whereClause, whereArgs);
    }

}
