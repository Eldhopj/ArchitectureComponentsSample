package com.eldhopj.architecturecomponentssample.DataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@TypeConverters(DateConvertor.class) // room cannot map complex data-types like Date into database
@Database(entities = {TaskDBModelClass.class}, version = 1, exportSchema = false) // In entities = we give list of Entity annotated class
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) { // if the AppDatabase instance is not created ie, null create one
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME) // databaseBuilder needs Context,current class,DB name
                        .allowMainThreadQueries() // WARNING : Queries should be done in a separate thread to avoid locking the UI, We will allow this ONLY TEMPORALLY to see that our DB is working
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    //define db interfaces in here
    public abstract TaskDao taskDao(); // To add the TaskDao we will include an abstract method that returns it
}
