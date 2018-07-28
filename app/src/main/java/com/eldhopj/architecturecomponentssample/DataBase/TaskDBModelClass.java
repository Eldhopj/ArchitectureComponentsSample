package com.eldhopj.architecturecomponentssample.DataBase;

/** Create getters and setters and two different constructors
 *      Note : Room only can use one constructor but we have two
 *              First one is to write data and second one is to read data
 *              We add @ignore to first constructor  so it uses second constructor*/

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "task")/**Create a database named task
 *      Note : if we didn't give "(tableName = "task")" the db name will be the name of the model class ie,"TaskDBModelClass"*/

public class TaskDBModelClass {
    // each of the variables are associated with the column names in the db
    // Note : @ignore annotation is used to remove the element from Room's processing logic
    @PrimaryKey (autoGenerate = true) /**Setting up id as primary key and make it autoGenerate so, we no need to do it manually*/
    private int id;
    private String description;
    private int priority;
    @ColumnInfo(name = "updated_at") /**Db name will be "updated_at" */
    private Date updatedAt;

    @Ignore  // We add @Ignore to first constructor so it uses second constructor
    public TaskDBModelClass(String description, int priority, Date updatedAt) {
        this.description = description;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public TaskDBModelClass(int id, String description, int priority, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
