package com.daoliangshu.japonaischinois.core.data;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;

import com.daoliangshu.japonaischinois.core.fragments.InfoSlidePageFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by daoliangshu on 2/7/17.
 */

public class DataManager {

    private static class SparseHtml {
        SparseArray<String> text;

        public SparseHtml() {
            text = new SparseArray<>();
        }

        public void put(int index, String value) {
            text.put(index, value);
        }

        public String get(int index) {
            return text.get(index, null);
        }
    }

    public static String getHtmlFile(int index, boolean isNightMode) {
        if (infoHtmlPreload == null) {
            infoHtmlPreload = new SparseHtml[2];
            infoHtmlPreload[0] = new SparseHtml();
            infoHtmlPreload[1] = new SparseHtml();
        }
        return infoHtmlPreload[isNightMode ? 1 : 0].get(index);
    }

    public static void putHtmlFile(String htmlText, int index, boolean isNightMode) {
        if (infoHtmlPreload == null) {
            infoHtmlPreload = new SparseHtml[2];
            infoHtmlPreload[0] = new SparseHtml();
            infoHtmlPreload[1] = new SparseHtml();
        }
        infoHtmlPreload[isNightMode ? 1 : 0].put(index, htmlText);
    }

    public static SparseHtml infoHtmlPreload[];
    public static SparseArray<InfoSlidePageFragment> infoFragments;

    public static boolean copyAssetFolder(AssetManager assetManager,
                                          String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            for (int i = 0; i < files.length; i++) {
                Log.i("file in assets : ", files[i]);
            }
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains(".")) {
                    Log.i("creating_file: ", toPath + "/" + file);
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                } else {
                    Log.i("creating_folder: ", toPath + "/" + file);
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
