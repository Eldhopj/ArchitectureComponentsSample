package com.eldhopj.architecturecomponentssample.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * @see <a href="https://github.com/googlecodelabs/android-persistence/blob/master/app/src/main/java/com/example/android/persistence/codelab/db/UserDao.java"/>
 *
 * Commit 6: Add live data
 *           By live data we can decrease the querying the Db everytime for changes
 */

@Dao
/**Data Access Object*/
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY priority")
    LiveData<List<TaskDBModelClass>> loadAllTasks(); // wrap the retuning object with live data

    @Insert
    void insertTask(TaskDBModelClass taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE) // to replace in-case of any conflicts
    void updateTask(TaskDBModelClass taskEntry);

    @Delete
    void deleteTask(TaskDBModelClass taskEntry);

    @Query("SELECT * FROM task WHERE id = :id") // The query for this method should get all the data for that id
    LiveData<TaskDBModelClass> loadTaskById(int id); // wrap the retuning object with live data
}
