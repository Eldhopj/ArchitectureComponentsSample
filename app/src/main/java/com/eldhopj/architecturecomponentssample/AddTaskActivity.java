package com.eldhopj.architecturecomponentssample;
/**This class is for Saving into DB
 *
 * Commit 5 : Updating the values*/

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.AppExecutors;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBModelClass;

import java.util.Date;

import static com.eldhopj.architecturecomponentssample.MainActivity.EXTRA_TASK_ID;

public class AddTaskActivity extends AppCompatActivity {
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    private static final int DEFAULT_TASK_ID = -1;// Constant for default task id to be used when not in update mode
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    private AppDatabase mDb; // Member variable for the Database
    private int mTaskId=DEFAULT_TASK_ID; // if the default task is same as the mTask id it definitely an add task

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        mDb = AppDatabase.getInstance(getApplicationContext()); //Initialize member variable for the data base

        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
        mButton = findViewById(R.id.saveButton);

        Intent intent = getIntent(); //Intent coming from MainActivity
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText("Update");
            /* Assign the value of EXTRA_TASK_ID in the intent to mTaskId,
             Use -1 as the default because the ID starts from 0*/
            mTaskId = intent.getIntExtra(EXTRA_TASK_ID,-1);
            if (mTaskId != DEFAULT_TASK_ID) { // check whether to update or not
                // populate the UI
                /**loadTaskById method is to retrieve the data belongs to id, mTaskId*/
                final LiveData<TaskDBModelClass> task = mDb.taskDao().loadTaskById(mTaskId);/**Getting LiveData object*/
                // NOTE : LiveData by default runs outside the main thread, so we remove the executors.
                /** @param lifecycleOwner ->
                 *  @param Observer */
                task.observe(this, new Observer<TaskDBModelClass>() { /**Calling its Observe method*/
                @Override
                    /*NOTE : onChanged runs on the main thread , So we remove runOnUiThread
                        Here the data change in the db will be updated to the recycler view*/
                public void onChanged(@Nullable TaskDBModelClass taskDBModelClass) {// interface to implement onChange method
                    task.removeObserver(this); // As we don't need lesson for changes continuous,So, we remove the observer
                    populateUI(taskDBModelClass);
                }
                });
            }
        }
    }

    public void SaveToDatabase(View view) {
        onSaveButtonClicked();
    }
    public void onSaveButtonClicked() {

        String description = mEditText.getText().toString();
        int priority = getPriorityFromViews(); //Get the priority value from Radio Group
        Date date = new Date(); //date variable and assign to it the current Date

        /**Saving into database*/
        // Make taskEntry final so it is visible inside the run method
        final TaskDBModelClass taskEntry = new TaskDBModelClass(description, priority, date); // passing value into model class
        AppExecutors.getInstance().diskIO().execute(new Runnable() { // Enables DB tans in a background thread
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    mDb.taskDao().insertTask(taskEntry);/** This will add data into our db*/
                }
                else {
                    taskEntry.setId(mTaskId);
                    mDb.taskDao().updateTask(taskEntry);/** This will update data into our db*/
                }
            }
        });
    }

    //Getting Priorities
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }
    //Setting the priorities
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    private void populateUI(TaskDBModelClass task) {
        //Check whether the task is null
        if (task == null) {
            return;
        }
        //Variable task to populate the UI
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }

}
