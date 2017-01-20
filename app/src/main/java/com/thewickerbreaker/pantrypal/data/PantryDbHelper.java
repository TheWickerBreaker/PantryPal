package com.thewickerbreaker.pantrypal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thewickerbreaker.pantrypal.data.PantryContract.FoodEntry;

class PantryDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "pantry.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PantryDbHelper}.
     *
     * @param context of the app
     */
    PantryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the food table
        String SQL_CREATE_FOOD_TABLE = "CREATE TABLE " + FoodEntry.TABLE_NAME + " ("
                + FoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FoodEntry.COLUMN_FOOD_ITEM + " TEXT NOT NULL, "
                + FoodEntry.COLUMN_CONTAINER_SIZE + " INTEGER NOT NULL, "
                + FoodEntry.COLUMN_SERVING_SIZE + " INTEGER NOT NULL, "
                + FoodEntry.COLUMN_SERVINGS + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_CALORIES + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_PROTEIN + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_CARBS + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_FAT + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_FOOD_TYPE + " INTEGER NOT NULL,"
                + FoodEntry.COLUMN_FOOD_LOCATION + " INTEGER NOT NULL);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_FOOD_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

