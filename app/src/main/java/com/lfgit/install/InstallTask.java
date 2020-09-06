package com.lfgit.install;
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

import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Constants.USR_DIR;
import static com.lfgit.utilites.Constants.USR_STAGING_DIR;
import static com.lfgit.utilites.Logger.LogMsg;

/**
 * Install the bootstrap packages
 * */
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

    /**
     * Install the bootstrap packages if necessary by following the below steps:
     * <p/>
     * (1) If $PREFIX already exist, assume that it is correct and be done. Note that this relies on that we do not create a
     * broken $PREFIX folder below.
     * <p/>
     * (2) A progress dialog is shown with "Installing" message and a spinner.
     * <p/>
     * (3) A staging folder, $STAGING_PREFIX, is {@link #deleteFolder(File)} if left over from broken installation below.
     * <p/>
     * (4) The zip file is loaded from a shared library.
     * <p/>
     * (5) The zip, containing entries relative to the $PREFIX, is is downloaded and extracted by a zip input stream
     * continuously encountering zip file entries:
     * <p/>
     * (5.1) If the zip entry encountered is SYMLINKS.txt, go through it and remember all symlinks to setup.
     * <p/>
     * (5.2) For every other zip entry, extract it into $STAGING_PREFIX and set execute permissions if necessary.
     *
     * source:
     * https://github.com/termux/termux-app/blob/master/app/src/main/java/com/termux/app/TermuxInstaller.java
     */

    private Boolean installFiles() {

        final File PREFIX_FILE = new File(USR_DIR);
        if (PREFIX_FILE.isDirectory()) {
            return false;
        }

        final String STAGING_PREFIX_PATH = USR_STAGING_DIR;
        final File STAGING_PREFIX_FILE = new File(STAGING_PREFIX_PATH);

        if (STAGING_PREFIX_FILE.exists()) {
            try {
                if(!deleteFolder(STAGING_PREFIX_FILE)) return false;
            } catch (IOException ignored) {}
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
                        if (parts.length != 2){
                            LogMsg("Malformed symlink line: " + line);
                            return false;
                        }
                        String oldPath = parts[1];
                        String newPath = STAGING_PREFIX_PATH + "/" + parts[0];
                        symlinks.add(Pair.create(oldPath, newPath));

                        if (!ensureDirectoryExists(new File(newPath).getParentFile())) return false;
                    }
                } else {
                    String zipEntryName = zipEntry.getName();
                    File targetFile = new File(STAGING_PREFIX_PATH, zipEntryName);
                    boolean isDirectory = zipEntry.isDirectory();

                    if (!ensureDirectoryExists(isDirectory ? targetFile : targetFile.getParentFile())) return false;

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

        if (symlinks.isEmpty()) {
            LogMsg("No SYMLINKS.txt encountered");
            return false;
        }

        symlinks.add(Pair.create("/system/bin/sh", STAGING_PREFIX_PATH + "/sh"));

        for (Pair<String, String> symlink : symlinks) {
            try {
                Os.symlink(symlink.first, symlink.second);
            } catch (ErrnoException e) {
                LogMsg("Unable to create symlink: " + symlink.first + " → " + symlink.second);
                return false;
            }
        }

        if (!STAGING_PREFIX_FILE.renameTo(PREFIX_FILE)) {
            LogMsg("Unable to rename staging folder");
            return false;
        }

        // Create a directory for Git Hooks
        File dir = new File(HOOKS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // Install Git Hooks
        exec.configHooks();

        return true;
    }

    private static Boolean ensureDirectoryExists(File directory) {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            LogMsg("Unable to create directory: " + directory.getAbsolutePath());
            return false;
        }
        return true;
    }

    private static byte[] loadZipBytes() {
        // Only load the shared library when necessary to save memory usage.
        System.loadLibrary("termux-bootstrap");
        return getZip();
    }

    public static native byte[] getZip();

    /** Delete a folder and all its content or throw. Don't follow symlinks. */
    static Boolean deleteFolder(File fileOrDirectory) throws IOException {
        if (fileOrDirectory.getCanonicalPath().equals(fileOrDirectory.getAbsolutePath()) && fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();

            if (children != null) {
                for (File child : children) {
                    deleteFolder(child);
                }
            }
        }

        if (!fileOrDirectory.delete()) {
            LogMsg("Unable to delete " + (fileOrDirectory.isDirectory() ? "directory " : "file ") + fileOrDirectory.getAbsolutePath());
            return false;
        }
        return true;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        return installFiles();
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(Boolean retVal) {
        listener.onTaskFinished(retVal);
    }
}
