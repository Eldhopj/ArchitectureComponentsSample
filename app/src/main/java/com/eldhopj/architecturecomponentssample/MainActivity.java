package com.eldhopj.architecturecomponentssample;

/**Commit 1&2:
 *          Add room dependencies
 *          Create a db model class with @Entity annotation
 *          Create a @DAO interface (TaskDao)
 *          Create a Database abstract class (AppDatabase)
 *          Saving into DB (AddTaskActivity)
 *          Retrieving from DB ( MainActivity)
 *
 * Commit 3:
 *          Making DB trans in a background thread using Executors
 *
 * Commit 4: Delete
 *          Deleting from DB using swipe gestures
 *
 * Commit 5: Update (steps below)
 *          Get the Id of the item to be updated through OnItemClick method
 *          Pass the id to the task activity and from there to loadTaskById
 *          populate the data of that ID
 *          Update the data's on that ID using updateTask method
 *
 * Commit 6: LiveData
 *          Add dependencies
 *          Note : LiveData is for to observe changes on the DB, So operations like insert,update or delete we don't need to use LiveData
 *
 * Commit 7: ViewModel
 *          ViewModel helps data to survive after a configuration changes and prevents the memory leaks
 *          Define the ViewModel classes , in view model class the data transactions are done because the lifecycle of ViewModel class is until the activity is destroyed
 *          ViewModelFactory class is used when we need to pass variables into ViewModel, we needed to pass the ID to the LoadTaskById() method in AddTaskViewModel class
 * */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.AppExecutors;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBModelClass;
import com.eldhopj.architecturecomponentssample.ViewModels.MainViewModel;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener{

    private static final String TAG = "MainActivity";
    //Define the variables
    private RecyclerView mRecyclerView;
    private List<TaskDBModelClass> mTaskEntries;
    private Adapter mAdapter;

    public static final String EXTRA_TASK_ID = "itemID";

    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext()); //Initialize member variable for the data base

        //bind with xml
        mRecyclerView = findViewById(R.id.recyclerViewTasks);
        mRecyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mAdapter content changes
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //it can be GridLayoutManager or StaggeredGridLayoutManager
        //set the mAdapter to the recycler view
        mAdapter = new Adapter(mTaskEntries,this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mAdapter.setOnItemClickListener(this); // For item onclick

        loadDataFromViewModel();/**Function to Loading Data from database and setting into an adapter*/


        /**deleting of data from DB*/
            /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Calls when a user swipes left or right on a ViewHolder
            /**@param viewHolder The view holder for that row
             * @param swipeDir integer that defines the swipe direction*/
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<TaskDBModelClass> tasks = mAdapter.getTasks(); //for getting all fields

                        mDb.taskDao().deleteTask(tasks.get(position)); /** Call deleteTask in the taskDao with the task at that position*/

                        /*loadData(); //Call loadData method to refresh the UI
                         * NOTE : we can remove this , Hence LiveData lessons for any changes it will auto update the UI (observer)*/

                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

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

    /**Loading data using LiveData*/
    private void loadDataFromViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class); /** View Model providers*/
        /** @param lifecycleOwner -> Objects which have an LifeCycle eg: Activities and Fragments
         *  @param Observer -> Observes the lifeCycleOwner */
        viewModel.getTasks().observe(this, new Observer<List<TaskDBModelClass>>() { /**Calling its Observe method*/
        @Override
            /*NOTE : onChanged runs on the main thread , So we remove runOnUiThread
                Here the data change in the db will be updated to the recycler view*/
        public void onChanged(@Nullable List<TaskDBModelClass> taskDBModelClasses) { // interface to implement onChange method
            Log.d(TAG, "Loading data from DB via LiveData in ViewModel");
            mAdapter.setTasks(taskDBModelClasses);
        }
        });
    }
    @Override
    public void onItemClick(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, itemId); /**ID of the item to be updated*/
        Log.d(TAG, "onItemClick: "+String.valueOf(itemId));
        startActivity(intent);
    }
}
