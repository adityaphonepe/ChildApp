package com.phonepe.childapp;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = "ChildApp";
    
    private static final String AUTHORITY = "com.phonepe.parentapp.key";
    private static final String PATH = "external";

    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private Uri uri;

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private final static int[] TO_IDS = {
            android.R.id.text1
    };

    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the CONTACT_KEY column
    private static final int CONTACT_KEY_INDEX = 1;

    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = Uri.parse("content://"+AUTHORITY+"/"+PATH);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        progressBar.setVisibility(View.INVISIBLE);
        mContactsList = findViewById(R.id.listView);

        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
//        getSupportLoaderManager().initLoader(0, null, this);

        Cursor cursor = getContentResolver().query(uri, null, null, null);
        if(cursor != null) {
            Log.d(TAG, "onLoadFinished: cursor not null and its count is "+cursor.getCount());
        } else {
            Log.d(TAG, "onLoadFinished: cursor is null");
        }
        //mCursorAdapter.swapCursor(cursor);
    }

    public void loadText(View view) {
        progressBar.setVisibility(View.VISIBLE);
//        ContentProviderClient client = getContentResolver().acquireContentProviderClient(uri);
//        if(client != null) {
//            Log.d(TAG, "loadText: Client is not null");
//        } else {
//            Log.d(TAG, "loadText: Client is null");
//        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(loader != null) {
            Log.d(TAG, "onLoadFinished: loader not null");
        } else {
            Log.d(TAG, "onLoadFinished: Loader is null");
        }

        if(cursor != null) {
            Log.d(TAG, "onLoadFinished: cursor not null and its count is "+cursor.getCount());
        } else {
            Log.d(TAG, "onLoadFinished: cursor is null");
        }

        // User for Contacts
        mCursorAdapter.swapCursor(cursor);

        // Use for Parent app
//        if(cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            String text = cursor.getString(cursor.getColumnIndex("TEXT"));
//            textView.setText(text);
//        } else {
//            Log.d(TAG, "onLoadFinished: Cursor is Null");
//            Toast.makeText(getApplicationContext(), "Cursor is null or its count is zero", Toast.LENGTH_SHORT).show();
//        }
//        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        // Get the Cursor
//        Cursor cursor = parent.getAdapter().getC
//        // Move to the selected contact
//        cursor.moveToPosition(position);
//        // Get the _ID value
//        mContactId = cursor.getLong(CONTACT_ID_INDEX);
//        // Get the selected LOOKUP KEY
//        mContactKey = cursor.getString(CONTACT_KEY_INDEX);
//        // Create the contact's content Uri
//        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
    }
}
