package com.thewickerbreaker.pantrypal.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PantryContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    static final String CONTENT_AUTHORITY = "com.thewickerbreaker.pantrypal";
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.food/food/ is a valid path for
     * looking at food item data. content://com.thewickerbreaker.pantrypal/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    static final String PATH_FOOD = "food";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PantryContract() {
    }

    /**
     * Inner class that defines constant values for the food database table.
     * Each entry in the table represents a single food item.
     */
    public static final class FoodEntry implements BaseColumns {
        /**
         * The content URI to access the food item data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOOD);
        /**
         * Unique ID number for the food item (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the food item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_FOOD_ITEM = "food";
        /**
         * Servings per container
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SERVINGS = "servings";
        /**
         * Servings size.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SERVING_SIZE = "size";
        /**
         * Total container size
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CONTAINER_SIZE = "container";
        /**
         * Calories from food
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CALORIES = "calories";
        /**
         * Protein in food
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PROTEIN = "protein";
        /**
         * Carbs in food
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CARBS = "carbs";
        /**
         * Fat in food
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_FAT = "fat";
        /**
         * Food Types.
         * <p>
         * The only possible values are {@link #COMPLETE_MEAL}, {@link #INGREDIENT},
         * or {@link #SEASONING}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_TYPE = "type";
        /**
         * Food Location.
         * <p>
         * The only possible values are {@link #CUPBOARD}, {@link #FRIDGE},
         * or {@link #TOP_OF_THE_FRIDGE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_LOCATION = "location";
        /**
         * Possible values for the types of food.
         */
        public static final int COMPLETE_MEAL = 0;
        public static final int INGREDIENT = 1;
        public static final int EITHER_OR = 2;
        public static final int SEASONING = 3;
        /**
         * Possible values for the location of the food item.
         */
        public static final int CUPBOARD = 0;
        public static final int FRIDGE = 1;
        public static final int TOP_OF_THE_FRIDGE = 2;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of food items.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single food item.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;
        /**
         * Name of database table for food items
         */
        final static String TABLE_NAME = "food";

        /**
         * Returns whether or not the given type is {@link #COMPLETE_MEAL}, {@link #INGREDIENT}, or {@link #EITHER_OR},
         * or {@link #SEASONING}.
         */
        static boolean isValidType(int type) {
            return type == COMPLETE_MEAL || type == INGREDIENT || type == EITHER_OR || type == SEASONING;
        }

        /**
         * Returns whether or not the given location is {@link #CUPBOARD}, {@link #FRIDGE},
         * or {@link #TOP_OF_THE_FRIDGE}.
         */
        static boolean isValidLocation(int location) {
            return location == CUPBOARD || location == FRIDGE || location == TOP_OF_THE_FRIDGE;
        }
    }
}