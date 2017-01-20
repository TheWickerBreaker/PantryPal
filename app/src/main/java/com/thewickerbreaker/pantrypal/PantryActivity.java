package com.thewickerbreaker.pantrypal;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thewickerbreaker.pantrypal.data.PantryContract.FoodEntry;

/**
 * Displays list of food that was entered and stored in the app.
 */
public class PantryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the food data loader
     */
    private static final int FOOD_LOADER = 0;
    /**
     * Adapter for the ListView
     */
    PantryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the food data
        ListView itemListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);
        mCursorAdapter = new PantryCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(PantryActivity.this, EditorActivity.class);
                // Form the content URI that represents the specific food item that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link FoodEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.food/food/2"
                // if the food item with ID 2 was clicked on.
                Uri currentFoodUri = ContentUris.withAppendedId(FoodEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentFoodUri);
                // Launch the {@link EditorActivity} to display the data for the current food item.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(FOOD_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded food data into the database. For debugging purposes only.
     */
    private void insertFood() {
        // Create a ContentValues object where column names are the keys,
        // and sample food attributes are the values.
        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_FOOD_ITEM, "Brown Rice");
        values.put(FoodEntry.COLUMN_CONTAINER_SIZE, "1000");
        values.put(FoodEntry.COLUMN_SERVING_SIZE, "100");
        values.put(FoodEntry.COLUMN_SERVINGS, "10");
        values.put(FoodEntry.COLUMN_CALORIES, "111");
        values.put(FoodEntry.COLUMN_PROTEIN, "3");
        values.put(FoodEntry.COLUMN_CARBS, "23");
        values.put(FoodEntry.COLUMN_FAT, "1");
        values.put(FoodEntry.COLUMN_FOOD_TYPE, FoodEntry.EITHER_OR);
        values.put(FoodEntry.COLUMN_FOOD_LOCATION, FoodEntry.CUPBOARD);
        // Insert a new row for sample food into the provider using the ContentResolver.
        // Use the {@link FoodEntry#CONTENT_URI} to indicate that we want to insert
        // into the food database table.
        // Receive the new content URI that will allow us to access sample data in the future.
        @SuppressWarnings("unused") Uri newUri = getContentResolver().insert(FoodEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all food in the database.
     */
    private void deleteAllFood() {
        int rowsDeleted = getContentResolver().delete(FoodEntry.CONTENT_URI, null, null);
        Log.v("PantryActivity", rowsDeleted + " rows deleted from food database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_pantry.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_pantry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertFood();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllFood();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FoodEntry._ID,
                FoodEntry.COLUMN_FOOD_ITEM,
                FoodEntry.COLUMN_CALORIES,
                FoodEntry.COLUMN_SERVINGS,
        };

        return new CursorLoader(this,
                FoodEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}