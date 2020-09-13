package com.lfgit.install;
import android.content.Context;
import android.os.AsyncTask;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Pair;

import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.executors.GitExecListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Constants.USR_DIR;
import static com.lfgit.utilites.Constants.USR_STAGING_DIR;

/**
 * Install the bootstrap packages
 * */
public class InstallTask extends AsyncTask<Boolean, Void, Boolean>
        implements ExecListener,GitExecListener
{
    private Boolean mInstalled = false;
    private GitExec mGitExec;
    private AsyncTaskListener mListener;

    public InstallTask(AsyncTaskListener listener, Context context)  {
        this.mListener = listener;
        this.mGitExec = new GitExec(this, this, context);
    }

    @Override
    public void onExecStarted() {
    }

    @Override
    public void onExecFinished(String result, int errCode) {
        if (!mInstalled) {
            mGitExec.lfsInstall();
            mInstalled = true;
        }
    }

    @Override
    public void onError(String errorMsg) {
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


        final File HOOKS_FILE = new File(HOOKS_DIR);
        deleteFolder(HOOKS_FILE);

        final File PREFIX_FILE = new File(USR_DIR);
        deleteFolder(PREFIX_FILE);

        final File STAGING_PREFIX_FILE = new File(USR_STAGING_DIR);
        deleteFolder(STAGING_PREFIX_FILE);


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
                            throw new RuntimeException("Malformed symlink line: " + line);
                        }
                        String oldPath = parts[1];
                        String newPath = USR_STAGING_DIR + "/" + parts[0];
                        symlinks.add(Pair.create(oldPath, newPath));

                        File dir = new File(newPath).getParentFile();
                        if (dir == null) {
                            throw new RuntimeException(newPath + "is null");
                        }

                        ensureDirectoryExists(dir);
                    }
                } else {
                    String zipEntryName = zipEntry.getName();
                    File targetFile = new File(USR_STAGING_DIR, zipEntryName);
                    boolean isDirectory = zipEntry.isDirectory();

                    ensureDirectoryExists(isDirectory ? targetFile : Objects.requireNonNull(targetFile.getParentFile()));

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
            throw new RuntimeException("Unable to read zipBytes");
        }

        if (symlinks.isEmpty()) {
            throw new RuntimeException("No SYMLINKS.txt encountered");
        }

        symlinks.add(Pair.create("/system/bin/sh", USR_STAGING_DIR + "/sh"));

        for (Pair<String, String> symlink : symlinks) {
            try {
                Os.symlink(symlink.first, symlink.second);
            } catch (ErrnoException e) {
                throw new RuntimeException("Unable to create symlink: " + symlink.first + " → " + symlink.second);
            }
        }

        if (!STAGING_PREFIX_FILE.renameTo(PREFIX_FILE)) {
            throw new RuntimeException("Unable to rename staging folder");
        }

        // Create a directory for Git Hooks
        File dir = new File(HOOKS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // Install Git Hooks
        mGitExec.configHooks();
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
    static void deleteFolder(File fileOrDirectory) {
        try {
            if (fileOrDirectory.getCanonicalPath().equals(fileOrDirectory.getAbsolutePath()) && fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();

                if (children != null) {
                    for (File child : children) {
                        deleteFolder(child);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("getCanonicalPath() IO exception");
        }

        if (!fileOrDirectory.delete()) {
            throw new RuntimeException("Unable to delete " + (fileOrDirectory.isDirectory() ? "directory " : "file ") + fileOrDirectory.getAbsolutePath());
        }
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        return installFiles();
    }

    @Override
    protected void onPreExecute() {
        mListener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(Boolean installed) {
        mListener.onTaskFinished(installed);
    }
}
