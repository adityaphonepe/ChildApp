package com.phonepe.childapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepe.parentapp.IKeyRetrival;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ChildApp";
    
    private static final String AUTHORITY = "com.phonepe.parentapp.key";
    private static final String PATH = "external";

    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private Uri uri;
    private IKeyRetrival keyRetrivalService;
    ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = Uri.parse("content://"+AUTHORITY+"/"+PATH);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public void loadText(View view) {
        progressBar.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void loadTextAidl(View view) {
        if(keyRetrivalService != null) {
            try {
                textView.setText(keyRetrivalService.getKey());
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }
        } else {
            initConnection();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        return new CursorLoader(
                this,
                uri,
                null,
                null,
                null,
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

        // Use for Parent app
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String text = cursor.getString(cursor.getColumnIndex("TEXT"));
            textView.setText(text);
        } else {
            Log.d(TAG, "onLoadFinished: Cursor is Null");
            Toast.makeText(getApplicationContext(), "Cursor is null or its count is zero", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }


    void initConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                keyRetrivalService = IKeyRetrival.Stub.asInterface(service);
                Log.d(TAG, "Service Connected: Binding done");
                //Toast.makeText(getApplicationContext(), "AIDL Connection Established", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                keyRetrivalService = null;
                Log.d(TAG, "Service Disconnected");
                //Toast.makeText(getApplicationContext(), "AIDL Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        if(keyRetrivalService == null) {
            Intent it = new Intent();
            it.setAction("com.phonepe.app.remote.service.KEYRETRIVAL");
            it.setComponent(new ComponentName("com.phonepe.parentapp", "com.phonepe.parentapp.KeyRetrivalService"));
            //binding to remote service
            boolean result = false;
            int count = 0;
            while(count < 15) {
                count++;
                result = bindService(it, serviceConnection, Service.BIND_AUTO_CREATE);
                if(result) break;
                //Toast.makeText(getApplicationContext(), "Not connected trying Content Provider", Toast.LENGTH_SHORT).show();
                //Log.d(TAG, "Not connected trying Content Provider");
                //unbindService(serviceConnection);
                //getSupportLoaderManager().initLoader(0, null, this);
            }
            Log.d(TAG, "initConnection: Result: "+result+" Count: "+count);
        }
    }
}
