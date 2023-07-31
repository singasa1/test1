package com.cariad.technology.sampleapp;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import technology.cariad.partnerlibrary.MileageListener;
import technology.cariad.partnerlibrary.PartnerLibrary;
import technology.cariad.partnerlibrary.CarDataManager;
import technology.cariad.partnerlibrary.ILibStateChangeListener;

//import technology.cariad.dynamicfunctionblocking


public class MainActivity extends AppCompatActivity implements MileageListener {
    private final String TAG = MainActivity.this.getClass().getSimpleName();

    private TextView verificationResult;
    private PartnerLibrary mLibHandle;
    private CarDataManager mCarDataManager;
    private LibStateListener mLibStateChangeListener;

    @Override
    public void onMileageValueChanged(float i) {
        float val = mCarDataManager.getCurrentMileage();

        Log.d(TAG,"Current Mileage Value: " + val);
        verificationResult.setText("Odomometer value: " + val);
    }

    class LibStateListener implements ILibStateChangeListener {

        @Override
        public void onStateChanged(boolean ready) throws RemoteException {
            Log.d(TAG,"LibState is ready");
            if (ready) {
                mLibHandle.start();
                mCarDataManager = mLibHandle.getCarDataManager();
                mCarDataManager.registerMileageListener(MainActivity.this);
            } else {
                mLibHandle.stop();
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
        Button btn = (Button)findViewById(R.id.btn_start_service);
        verificationResult = (TextView)findViewById(R.id.tv);
        mLibStateChangeListener = new LibStateListener();
        mLibHandle = new PartnerLibrary(this);
        mLibHandle.addListener(mLibStateChangeListener);
//        if (mLibHandle.isPartnerEnablerServiceReady()) {
            Log.d(TAG,"PartnerenablerService is installed");
            mLibHandle.initialize();

//        } else {
//            Log.d(TAG,"PartnerenablerService is not installed, either we can continue without PES for now or ask library to show popup to install the latest version of PES");
//            btn.setVisibility(View.INVISIBLE);
//            mLibHandle.requestUserToInstallDependency();
//        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Clicked start PartnerLibrary");
                CarDataManager carDataManager = mLibHandle.getCarDataManager();
                float val = carDataManager.getCurrentMileage();

                //boolean verify = mLibHandle.verifyDigitalSignature(getPackageName());
                //if (verify) verificationResult.setText("Digital Signature Verification Succeeded");
                //else verificationResult.setText("Verification Result Failed");
                //Log.d(TAG,"Verify Result: " + verify);
                Log.d(TAG,"Current Mileage Value: " + val);
                verificationResult.setText("Odomometer value: " + val);
            }
        });
    }

    @Override
    protected void onDestroy () {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        mLibHandle.release();
    }
}