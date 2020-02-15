package com.lfgit.importer;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.system.Os;
import android.util.Log;

import com.lfgit.interfaces.TaskListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

import static com.lfgit.utilites.Constants.APP_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;


public class AssetImporter extends AsyncTask<Boolean, Void, Boolean> {
    private AssetManager assetManager;
    private TaskListener listener;

    public AssetImporter(AssetManager assets, TaskListener listener)  {
        this.assetManager = assets;
        this.listener = listener;
    }

    private Boolean importAssets(final Boolean copyAssets) {
        Arch targetDev = Arch.arm64_v8a;//
        String assetDir = "git-lfs";

        if(copyAssets) {
            if (assetsEmpty(assetDir)) {
                Log.d("petr", "empty");
                return false;
            }
            copyFileOrDir(assetDir);
        }
        File dir = new File(APP_DIR + "repos");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return true;
    }

    private Boolean assetsEmpty(String path) {
        String[] assets;
        try {
            assets = assetManager.list(path);
            assert assets != null;
            Log.d("petr", Arrays.toString(assets));
            if (assets.length == 0)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void copyFileOrDir(String path) {
        String[] assets;
        try {
            assets = assetManager.list(path);
            assert assets != null;

            // copy only content of architecture directory
            String noArchDir = (path.substring(path.indexOf("/")+1).trim());
            if (noArchDir.equals(path)) noArchDir = "";


            if (assets.length == 0) {
                copyFile(path, noArchDir);
            } else {
                String fullPath = FILES_DIR + noArchDir;
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdir();
                for (String asset : assets) {
                    copyFileOrDir(path + "/" + asset);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile(String filename, String noArchDir) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = FILES_DIR + "/" + noArchDir;
            File file = new File(newFileName);
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            Os.chmod(file.getAbsolutePath(), 0700);
        } catch (Exception e) {
            Log.e("tag", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        return importAssets(params[0]);
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(Boolean v) {
        listener.onTaskFinished();
    }
}
