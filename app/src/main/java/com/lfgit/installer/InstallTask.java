package com.lfgit.installer;
import android.os.AsyncTask;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Pair;

import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Constants.USR_DIR;
import static com.lfgit.utilites.Constants.USR_STAGING_DIR;

public class InstallTask extends AsyncTask<Boolean, Void, Boolean> implements ExecListener {
    private Boolean installed = false;
    private GitExec exec = new GitExec(this);
    @Override
    public void onExecStarted() {
    }

    @Override
    public void onExecFinished(String result, int errCode) {
        if (!installed) {
            exec.lfsInstall();
            installed = true;
        }
    }

    private AsyncTaskListener listener;

    public InstallTask(AsyncTaskListener listener)  {
        this.listener = listener;
    }


    // source:https://github.com/termux/termux-app/blob/master/app/src/main/java/com/termux/app/TermuxInstaller.java
    private Boolean installAssets() {
        final File PREFIX_FILE = new File(USR_DIR);
        if (PREFIX_FILE.isDirectory()) {
            return true;
        }

        final String STAGING_PREFIX_PATH = USR_STAGING_DIR;
        final File STAGING_PREFIX_FILE = new File(STAGING_PREFIX_PATH);

        if (STAGING_PREFIX_FILE.exists()) {
            try {
                deleteFolder(STAGING_PREFIX_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final byte[] buffer = new byte[8096];
        final List<Pair<String, String>> symlinks = new ArrayList<>(50);

        final byte[] zipBytes = loadZipBytes();
        try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInput.getNextEntry()) != null) {
                if (zipEntry.getName().equals("SYMLINKS.txt")) {
                    BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                    String line;
                    while ((line = symlinksReader.readLine()) != null) {
                        String[] parts = line.split("→");
                        if (parts.length != 2)
                            throw new RuntimeException("Malformed symlink line: " + line);
                        String oldPath = parts[1];
                        String newPath = STAGING_PREFIX_PATH + "/" + parts[0];
                        symlinks.add(Pair.create(oldPath, newPath));

                        ensureDirectoryExists(new File(newPath).getParentFile());
                    }
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
            e.printStackTrace();
        }

        if (symlinks.isEmpty())
            throw new RuntimeException("No SYMLINKS.txt encountered");
        for (Pair<String, String> symlink : symlinks) {
            try {
                Os.symlink(symlink.first, symlink.second);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }

        if (!STAGING_PREFIX_FILE.renameTo(PREFIX_FILE)) {
            throw new RuntimeException("Unable to rename staging folder");
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

        exec.configHooks();
        return true;
    }

    private static void ensureDirectoryExists(File directory) {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new RuntimeException("Unable to create directory: " + directory.getAbsolutePath());
        }
    }

    private static byte[] loadZipBytes() {
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

    @Override
    protected Boolean doInBackground(Boolean... params) {
        return installAssets();
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
