package com.lfgit.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Repo.class}, version = 1)
public abstract class RepoDatabase extends RoomDatabase {
    public abstract RepoDao repoDao();

    private static volatile RepoDatabase INSTANCE;

    public static synchronized RepoDatabase getInstance(Context context) {
        // singleton database
        if (INSTANCE == null) {
            synchronized (RepoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RepoDatabase.class, "DB_REPO")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}