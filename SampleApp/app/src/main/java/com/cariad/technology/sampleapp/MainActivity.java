package com.cariad.technology.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.Response;
import com.volkswagenag.partnerlibrary.ILibStateChangeListener;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();

    private Button mStartServiceButton;
    private Button mCarDataButton;
    private Button mNavigationButton;
    private TextView mServiceStatusTextView;
    private ProgressBar mServiceStateProgressBar;

    private PartnerLibrary mPartnerLibrary;

    private LibStateListener mLibStateChangeListener;
    private boolean mIsServiceConnected = false;


    class LibStateListener implements ILibStateChangeListener {

        @Override
        public void onStateChanged(boolean ready) throws RemoteException {
            Log.d(TAG,"LibState status: " + ready);
            mServiceStateProgressBar.setVisibility(View.INVISIBLE);
            if (ready) {
                try {
                    mStartServiceButton.setText(R.string.stop_service);
                    mIsServiceConnected = true;
                    mServiceStatusTextView.setText(R.string.service_state_ready);
                    mCarDataButton.setVisibility(View.VISIBLE);
                    mNavigationButton.setVisibility(View.VISIBLE);

                    Response.Status status = mPartnerLibrary.start();
                    if (status != Response.Status.SUCCESS) {
                        Log.e(TAG, "Failure in starting the service " + status.toString());
                        showToast("Failure in starting the service: " + status.toString());
                    }
                } catch (Exception e) {
                    mCarDataButton.setVisibility(View.INVISIBLE);
                    mNavigationButton.setVisibility(View.INVISIBLE);
                    mServiceStatusTextView.setText(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                mCarDataButton.setVisibility(View.INVISIBLE);
                mNavigationButton.setVisibility(View.INVISIBLE);
                Response.Status status = mPartnerLibrary.stop();
                if (status != Response.Status.SUCCESS) {
                    Log.e(TAG, "Failure in stopping the service " + status.toString());
                    showToast("Failure in stopping the service: " + status.toString());
                }
                mServiceStatusTextView.setText(R.string.service_state_error);
            }
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        mLibStateChangeListener = new LibStateListener();
        mPartnerLibrary = PartnerLibrary.getInstance(this);
        mIsServiceConnected = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceStatusTextView.setText("");
    }

    private void initializeViews() {
        mStartServiceButton = (Button) findViewById(R.id.button_service_status);
        mCarDataButton = (Button) findViewById(R.id.button_cardata);
        mNavigationButton = (Button) findViewById(R.id.button_navigation);
        mServiceStatusTextView = (TextView) findViewById(R.id.text_service_state);
        mServiceStateProgressBar = (ProgressBar) findViewById(R.id.progressbar_service_state);

        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsServiceConnected) {
                    Log.d(TAG, "Clicked start PartnerLibrary");
                    mServiceStateProgressBar.setVisibility(View.VISIBLE);
                    mServiceStatusTextView.setVisibility(View.VISIBLE);
                    mServiceStatusTextView.setText(R.string.service_state_connecting);
                    initializePartnerLibrary();
                } else {
                    Log.d(TAG, "Clicked stop PartnerLibrary");
                    deinitializePartnerLibrary();
                    mServiceStatusTextView.setText("Service disconnected");
                    mStartServiceButton.setText(R.string.start_service);
                }

            }
        });

        mCarDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsServiceConnected) {
                    Log.d(TAG, "Going to CarData Activity");
                    goTo(CarDataActivity.class);
                } else {
                    showToast("Start and Connect to Service to use CarData");
                }

            }
        });

        mNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsServiceConnected) {
                    Log.d(TAG, "Going to Navigation Activity");
                    goTo(NavigationActivity.class);
                } else {
                    showToast("Start and Connect to Service to use Navigation");
                }
            }
        });
    }

    private void goTo(Class<?> nextActivity) {
        Intent myIntent = new Intent(MainActivity.this, nextActivity);
        MainActivity.this.startActivity(myIntent);
    }

    private void initializePartnerLibrary() {
        Log.d(TAG, "initialize");
        mPartnerLibrary.addListener(mLibStateChangeListener);
        if (mPartnerLibrary.initialize() != Response.Status.SUCCESS) {
            Log.e(TAG, "Failure in service initialization");
            showToast("Failure in service initialization!");
        }
    }

    private void deinitializePartnerLibrary() {
        if (mPartnerLibrary != null) {
            try {
                mIsServiceConnected = false;
                Response.Status status = mPartnerLibrary.stop();
                if (status != Response.Status.SUCCESS) {
                    Log.e(TAG, "Failure in stopping the service " + status.toString());
                    showToast("Failure in stopping the service: " + status.toString());
                }
                mPartnerLibrary.release();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy () {
        Log.d(TAG,"onDestroy");
        deinitializePartnerLibrary();
        super.onDestroy();
    }
}
