package com.daoliangshu.japonaischinois.core.data;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.core.db.DatabaseContract;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
/**
 * Created by daoliangshu on 9/1/17.
 */

public class DownloadActivity extends Activity {
    final String BASE_URL = "79.86.18.33:7000/editor/download_csv/";

    private Button startBtn;
    private ProgressBar mLoadingProgressBar;
    private DownloadFileAsync asynDownload;
    private TextView mText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_state_activity);
        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startDownload();
            }
        });
        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_progress);
        mText = (TextView)findViewById(R.id.text);
        mText.setText(getResources().getString(R.string.update_dictionary));
    }

    private void startDownload() {
        startBtn.setText(getResources().getString(R.string.processing));
        startBtn.setEnabled(false);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setMax(100);
        mLoadingProgressBar.setProgress(0);
        asynDownload = new DownloadFileAsync();
        asynDownload.execute(BASE_URL);
    }

    private void cancelDownload(){
        if(asynDownload != null){
            asynDownload.cancel(true);
            mText.setText(getResources().getString(R.string.download_failed));
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL("http://" + aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.setConnectTimeout(5000);
                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                InputStream input = new BufferedInputStream(url.openStream());

                File f = new File(getFilesDir().getAbsoluteFile() + "/test.db");
                f.delete();

                OutputStream output =
                        new FileOutputStream(getFilesDir().
                                getAbsoluteFile()+"/test.db");

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    Log.d("PROGRESS2", ""+total);
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.d("DEBUG", "Can't Download");
                e.printStackTrace();
            }
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("PROGRESS", ""+progress[0]);
            mLoadingProgressBar.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onCancelled(String unused){
            super.onCancelled(unused);
        }


        @Override
        protected void onPostExecute(String unused) {

            File directory = new File(getFilesDir().getPath());
            File[] files = directory.listFiles();
            Log.d("UPDATE_F:" , "File Count: "+ files.length);
            for(int i=0; i< files.length; i++){
                Log.d("UPATES_Fs", "Filename: " + files[i].getName() + "  Size: " + files[i].getTotalSpace());
            }
            mText.setText(getResources().getString(R.string.download_succeed));
            makeDBUpdate();
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            startBtn.setText(getResources().getString(R.string.update));
            startBtn.setEnabled(true);
        }
    }

    public void makeDBUpdate(){
        try {
            InputStreamReader osr =
                    new InputStreamReader(
                            new FileInputStream(getFilesDir().
                                    getAbsolutePath() + "/test.db") );
            BufferedReader br = new BufferedReader(osr);
            String line;
            //while( (line=br.readLine()) != null){
            //    Log.d("INPUT:: " , "--> line: " + line);
            //}
            br.close();
            osr.close();

            CSVReader reader = new CSVReader(new FileReader(getFilesDir().getAbsolutePath() + "/test.db"));
            DBHelper mDb = Settings.dbEntryManager!=null?Settings.dbEntryManager.getDB():null;
            if(mDb == null){
                mDb = new DBHelper(getApplicationContext());
            }
            String[] passedInfos =  reader.readNext();
            int rowCount = Integer.parseInt(passedInfos[0]);
            int rowNumber = 0;
            mLoadingProgressBar.setProgress(rowNumber/rowCount);
            String [] nextLine;
            while((nextLine = reader.readNext()) != null){
                rowNumber++;
                mLoadingProgressBar.setProgress(rowNumber/rowCount);
                mDb.updateRow(nextLine, DatabaseContract.DICO_TABLE.TABLE_NAME_DICO);
                /*for(int i =0; i< nextLine.length; i++){
                    Log.i("CSV", "Cell column index: " + i);
                    Log.i("CSV","Cell Value: " + nextLine[i]);
                    Log.i("CSV","---");
                }*/
            }
        }catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}