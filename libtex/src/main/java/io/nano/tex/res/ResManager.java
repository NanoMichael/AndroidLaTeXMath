package io.nano.tex.res;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nano on 18-11-10
 */
public final class ResManager {

    private static final String TAG = "ResManager";

    private String rootDir;

    public ResManager(Context context) {
        rootDir = context.getFilesDir().getPath() + File.separator + "tex";
    }

    public String getResourcesRootDirectory() {
        return rootDir;
    }

    private List<String> listRes() {
        InputStream is = ResManager.class.getResourceAsStream("readme");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        List<String> res = new ArrayList<>();
        try {
            while ((line = in.readLine()) != null) res.add(line);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read resources", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close resources file.", e);
            }
        }
        return res;
    }

    public void unpackResources() {
        List<String> res = listRes();
        File root = new File(rootDir);
        if (!root.exists()) root.mkdirs();

        for (String path : res) {
            File file = new File(rootDir + File.separator + path);
            if (file.exists()) continue;
            int li = path.lastIndexOf(File.separator);
            if (li >= 0) {
                String dir = path.substring(0, li);
                File f = new File(rootDir + File.separator + dir);
                if (!f.exists()) f.mkdirs();
            }
            String p = rootDir + File.separator + path;
            InputStream is = ResManager.class.getResourceAsStream(path);
            Log.i(TAG, "Copy resource: " + path);
            copyTo(is, p);
        }
    }

    private static void copyTo(InputStream is, String targetPath) {
        OutputStream os = null;
        try {
            byte[] buffer = new byte[2048];
            os = new BufferedOutputStream(new FileOutputStream(targetPath));
            int l;
            while ((l = is.read(buffer)) > 0) os.write(buffer, 0, l);
            os.flush();
        } catch (Exception e) {
            Log.e(TAG, "Copy resource failed", e);
        } finally {
            try {
                is.close();
                if (os != null) os.close();
            } catch (IOException e) {
                Log.e(TAG, "Copy resource failed", e);
            }
        }
    }
}

