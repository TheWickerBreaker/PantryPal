package com.thewickerbreaker.pantrypal.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thewickerbreaker.pantrypal.data.PantryContract.FoodEntry;

public class PantryProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PantryProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the food table
     */
    private static final int FOOD = 100;
    /**
     * URI matcher code for the content URI for a single food item in the food table
     */
    private static final int FOOD_ITEM_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(PantryContract.CONTENT_AUTHORITY, PantryContract.PATH_FOOD, FOOD);
        sUriMatcher.addURI(PantryContract.CONTENT_AUTHORITY, PantryContract.PATH_FOOD + "/#", FOOD_ITEM_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    private PantryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PantryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                // For the FOOD code, query the food table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the food table.
                // This will perform a query on the food table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(FoodEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FOOD_ITEM_ID:
                // For the FOOD_ITEM_ID code, extract out the ID from the URI.
                // For an example URI such as "content:com.thewickerbreaker.pantrypal/food/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the food table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(FoodEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                return insertFood(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFood(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(FoodEntry.COLUMN_FOOD_ITEM);
        if (name == null) {
            throw new IllegalArgumentException("This item requires a name");
        }
        // If the container size is provided, check that it's greater than or equal to 0
        Integer containerSize = values.getAsInteger(FoodEntry.COLUMN_CONTAINER_SIZE);
        if (containerSize != null && containerSize < 0) {
            throw new IllegalArgumentException("Container Size requires valid measurement");
        }
        // If the serving size is provided, check that it's greater than or equal to 0
        Integer servingSize = values.getAsInteger(FoodEntry.COLUMN_SERVING_SIZE);
        if (servingSize != null && servingSize < 0) {
            throw new IllegalArgumentException("Serving Size requires valid measurement");
        }
        // If the servings per container is provided, check that it's greater than or equal to 0
        Integer servings = values.getAsInteger(FoodEntry.COLUMN_SERVINGS);
        if (servings != null && servings < 0) {
            throw new IllegalArgumentException("Serving per container requires valid count");
        }
        // If the calories are provided, check that it's greater than or equal to 0
        Integer calories = values.getAsInteger(FoodEntry.COLUMN_CALORIES);
        if (calories != null && calories < 0) {
            throw new IllegalArgumentException("Calories requires valid count");
        }
        // If the protein is provided, check that it's greater than or equal to 0
        Integer protein = values.getAsInteger(FoodEntry.COLUMN_PROTEIN);
        if (protein != null && protein < 0) {
            throw new IllegalArgumentException("Protein requires valid count");
        }
        // If the Carbs is provided, check that it's greater than or equal to 0
        Integer carbs = values.getAsInteger(FoodEntry.COLUMN_CARBS);
        if (carbs != null && carbs < 0) {
            throw new IllegalArgumentException("Carbs requires valid count");
        }
        // If the fat is provided, check that it's greater than or equal to 0
        Integer fat = values.getAsInteger(FoodEntry.COLUMN_FAT);
        if (fat != null && fat < 0) {
            throw new IllegalArgumentException("Fat requires valid count");
        }
        // Check that the type is valid
        Integer type = values.getAsInteger(FoodEntry.COLUMN_FOOD_TYPE);
        if (type == null || !FoodEntry.isValidType(type)) {
            throw new IllegalArgumentException("Food requires valid type");
        }
        // Check that the location is valid
        Integer location = values.getAsInteger(FoodEntry.COLUMN_FOOD_LOCATION);
        if (location == null || !FoodEntry.isValidLocation(location)) {
            throw new IllegalArgumentException("Food requires valid type");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new food item with the given values
        long id = database.insert(FoodEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the food item content URI
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                return updateFood(uri, contentValues, selection, selectionArgs);
            case FOOD_ITEM_ID:
                // For the FOOD_ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFood(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update food in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more food items).
     * Return the number of rows that were successfully updated.
     */
    private int updateFood(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link FoodEntry#COLUMN_FOOD_ITEM} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_FOOD_ITEM)) {
            String name = values.getAsString(FoodEntry.COLUMN_FOOD_ITEM);
            if (name == null) {
                throw new IllegalArgumentException("This Food Item requires a name.");
            }
        }
        // If the {@link FoodEntry#COLUMN_CONTAINER_SIZE} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_CONTAINER_SIZE)) {
            Integer containerSize = values.getAsInteger(FoodEntry.COLUMN_CONTAINER_SIZE);
            if (containerSize != null && containerSize < 0) {
                throw new IllegalArgumentException("The Container Size requires a measurement.");
            }
        }
        // If the {@link FoodEntry#COLUMN_SERVING_SIZE} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_SERVING_SIZE)) {
            Integer servingSize = values.getAsInteger(FoodEntry.COLUMN_SERVING_SIZE);
            if (servingSize != null && servingSize < 0) {
                throw new IllegalArgumentException("This Serving Size requires a measurement.");
            }
        }
        // If the {@link FoodEntry#COLUMN_SERVINGS} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_SERVINGS)) {
            Integer servings = values.getAsInteger(FoodEntry.COLUMN_SERVINGS);
            if (servings != null && servings < 0) {
                throw new IllegalArgumentException("The Servings entry require a count.");
            }
        }
        // If the {@link FoodEntry#COLUMN_CALORIES} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_CALORIES)) {
            Integer calories = values.getAsInteger(FoodEntry.COLUMN_CALORIES);
            if (calories != null && calories < 0) {
                throw new IllegalArgumentException("The Calories entry require a count.");
            }
        }
        // If the {@link FoodEntry#COLUMN_PROTEIN} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_PROTEIN)) {
            Integer protein = values.getAsInteger(FoodEntry.COLUMN_PROTEIN);
            if (protein != null && protein < 0) {
                throw new IllegalArgumentException("The Protein entry require a count.");
            }
        }
        // If the {@link FoodEntry#COLUMN_CARBS} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_CARBS)) {
            Integer carbs = values.getAsInteger(FoodEntry.COLUMN_CARBS);
            if (carbs != null && carbs < 0) {
                throw new IllegalArgumentException("The Carbs entry require a count.");
            }
        }
        // If the {@link FoodEntry#COLUMN_FAT} key is present,
        // check that the name value is not null.
        if (values.containsKey(FoodEntry.COLUMN_FAT)) {
            Integer fat = values.getAsInteger(FoodEntry.COLUMN_FAT);
            if (fat != null && fat < 0) {
                throw new IllegalArgumentException("The Fat entry require a count.");
            }
        }
        values.put(FoodEntry.COLUMN_FOOD_TYPE, FoodEntry.EITHER_OR);
        values.put(FoodEntry.COLUMN_FOOD_LOCATION, FoodEntry.CUPBOARD);
        // If the {@link FoodEntry#COLUMN_FOOD_TYPE} key is present,
        // check that the type value is valid.
        if (values.containsKey(FoodEntry.COLUMN_FOOD_TYPE)) {
            Integer type = values.getAsInteger(FoodEntry.COLUMN_FOOD_TYPE);
            if (type == null || !FoodEntry.isValidType(type)) {
                throw new IllegalArgumentException("Food requires valid type.");
            }
        }
        // If the {@link FoodEntry#COLUMN_FOOD_LOCATION} key is present,
        // check that the location value is valid.
        if (values.containsKey(FoodEntry.COLUMN_FOOD_LOCATION)) {
            Integer location = values.getAsInteger(FoodEntry.COLUMN_FOOD_LOCATION);
            if (location == null || !FoodEntry.isValidLocation(location)) {
                throw new IllegalArgumentException("Food requires valid location.");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(FoodEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                rowsDeleted = database.delete(FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FOOD_ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                return FoodEntry.CONTENT_LIST_TYPE;
            case FOOD_ITEM_ID:
                return FoodEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}