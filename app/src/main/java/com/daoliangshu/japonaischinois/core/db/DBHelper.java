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

import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_EN_1;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_EN_2;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_FR;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_FR_2;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_ID;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_JP;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_JP_2;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_LESSON;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_LEVEL;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_THEMATIC;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_ZH;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.COL_ZH_2;
import static com.daoliangshu.japonaischinois.core.db.DatabaseContract.DICO_TABLE.TABLE_NAME_DICO;

/**
 * Created by daoliangshu on 1/28/17.
 * Class containing the db connection,
 * provides methods to get data from db.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static String source1 = "zh_1";
    public static String source2 = "fr_1";
    public static String target = "jp_1";

    private final static String TB_SENTENCES = "sentences";

    private final static String COL_DEST_WORD = "dst_word";
    private final static String COL_DEST_WORD2 = "dst_word2";

    private SQLiteDatabase myDB;
    private static String DB_PATH;
    private static final String DB_NAME = "db_dico";
    private final Context myContext;

    public DBHelper(Context context) throws SQLException {
        super(context, DB_NAME, null, 3);
        myContext = context;
        DB_PATH = myContext.getFilesDir().getPath();
        initDb();
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
        db.execSQL(DatabaseContract.DICO_TABLE.CREATE_TABLE_DICO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    /*---------------------------------------------*/
    /*--------------GETTERS------------------------*/
    /*---------------------------------------------*/
    public static String getPhoneticTable(String table) {
        switch (table) {
            case COL_FR:
                return COL_FR_2;
            case COL_ZH:
                return COL_ZH_2;
            case COL_JP:
                return COL_JP_2;
            case COL_EN_1:
                return COL_EN_2;
            default:
                return COL_FR_2;
        }
    }


    public String getWordTrans(String zhWord) {
        String query = "SELECT " +
                target + " " +
                "FROM " + TABLE_NAME_DICO + " " +
                "WHERE " + source1 + "=\"" + zhWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        if (c.moveToFirst()) {

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
                " FROM " + TABLE_NAME_DICO +
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

        String q = "SELECT " + COL_ID + " FROM " + TABLE_NAME_DICO;
        boolean hasCondition = false;
        ArrayList<String> params = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] == -2) {
                if (!hasCondition) {
                    hasCondition = true;
                    q += " WHERE ";
                } else {
                    q += " AND ";
                }
                q += " " + corCol[i] + " NOT NULL AND LENGTH(" + corCol[i] + ")>0 ";
            } else if (indexes[i] != -1) {
                if (!hasCondition) {
                    hasCondition = true;
                    q += " WHERE ";
                } else {
                    q += " AND ";
                }
                q += corCol[i];
                q += "=? ";
                params.add(String.valueOf(indexes[i]));
            }
        }
        Cursor c = myDB.rawQuery(q, params.toArray(new String[params.size()]));
        ArrayList<Integer> listIDs = new ArrayList<>();
        while (c.moveToNext()) {
            listIDs.add(c.getInt(0));
        }
        c.close();
        if (listIDs.size() <= 0) return null;
        return listIDs;
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
        values.put(COL_JP_2, row[3]);
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
