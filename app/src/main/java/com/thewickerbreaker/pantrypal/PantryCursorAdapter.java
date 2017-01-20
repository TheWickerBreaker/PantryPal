package com.thewickerbreaker.pantrypal;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thewickerbreaker.pantrypal.data.PantryContract;

class PantryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link PantryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    PantryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the food data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current food can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView foodItemTextView = (TextView) view.findViewById(R.id.food_list_item);
        TextView caloriesTextView = (TextView) view.findViewById(R.id.calories_list_item);
        TextView servingsTextView = (TextView) view.findViewById(R.id.servings_list_item);

        int foodItemColumnIndex = cursor.getColumnIndex(PantryContract.FoodEntry.COLUMN_FOOD_ITEM);
        int caloriesColumnIndex = cursor.getColumnIndex(PantryContract.FoodEntry.COLUMN_CALORIES);
        int servingColumnIndex = cursor.getColumnIndex(PantryContract.FoodEntry.COLUMN_SERVINGS);

        int perCalories = cursor.getInt(caloriesColumnIndex);
        int totalServing = cursor.getInt(servingColumnIndex);

        int totalCalories = perCalories * totalServing;

        String foodItem = cursor.getString(foodItemColumnIndex);
        String calories = String.valueOf(totalCalories);
        String servings = cursor.getString(servingColumnIndex);

        foodItemTextView.setText(foodItem);
        caloriesTextView.setText(calories);
        servingsTextView.setText(servings);
    }
}