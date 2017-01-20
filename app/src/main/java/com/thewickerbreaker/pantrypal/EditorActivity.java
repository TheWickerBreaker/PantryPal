package com.thewickerbreaker.pantrypal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.thewickerbreaker.pantrypal.data.PantryContract.FoodEntry;

/**
 * Allows user to create a new food item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the food data loader
     */
    private static final int EXISTING_FOOD_LOADER = 0;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    int servings;
    /**
     * Content URI for the existing food item (null if it's a new food item)
     */
    private Uri mCurrentFoodUri;
    /**
     * EditText field to enter the food item name
     */
    private EditText mItemEditText;
    /**
     * EditText field to enter the container size
     */
    private EditText mContainerSizeEditText;
    /**
     * EditText field to enter the serving size.
     */
    private EditText mServingSizeEditText;
    /**
     * EditText field to enter the servings per container.
     */
    private EditText mServingsEditText;
    /**
     * EditText field to enter the calories.
     */
    private EditText mCaloriesEditText;
    /**
     * EditText field to enter the protein.
     */
    private EditText mProteinEditText;
    /**
     * EditText field to enter the carbs.
     */
    private EditText mCarbsEditText;
    /**
     * EditText field to enter the fat.
     */
    private EditText mFatEditText;
    /**
     * EditText field to enter the food item type
     */
    private Spinner mTypeSpinner;
    /**
     * Type. The possible values are:
     * 0 for complete meal, 1 for ingredient, 2 for seasoning.
     */
    private int mType = FoodEntry.COMPLETE_MEAL;
    /**
     * EditText field to enter the food item location
     */
    private Spinner mLocationSpinner;
    /**
     * Location. The possible values are:
     * 0 for cupboard, 1 for fridge, 2 for top of fridge.
     */
    private int mLocation = FoodEntry.CUPBOARD;
    /**
     * Boolean flag that keeps track of whether the Item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentFoodUri = intent.getData();
        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        if (mCurrentFoodUri == null) {
            // This is a new food item, so change the app bar to say "Add An Item"
            setTitle(getString(R.string.editor_activity_title_new_item));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change app bar to say "Edit Item Info"
            setTitle(getString(R.string.editor_activity_title_edit_item_info));
            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_FOOD_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mItemEditText = (EditText) findViewById(R.id.edit_item);
        mContainerSizeEditText = (EditText) findViewById(R.id.edit_container_size);
        mServingSizeEditText = (EditText) findViewById(R.id.edit_serving_size);
        mServingsEditText = (EditText) findViewById(R.id.edit_servings);
        mCaloriesEditText = (EditText) findViewById(R.id.edit_calories);
        mProteinEditText = (EditText) findViewById(R.id.edit_protein);
        mCarbsEditText = (EditText) findViewById(R.id.edit_carbs);
        mFatEditText = (EditText) findViewById(R.id.edit_fat);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mLocationSpinner = (Spinner) findViewById(R.id.spinner_location);
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mItemEditText.setOnTouchListener(mTouchListener);
        mContainerSizeEditText.setOnTouchListener(mTouchListener);
        mServingSizeEditText.setOnTouchListener(mTouchListener);
        mServingsEditText.setOnTouchListener(mTouchListener);
        mCaloriesEditText.setOnTouchListener(mTouchListener);
        mProteinEditText.setOnTouchListener(mTouchListener);
        mCarbsEditText.setOnTouchListener(mTouchListener);
        mFatEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mLocationSpinner.setOnTouchListener(mTouchListener);

        setupTypeSpinner();
        setupLocationSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the type of food item.
     */
    private void setupTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.complete_meal))) {
                        mType = FoodEntry.COMPLETE_MEAL; // complete meal
                    } else if (selection.equals(getString(R.string.ingredient))) {
                        mType = FoodEntry.INGREDIENT; // ingredient
                    } else if (selection.equals(getString(R.string.either_or))) {
                        mType = FoodEntry.EITHER_OR; // either or
                    } else {
                        mType = FoodEntry.SEASONING; // seasoning
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = FoodEntry.INGREDIENT; // ingredient
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the location of the food item.
     */
    private void setupLocationSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter locationSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_location_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mLocationSpinner.setAdapter(locationSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.cupboard))) {
                        mLocation = FoodEntry.CUPBOARD; // cupboard
                    } else if (selection.equals(getString(R.string.fridge))) {
                        mLocation = FoodEntry.FRIDGE; // fridge
                    } else {
                        mLocation = FoodEntry.TOP_OF_THE_FRIDGE; // top of fridge
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLocation = FoodEntry.CUPBOARD; // cupboard
            }
        });
    }

    /**
     * Get user input from editor and save food item into database.
     */
    private void saveFoodItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String itemEditText = mItemEditText.getText().toString().trim();
        String containerSizeEditText = mContainerSizeEditText.getText().toString().trim();
        String servingSizeEditText = mServingSizeEditText.getText().toString().trim();
        String servingsEditText = mServingsEditText.getText().toString().trim();
        String caloriesEditText = mCaloriesEditText.getText().toString().trim();
        String proteinEditText = mProteinEditText.getText().toString().trim();
        String carbsEditText = mCarbsEditText.getText().toString().trim();
        String fatEditText = mFatEditText.getText().toString().trim();
        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentFoodUri == null &&
                TextUtils.isEmpty(itemEditText) && TextUtils.isEmpty(containerSizeEditText) &&
                TextUtils.isEmpty(servingSizeEditText) && TextUtils.isEmpty(servingsEditText)
                && TextUtils.isEmpty(caloriesEditText) && TextUtils.isEmpty(proteinEditText)
                && TextUtils.isEmpty(carbsEditText) && TextUtils.isEmpty(fatEditText)
                && mType == FoodEntry.INGREDIENT && mLocation == FoodEntry.CUPBOARD) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and food item attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_FOOD_ITEM, itemEditText);
        values.put(FoodEntry.COLUMN_CONTAINER_SIZE, containerSizeEditText);
        values.put(FoodEntry.COLUMN_SERVING_SIZE, servingSizeEditText);
        values.put(FoodEntry.COLUMN_SERVINGS, servingsEditText);
        values.put(FoodEntry.COLUMN_CALORIES, caloriesEditText);
        values.put(FoodEntry.COLUMN_PROTEIN, proteinEditText);
        values.put(FoodEntry.COLUMN_CARBS, carbsEditText);
        values.put(FoodEntry.COLUMN_FAT, fatEditText);
        values.put(FoodEntry.COLUMN_FOOD_TYPE, mType);
        values.put(FoodEntry.COLUMN_FOOD_LOCATION, mLocation);
        // Determine if this is a new or existing item by checking if mCurrentFoodUri is null or not
        if (mCurrentFoodUri == null) {
            // This is a NEW food item, so insert a new food item into the provider,
            // returning the content URI for the new food item.
            Uri newUri = getContentResolver().insert(FoodEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING food item, so update the food item with content URI: mCurrentFoodUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentFoodUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new food item, hide the "Delete" menu item.
        if (mCurrentFoodUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save food item to database
                saveFoodItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (PantryActivity)
                // If the food item hasn't changed, continue with navigating up to parent activity
                // which is the {@link PantryActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the food item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all food item attributes, define a projection that contains
        // all columns from the food table
        String[] projection = {
                FoodEntry._ID,
                FoodEntry.COLUMN_FOOD_ITEM,
                FoodEntry.COLUMN_CONTAINER_SIZE,
                FoodEntry.COLUMN_SERVING_SIZE,
                FoodEntry.COLUMN_SERVINGS,
                FoodEntry.COLUMN_CALORIES,
                FoodEntry.COLUMN_PROTEIN,
                FoodEntry.COLUMN_CARBS,
                FoodEntry.COLUMN_FAT,
                FoodEntry.COLUMN_FOOD_TYPE,
                FoodEntry.COLUMN_FOOD_LOCATION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentFoodUri,         // Query the content URI for the current food item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of food item attributes that we're interested in
            int foodItemColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_ITEM);
            int containerSizeColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_CONTAINER_SIZE);
            int servingSizeColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_SERVING_SIZE);
            int servingsColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_SERVINGS);
            int caloriesColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_CALORIES);
            int proteinColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_PROTEIN);
            int carbsColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_CARBS);
            int fatColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FAT);
            int typeColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_TYPE);
            int locationColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_LOCATION);
            // Extract out the value from the Cursor for the given column index
            String foodItem = cursor.getString(foodItemColumnIndex);
            int containerSize = cursor.getInt(containerSizeColumnIndex);
            int servingSize = cursor.getInt(servingSizeColumnIndex);
            servings = cursor.getInt(servingsColumnIndex);
            int calories = cursor.getInt(caloriesColumnIndex);
            int protein = cursor.getInt(proteinColumnIndex);
            int carbs = cursor.getInt(carbsColumnIndex);
            int fat = cursor.getInt(fatColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            int location = cursor.getInt(locationColumnIndex);
            // Update the views on the screen with the values from the database
            mItemEditText.setText(foodItem);
            mContainerSizeEditText.setText(Integer.toString(containerSize));
            mServingSizeEditText.setText(Integer.toString(servingSize));
            mServingsEditText.setText(Integer.toString(servings));
            mCaloriesEditText.setText(Integer.toString(calories));
            mProteinEditText.setText(Integer.toString(protein));
            mCarbsEditText.setText(Integer.toString(carbs));
            mFatEditText.setText(Integer.toString(fat));
            // Type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Complete Meal, 1 is Ingredient, 2 is Either Or, 3 is Seasoning).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (type) {
                case FoodEntry.COMPLETE_MEAL:
                    mTypeSpinner.setSelection(0);
                    break;
                case FoodEntry.EITHER_OR:
                    mTypeSpinner.setSelection(2);
                    break;
                case FoodEntry.SEASONING:
                    mTypeSpinner.setSelection(3);
                    break;
                default:
                    mTypeSpinner.setSelection(1);
                    break;
            }
            // Location is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Cupboard, 1 is Fridge, 2 is Top of Fridge).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (location) {
                case FoodEntry.FRIDGE:
                    mTypeSpinner.setSelection(1);
                    break;
                case FoodEntry.TOP_OF_THE_FRIDGE:
                    mTypeSpinner.setSelection(2);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mItemEditText.setText("");
        mContainerSizeEditText.setText("");
        mServingSizeEditText.setText("");
        mServingsEditText.setText("");
        mCaloriesEditText.setText("");
        mProteinEditText.setText("");
        mCarbsEditText.setText("");
        mFatEditText.setText("");
        mTypeSpinner.setSelection(1); // Select "Ingredient"
        mLocationSpinner.setSelection(0); // Select "Cupboard"
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the food item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this food item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the food item.
                deleteFoodItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the food item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the food item in the database.
     */
    private void deleteFoodItem() {
        // Only perform the delete if this is an existing food item.
        if (mCurrentFoodUri != null) {
            // Call the ContentResolver to delete the food item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentFoodUri
            // content URI already identifies the food item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentFoodUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void add(View view) {
        servings = servings + 1;
        mServingsEditText.setText(Integer.toString(servings));
    }

    @SuppressLint("SetTextI18n")
    public void spoil(View view) {

        if (servings > 1) {
            servings = servings - 1;
            mServingsEditText.setText(Integer.toString(servings));
        } else {
            showDeleteConfirmationDialog();
        }
    }

    @SuppressLint("SetTextI18n")
    public void eat(View view) {
        if (servings > 1) {
            servings = servings - 1;
            mServingsEditText.setText(Integer.toString(servings));
        } else {
            showDeleteConfirmationDialog();
        }
    }
}