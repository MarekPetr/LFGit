package com.lfgit.tasks;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import static com.lfgit.Constants.binDir;
import static com.lfgit.Constants.filesDir;
import static com.lfgit.Constants.libDir;
import static com.lfgit.Logger.LogMsg;
import static com.lfgit.permissions.PermissionRequester.isWriteStoragePermissionGranted;

abstract class Executor {

    private String result;
    String exe;
    private Activity mActivity;

    Executor(Activity activity) {
        this.mActivity = activity;
        this.exe = binDir;
    }

    String getResult() {
        return result;
    }

    Integer envExeForRes(String binary, String destDir, String... strings) {
        result = "";
        String exeDir = exe + binary;
        Integer errCode = 0;

        String dirPath = "";
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if (isWriteStoragePermissionGranted(mActivity)) {
                dirPath = Environment.getExternalStorageDirectory().toString() + "/" + destDir;
                File f = new File(Environment.getExternalStorageDirectory(), destDir);
                if (strings.length > 0 && strings[0].equals("init")) {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                }
            } else {
                result = "Permission denied";
                return -1;
            }
        } else {
            result = "Media not mounted";
            return -1;
        }

        List<String> args = new ArrayList<>();
        args.add(exeDir);
        args.addAll(Arrays.asList(strings));

        Log.d("petr", "exe: " + Arrays.toString(args.toArray()));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // redirect error stream to input stream
        pb.directory(new File(dirPath));
        Map<String, String> env = pb.environment();

        env.put("LD_LIBRARY_PATH", env.get("LD_LIBRARY_PATH") + ":" + libDir + ":" + libDir+"aarch64-linux-gnu");
        env.put("PATH", env.get("PATH") + ":" + binDir + ":" + libDir);
        env.put("HOME", filesDir);

        Process javap = null;
        Buffer buffer = null;
        try {
            javap = pb.start();
            buffer = new Buffer(javap.getInputStream());
            errCode = javap.waitFor();
            result = buffer.getOutput();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        LogMsg(result);

        if (errCode == 0) {
            result += "\nOperation successful";
        } else {
            result += "\nOperation failed with error " + errCode;
        }

        return errCode;
    }

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
                if((line = reader.readLine()) != null) {
                    mBuffer.append(line);
                    while((line = reader.readLine()) != null) {
                        mBuffer.append(EOL).append(line);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}

