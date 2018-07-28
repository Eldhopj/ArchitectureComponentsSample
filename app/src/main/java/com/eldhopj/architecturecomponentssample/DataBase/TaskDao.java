package com.eldhopj.architecturecomponentssample.DataBase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao/**Data Access Object*/
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY priority")
    List<TaskDBModelClass> loadAllTasks();

    @Insert
    void insertTask(TaskDBModelClass taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE) // to replace in-case of any conflicts
    void updateTask(TaskDBModelClass taskEntry);

    @Delete
    void deleteTask(TaskDBModelClass taskEntry);
}
