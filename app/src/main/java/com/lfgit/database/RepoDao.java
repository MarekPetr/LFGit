package com.lfgit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lfgit.database.model.Repo;

import java.util.List;

@Dao
public interface RepoDao {
    @Query("SELECT * from repo")
    LiveData<List<Repo>> getAllRepos();

    // Insert one repository
    @Insert (onConflict = OnConflictStrategy.ABORT)
    void insertRepo(Repo repo);

    // Insert an array of repositories
    @Insert
    void insertList(List<Repo> repos);

    @Update
    void updateRepos(List<Repo> repos);

    @Query("UPDATE repo SET username = :username, password= :password WHERE id =:id")
    void updateCredentials(String username, String password, int id);

    @Query("UPDATE repo SET remoteURL = :remoteURL WHERE id =:id")
    void updateRemoteURL(String remoteURL, int id);

    // delete the whole repository
    @Query("DELETE FROM repo")
    void deleteAll();

    // Show repositories ordered by name
    @Query("SELECT * FROM repo ORDER BY localPath ASC")
    LiveData<List<Repo>> getAlphabetizedRepos();

    @Query("DELETE FROM repo WHERE id = :repoId")
    void deleteByRepoId(int repoId);

    @Query("DELETE FROM repo WHERE localPath = :localPath")
    void deleteByLocalPath(String localPath);


}
