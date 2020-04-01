package com.lfgit.installer;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.util.Pair;

import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.lfgit.utilites.Constants.APP_DIR;
import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;
import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Constants.LIB_DIR;
import static com.lfgit.utilites.Constants.USR_DIR;
import static com.lfgit.utilites.Constants.USR_STAGING_DIR;
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

        /*if(copyAssets) {
            if (assetsEmpty(assetDir)) {
                LogMsg("empty");
                return false;
            }
            copyFileOrDir(assetDir);
        }*/

        final File PREFIX_FILE = new File(USR_DIR);

        final String STAGING_PREFIX_PATH = USR_STAGING_DIR;
        final File STAGING_PREFIX_FILE = new File(STAGING_PREFIX_PATH);

        if (STAGING_PREFIX_FILE.exists()) {
            try {
                deleteFolder(STAGING_PREFIX_FILE);
            } catch (IOException e) {
                // no error
            }
        }

        final byte[] buffer = new byte[8096];
        final List<Pair<String, String>> symlinks = new ArrayList<>(50);

        final byte[] zipBytes = loadZipBytes();
        try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInput.getNextEntry()) != null) {
                if (zipEntry.getName().equals("SYMLINKS.txt")) {
                    /*BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                    String line;
                    while ((line = symlinksReader.readLine()) != null) {
                        String[] parts = line.split("←");
                        if (parts.length != 2)
                            throw new RuntimeException("Malformed symlink line: " + line);
                        String oldPath = parts[0];
                        String newPath = STAGING_PREFIX_PATH + "/" + parts[1];
                        symlinks.add(Pair.create(oldPath, newPath));

                        ensureDirectoryExists(new File(newPath).getParentFile());
                    }*/
                } else {
                    String zipEntryName = zipEntry.getName();
                    File targetFile = new File(STAGING_PREFIX_PATH, zipEntryName);
                    boolean isDirectory = zipEntry.isDirectory();

                    ensureDirectoryExists(isDirectory ? targetFile : targetFile.getParentFile());

                    if (!isDirectory) {
                        try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                            int readBytes;
                            while ((readBytes = zipInput.read(buffer)) != -1)
                                outStream.write(buffer, 0, readBytes);
                        }
                        if (zipEntryName.startsWith("bin/") || zipEntryName.startsWith("libexec") || zipEntryName.startsWith("lib/apt/methods")) {
                            //noinspection OctalInteger
                            Os.chmod(targetFile.getAbsolutePath(), 0700);
                        }
                    }
                }
            }
        } catch (IOException | ErrnoException e) {
            // nothing
        }

        /*if (symlinks.isEmpty())
            throw new RuntimeException("No SYMLINKS.txt encountered");
        for (Pair<String, String> symlink : symlinks) {
            Os.symlink(symlink.first, symlink.second);
        }*/

        if (!STAGING_PREFIX_FILE.renameTo(PREFIX_FILE)) {
            throw new RuntimeException("Unable to rename staging folder");
        }

        File dir = new File(HOOKS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            Os.symlink("/system/bin/sh", BIN_DIR+"/sh");
            File git = new File(BIN_DIR+"/git");
            git.delete();
            File libz = new File(LIB_DIR+"/libz.so.1");
            libz.delete();
            Os.symlink("../libexec/git-core/git", BIN_DIR+"/git");
            Os.symlink(LIB_DIR+"/libz.so.1.2.11", LIB_DIR+"/libz.so.1");
        } catch (ErrnoException e) {
            e.printStackTrace();
        }

        GitExec exec = new GitExec(this);
        exec.configHooks();
        return true;
    }

    private static void ensureDirectoryExists(File directory) {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new RuntimeException("Unable to create directory: " + directory.getAbsolutePath());
        }
    }

    public static byte[] loadZipBytes() {
        // Only load the shared library when necessary to save memory usage.
        System.loadLibrary("termux-bootstrap");
        return getZip();
    }

    public static native byte[] getZip();

    /** Delete a folder and all its content or throw. Don't follow symlinks. */
    static void deleteFolder(File fileOrDirectory) throws IOException {
        if (fileOrDirectory.getCanonicalPath().equals(fileOrDirectory.getAbsolutePath()) && fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();

            if (children != null) {
                for (File child : children) {
                    deleteFolder(child);
                }
            }
        }

        if (!fileOrDirectory.delete()) {
            throw new RuntimeException("Unable to delete " + (fileOrDirectory.isDirectory() ? "directory " : "file ") + fileOrDirectory.getAbsolutePath());
        }
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
