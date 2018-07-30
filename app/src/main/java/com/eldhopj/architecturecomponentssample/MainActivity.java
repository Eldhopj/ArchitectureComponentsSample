package com.eldhopj.architecturecomponentssample;

/**Commit 1:
 *          Add room dependencies
 *          Create a db model class with @Entity annotation
 *          Create a @DAO interface (TaskDao)
 *          Create a Database abstract class (AppDatabase)
 *          Saving into DB (AddTaskActivity)
 *          Retriving from DB ( MainActivity)
 *          */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.AppExecutors;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBModelClass;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener{

    private static final String TAG = "MainActivity";
    //Define the variables
    private RecyclerView recyclerView;
    private List<TaskDBModelClass> mTaskEntries;
    private Adapter mAdapter;

    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext()); //Initialize member variable for the data base

        //bind with xml
        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mAdapter content changes
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //it can be GridLayoutManager or StaggeredGridLayoutManager
        //set the mAdapter to the recycler view
        mAdapter = new Adapter(mTaskEntries,this);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        mAdapter.setOnItemClickListener(this); // For item onclick

        //FAB button
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
    }

    /**Loading Data from database and setting into an adapter in here*/
    @Override
    protected void onStart() {
        super.onStart();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaskDBModelClass> tasks = mDb.taskDao().loadAllTasks();

                // NOTE : We will be able to simplify this once we learn more about Android Architecture Components
                runOnUiThread(new Runnable() { //We need to use the runOnUiThread method to wrap setting tasks to the adapter
                    @Override
                    public void run() {
                        mAdapter.setTasks(tasks);
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        try {
            TaskDBModelClass clickedItem = mTaskEntries.get(position); // We get the item at the clicked position out of our list items
        }catch (NullPointerException e){
            Log.d(TAG, "onItemClick: " + e.getMessage());
        }

    }
}
