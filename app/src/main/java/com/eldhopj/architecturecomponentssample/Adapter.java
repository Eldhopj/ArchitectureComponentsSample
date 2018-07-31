package com.eldhopj.architecturecomponentssample;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eldhopj.architecturecomponentssample.DataBase.TaskDBModelClass;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    private List<TaskDBModelClass> mTaskEntries;
    private Context mContext;
    private OnItemClickListener mListener; // Listener for the OnItemClickListener interface

    private static final String DATE_FORMAT = "dd/MM/yyy"; // Constant for date format
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()); // Date formatter


    public Adapter(List<TaskDBModelClass> mTaskEntries, Context mContext) {
        this.mTaskEntries = mTaskEntries;
        this.mContext = mContext;
    }

    // interface will forward our click and data from adapter to our main activity

    public interface OnItemClickListener {
        void onItemClick(int elementId);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// this method calls when ever our view method is created , ie; the instance of ViewHolder class is created
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false); //list_item-> is the Card view which holds the data in the recycler view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//populate the data into the list_item (View Holder), as we scroll
        //Binding data to the list_item
        TaskDBModelClass taskEntry = mTaskEntries.get(position);

        //Getting value
        String description = taskEntry.getDescription();
        int priority = taskEntry.getPriority();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());

        //Setting values
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.priorityView.setText(String.valueOf(priority));

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    }

    /*
    Getting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

    //View Holder class caches these references that gonna modify in the adapter
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Define viewHolder views (list_item) here
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        //create a constructor with itemView as a params
        public ViewHolder(View itemView) { // with the help of "itemView" we ge the views from xml
            super(itemView);
            //bind views
            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);

            //Assigning on click listener on the item and passing the ID value of the item
            itemView.setOnClickListener(new View.OnClickListener() { // we can handle the click as like we do in normal
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int elementId = mTaskEntries.get(getAdapterPosition()).getId(); /** Get the id of the item on that position*/
                        mListener.onItemClick(elementId); // we catch the id on the item view then pass it over the interface and then to our activity
                    }
                }
            });
        }
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskDBModelClass> taskEntries) {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    //Retrieve list of task entry objects from our adapter
    public List<TaskDBModelClass> getTasks() {
        return mTaskEntries;
    }
}

