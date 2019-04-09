package com.eldhopj.architecturecomponentssample.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel { // extends view model

    private static final String TAG = "MainViewModel";

    /**We are using this ViewModel to cache our list of task entry objects wrapped in a live data object*/
    private LiveData<List<TaskDBEntity>> tasks;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication()); //We are querying our DB in our ViewModel
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");

        /**Query to load all data*/
        tasks = database.taskDao().loadAllTasks();// Get the instance of our DB and call the LoadAllTasks() method
    }

    public LiveData<List<TaskDBEntity>> getTasks() {
        return tasks;
    }
}
