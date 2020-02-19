package com.lfgit.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lfgit.database.model.Repo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Repo.class}, version = 1, exportSchema = false)
public abstract class RepoDatabase extends RoomDatabase {
    public abstract RepoDao repoDao();

    private static volatile RepoDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static synchronized RepoDatabase getInstance(Context context) {
        // singleton database
        if (INSTANCE == null) {
            synchronized (RepoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RepoDatabase.class, "DB_REPO")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // TODO migration if needed
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}