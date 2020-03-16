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

abstract class AbstractExecutor {

    private String mResult;
    private int mErrCode;
    String mExeDir;

    AbstractExecutor() {
        mExeDir = BIN_DIR;
    }

    public String getResult() {
        return mResult;
    }

    public int getErrCode() {
        return mErrCode;
    }

    String executeBinary(String binary, String destDir, String... strings) {
        String exeBin = mExeDir + binary;
        File f = new File(destDir);
        if (binary.equals("git") && strings[0].equals("init")) {
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

        Process javap;
        Buffer buffer;
        try {
            javap = pb.start();
            buffer = new Buffer(javap.getInputStream());
            mErrCode = javap.waitFor();
            mResult = buffer.getOutput();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (mResult.isEmpty()) {
            if (mErrCode == 0) {
                mResult = "Operation successful";
            } else {
                mResult = "Operation failed";
            }
        }
        return mResult;
    }
    // source https://github.com/jjNford/android-shell/blob/master/src/com/jjnford/android/util/Shell.java
    private static class Buffer extends Thread {
        private InputStream mInputStream;
        private StringBuffer mBuffer;
        private static final String EOL = System.getProperty("line.separator");

        /**
         * @param inputStream Data stream to get shell output from.
         */
        Buffer(InputStream inputStream) {
            mInputStream = inputStream;
            mBuffer = new StringBuffer();
            this.start();
        }

        public String getOutput() {
            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBuffer.toString();
        }

        @Override
        public void run() {
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
                while((line = reader.readLine()) != null) {
                    mBuffer.append(line).append(EOL);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}

