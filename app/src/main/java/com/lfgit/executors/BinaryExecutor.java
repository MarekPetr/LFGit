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
import static com.lfgit.utilites.Logger.LogMsg;

/**
 * Execute a compiled program.
 */
class BinaryExecutor {

    private Process mProcess = null;
    private String mExeDir;
    private static final String EOL = System.getProperty("line.separator");
    private ExecListener mCallback;

    BinaryExecutor(ExecListener callback) {
        mExeDir = BIN_DIR;
        mCallback = callback;
    }

    void run(String binary, String destDir, String... strings) {
        String exeBin = mExeDir + "/" + binary;
        File f = new File(destDir);

        List<String> args = new ArrayList<>();
        args.add(exeBin);
        args.addAll(Arrays.asList(strings));

        LogMsg("exe: " + Arrays.toString(args.toArray()));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // redirect error stream to input stream
        pb.directory(new File(destDir));
        Map<String, String> env = pb.environment();
        env.put("LD_LIBRARY_PATH", LIB_DIR + ":" + BIN_DIR);
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
                StringBuilder mOutBuffer = new StringBuilder();
                String result;
                int errCode;
                mCallback.onExecStarted();
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
                    errCode = mProcess.waitFor();
                    result = mOutBuffer.toString();
                    mCallback.onExecFinished(result, errCode);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }.start();
    }
}

