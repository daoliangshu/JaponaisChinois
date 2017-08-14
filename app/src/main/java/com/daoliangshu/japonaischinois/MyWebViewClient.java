package com.daoliangshu.japonaischinois;
import android.content.Context;
import android.webkit.WebViewClient;

import com.daoliangshu.japonaischinois.core.DBHelper;

/**
 * Created by daoliangshu on 2/4/17.
 */

public class MyWebViewClient extends WebViewClient {
    private final Context context;
    private DBHelper dbHelper = null;

    public MyWebViewClient(Context context) {
        this.context = context;
        // this.myDBHelper = myDbHelper;
    }

}
