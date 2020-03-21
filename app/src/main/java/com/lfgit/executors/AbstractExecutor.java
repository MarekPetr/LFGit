package com.lfgit.executors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;
import static com.lfgit.utilites.Constants.LIB_DIR;
import static com.lfgit.utilites.Constants.RepoTask;
import static com.lfgit.utilites.Logger.LogMsg;

abstract class AbstractExecutor {

    private Process mProcess = null;
    private String mResult = "";
    private int mErrCode;
    String mExeDir;
    private final StringBuffer mOutBuffer = new StringBuffer();
    private static final String EOL = System.getProperty("line.separator");
    private ExecCallback mCallback;

    AbstractExecutor(ExecCallback callback) {
        mExeDir = BIN_DIR;
        mCallback = callback;
    }

    public String getResult() {
        return mResult;
    }

    public int getErrCode() {
        return mErrCode;
    }

    void executeBinary(String binary, String destDir, String... strings) {
        String exeBin = mExeDir + "/" + binary;
        File f = new File(destDir);
        if (binary.equals("git") &&
                (strings[0].equals("init") || strings[0].equals("clone"))) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        List<String> args = new ArrayList<>();
        args.add(exeBin);
        args.addAll(Arrays.asList(strings));

        LogMsg("exe: " + Arrays.toString(args.toArray()));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // redirect error stream to input stream
        pb.directory(new File(destDir));
        Map<String, String> env = pb.environment();
        LogMsg("LIB_DIR: " + LIB_DIR);
        env.put("LD_LIBRARY_PATH", LIB_DIR);
        env.put("PATH", BIN_DIR);
        env.put("HOME", FILES_DIR);
        env.put("XDG_CONFIG_HOME",FILES_DIR);

        Process javap = null;
        try {
            javap = pb.start();
        } catch (IOException e) {
            mProcess = null;
            e.printStackTrace();
        }

        mProcess = javap;

        new Thread() {
            @Override
            public void run() {
                String line;
                try {
                    InputStream stdout = mProcess.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
                    while((line = reader.readLine()) != null) {
                        mOutBuffer.append(line).append(EOL);
                    }
                } catch(IOException e) {
                    // ignore
                }

                try {
                    mErrCode = mProcess.waitFor();
                    mResult = mOutBuffer.toString();
                    if (mResult.isEmpty()) {
                        if (mErrCode == 0) {
                            mResult = "Operation successful";
                        } else {
                            mResult = "Operation failed";
                        }
                    }
                    mCallback.passResult(RepoTask.valueOf(strings[0]), mResult, mErrCode);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }.start();
    }
}

