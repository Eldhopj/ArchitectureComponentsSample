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
 * */

import android.content.Intent;
import android.os.Bundle;
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
                        List<TaskDBModelClass> tasks = mAdapter.getTasks();

                        mDb.taskDao().deleteTask(tasks.get(position)); /** Call deleteTask in the taskDao with the task at that position*/

                        loadData(); //Call loadData method to refresh the UI
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

    /**Loading Data from database and setting into an adapter in here*/
    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaskDBModelClass> tasks = mDb.taskDao().loadAllTasks(); /**Query to load all data*/

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
    public void onItemClick(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, itemId); /**ID of the item to be updated*/
        Log.d(TAG, "onItemClick: "+String.valueOf(itemId));
        startActivity(intent);
    }
}
