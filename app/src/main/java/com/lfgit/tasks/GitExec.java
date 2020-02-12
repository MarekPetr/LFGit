package com.lfgit.tasks;
import android.app.Activity;

import static com.lfgit.Constants.*;
import static com.lfgit.Constants.reposDir;
import static com.lfgit.Logger.LogMsg;

public class GitExec extends Executor {

    public GitExec( Activity activity) {
        super(activity);
    }

    public String ldd() {
        envExeForRes("ldd", "", "/data/data/com.lfgit/files/usr/shimmed/git-annex/git-annex");
        return getResult();
    }

    public String busybox_echo() {
        envExeForRes("busybox", "", "echo", "ahoj");
        return getResult();
    }

    public String uname() {
        envExeForRes("uname", "", "-o");
        return getResult();
    }

    public String proot() {
        envExeForRes("proot", "", "/data/data/com.lfgit/files/usr/bin/git", "--help");
        return getResult();
    }

    public void config() {
        envExeForRes("git", "","config", "--global", "user.email", "petr.marek18@gmail.com");
        LogMsg(getResult());
        envExeForRes("git", "", "config", "--global", "user.name", "MarekPetr");
        LogMsg(getResult());
    }

    public String init(String dest) {
        String gitOperation = "init";
        envExeForRes("git", dest, gitOperation);
        return getResult();
    }

    public String commit() {
        String gitOperation = "commit";
        String message = "-m\"newFileToCommit\"";
        String destDir = reposDir + "clone/test";
        envExeForRes("git", destDir, gitOperation, message);
        return getResult();
    }

    public String clone(String dest, String userName, String password) {
        String gitOperation = "clone";
        String url = "https://" + userName + ":" + password + "@github.com/MarekPetr/test";
        envExeForRes("git", dest, gitOperation, url);
        return getResult();
    }

    public String status() {
        String gitOperation = "status";
        String destDir = reposDir + "repo/";
        envExeForRes("git", destDir, gitOperation);
        return getResult();
    }

    public String add() {
        String gitOperation = "add";
        String destDir = reposDir + "clone/test";
        envExeForRes("git", destDir, gitOperation, ".");
        return getResult();
    }

    public String push() {
        String gitOperation = "push";
        String destDir = reposDir + "clone/test";
         envExeForRes("git", destDir, gitOperation);
        return getResult();
    }
}
