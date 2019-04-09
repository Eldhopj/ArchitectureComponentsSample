package com.eldhopj.architecturecomponentssample.DataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;
/**Connect all different parts*/
@TypeConverters(DateConvertor.class) // room cannot map complex data-types like Date into database
@Database(entities = {TaskDBEntity.class}, version = 1, exportSchema = false) // In entities = we give list of Entity annotated class
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "todolist";

    private static AppDatabase sInstance;
    //define db interfaces in here
    public abstract TaskDao taskDao(); 

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) { // if the AppDatabase instance is not created ie, null create one
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME) // databaseBuilder needs Context,current class,DB name
//                        .allowMainThreadQueries() // WARNING : Queries should be done in a separate thread to avoid locking the UI, We will allow this ONLY TEMPORALLY to see that our DB is working
                        .fallbackToDestructiveMigration() //WARNING : Deletes existing Db while migrating into another version
                        .build();
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

}
