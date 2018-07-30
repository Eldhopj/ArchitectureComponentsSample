package com.eldhopj.architecturecomponentssample;
/**This class is for Saving into DB*/

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.eldhopj.architecturecomponentssample.DataBase.AppDatabase;
import com.eldhopj.architecturecomponentssample.DataBase.AppExecutors;
import com.eldhopj.architecturecomponentssample.DataBase.TaskDBModelClass;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    private AppDatabase mDb; // Member variable for the Database
    EditText mEditText;
    RadioGroup mRadioGroup;

    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        mDb = AppDatabase.getInstance(getApplicationContext()); //Initialize member variable for the data base

        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
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
                mDb.taskDao().insertTask(taskEntry);// This will add data into our db
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

}
