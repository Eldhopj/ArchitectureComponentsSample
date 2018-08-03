package com.eldhopj.architecturecomponentssample.ViewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;

/**We are creating ViewModelFactory because we need to pass the id into the ViewModel */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    //Our ViewModelFactory requires two member variables
    private final AppDatabase mDb; // instance of the DB
    private final int mTaskId; // ID of the task

    public ViewModelFactory(AppDatabase mDb, int mTaskId) { // Constructor
        this.mDb = mDb;
        this.mTaskId = mTaskId;
    }

    /**Overiding create fun*/
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
         return (T) new AddTaskViewModel(mDb, mTaskId);
    }
}
