package com.lfgit.installer;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

import static com.lfgit.utilites.Constants.APP_DIR;
import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;
import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Logger.LogMsg;

public class InstallTask extends AsyncTask<Boolean, Void, Boolean> implements ExecListener {
    @Override
    public void onExecStarted() {
    }

    @Override
    public void onExecFinished(String result, int errCode) {
    }

    private enum Arch {
        x86(0),
        arm64_v8a(1);
        int value;

        Arch(int value) {
            this.value = value;
        }
    }

    private AssetManager assetManager;
    private AsyncTaskListener listener;

    public InstallTask(AssetManager assets, AsyncTaskListener listener)  {
        this.assetManager = assets;
        this.listener = listener;
    }

    private Boolean installAssets(final Boolean copyAssets) {
        Arch targetDev = Arch.arm64_v8a;//
        String assetDir = "git-lfs";

        if(copyAssets) {
            if (assetsEmpty(assetDir)) {
                LogMsg("empty");
                return false;
            }
            copyFileOrDir(assetDir);
        }
        File dir = new File(HOOKS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            Os.symlink("/system/bin/sh", BIN_DIR+"/sh");
        } catch (ErrnoException e) {
            e.printStackTrace();
        }

        GitExec exec = new GitExec(this);
        exec.configHooks();
        return true;
    }

    private Boolean assetsEmpty(String path) {
        String[] assets;
        try {
            assets = assetManager.list(path);
            assert assets != null;
            LogMsg(Arrays.toString(assets));
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


            String pathNoArch = FILES_DIR + "/" + noArchDir;
            if (assets.length == 0) {
                copyFile(path, pathNoArch);
            } else {
                File dir = new File(pathNoArch);
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

    private void copyFile(String filename, String pathNoArch) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File file = new File(pathNoArch);
            out = new FileOutputStream(pathNoArch);

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
        return installAssets(params[0]);
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
