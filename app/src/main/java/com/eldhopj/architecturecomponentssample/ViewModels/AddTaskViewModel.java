package com.eldhopj.architecturecomponentssample.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBEntity;

public class AddTaskViewModel extends ViewModel { // We extends ViewModel instead of AndroidViewModel because we are using a factory
    /**We are using this ViewModel to cache our task entry objects wrapped in a live data object*/
    private LiveData<TaskDBEntity> task;

    // Note: The constructor should receive the database and the taskId
    public AddTaskViewModel(AppDatabase database, int taskId) {
        /**loadTaskById method is to retrieve the data belongs to the id. ie, mTaskId*/
        task = database.taskDao().loadTaskById(taskId);
    }

    public LiveData<TaskDBEntity> getTask() {
        return task;
    }
}
