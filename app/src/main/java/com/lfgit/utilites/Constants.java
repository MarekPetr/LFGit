package com.lfgit.utilites;

import com.lfgit.database.model.Repo;

import org.jetbrains.annotations.NotNull;

public class Constants {
    public static final String PKG = "com.lfgit/";
    public static final String APP_DIR = "/data/data" + "/" + PKG;
    public static final String FILES_DIR = APP_DIR + "files";
    public static final String USR_DIR = FILES_DIR + "/usr";
    public static final String LIB_DIR = USR_DIR + "/lib";
    public static final String BIN_DIR = USR_DIR + "/bin";
    public static final String REPOS_DIR = APP_DIR + "/repos";
    public static final String GIT_CORE_DIR = FILES_DIR + "/libexec/git-core";

    public enum AddRepo {
        OK(0),
        ALREADY_ADDED(1);

        int value;
        AddRepo(int value) {
            this.value = value;
        }
    }

    public enum RepoTask {
        INIT("init"),
        CLONE("clone"),
        ADD("add"),
        COMMIT("commit"),
        PUSH("push"),
        PULL("pull"),
        STATUS("status");

        private String task;
        RepoTask(String task) {
            this.task = task;
        }

        @NotNull
        @Override
        public String toString(){
            return task;
        }

        public static RepoTask toValue(String task) {
            for(RepoTask needle : values()) {
                if (needle.task.equals(task)) {
                    return needle;
                }
            }
            return null;
        }
    }
}
