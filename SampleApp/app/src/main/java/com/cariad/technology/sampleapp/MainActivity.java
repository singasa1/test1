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

import technology.cariad.partnerlibrary.MileageListener;
import technology.cariad.partnerlibrary.PartnerLibrary;
import technology.cariad.partnerlibrary.CarDataManager;
import technology.cariad.partnerlibrary.ILibStateChangeListener;
public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();

    Button mStartServiceButton;
    Button mCarDataButton;
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

                    mPartnerLibrary.start();
                } catch (Exception e) {
                    mCarDataButton.setVisibility(View.INVISIBLE);
                    mServiceStatusTextView.setText(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                mCarDataButton.setVisibility(View.INVISIBLE);
                mPartnerLibrary.stop();
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
        mPartnerLibrary = PartnerLibraryManager.getInstance(this).getPartnerLibrary();
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
                    Toast.makeText(MainActivity.this, "Start and Connect to Service to use CarData", Toast.LENGTH_LONG).show();
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
//        if (mLibHandle.isPartnerEnablerServiceReady()) {
//        Log.d(TAG,"PartnerenablerService is installed");
        mPartnerLibrary.initialize();
//        } else {
//            Log.d(TAG,"PartnerenablerService is not installed, either we can continue without PES for now or ask library to show popup to install the latest version of PES");
//            btn.setVisibility(View.INVISIBLE);
//            mLibHandle.requestUserToInstallDependency();
//        }

    }

    private void deinitializePartnerLibrary() {
        if (mPartnerLibrary != null) {
            try {
                mIsServiceConnected = false;
                mPartnerLibrary.stop();
                mPartnerLibrary.release();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onDestroy () {
        Log.d(TAG,"onDestroy");
        deinitializePartnerLibrary();
        super.onDestroy();
    }
}