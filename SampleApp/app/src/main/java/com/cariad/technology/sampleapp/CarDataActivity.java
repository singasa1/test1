package com.cariad.technology.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.MileageListener;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.SteeringAngleListener;
import com.volkswagenag.partnerlibrary.TurnSignalListener;
import com.volkswagenag.partnerlibrary.VehicleLightState;
import com.volkswagenag.partnerlibrary.VehicleSignalIndicator;


public class CarDataActivity extends AppCompatActivity implements MileageListener, FogLightStateListener, SteeringAngleListener, TurnSignalListener, AdapterView.OnItemSelectedListener {

    private final String TAG = CarDataActivity.this.getClass().getSimpleName();

    private Spinner mCarDataGetListSpinner;
    private TextView mResultTextView;
    private TextView mTextViewListenerUpdateMileage;
    private TextView mTextViewListenerUpdateFogLights;
    private TextView mTextViewListenerUpdateSteeringAngle;
    private TextView mTextViewListenerUpdateTurnSignalIndicator;


    private CarDataManager mCarDataManager;

    private NavigationManager mNavigationManager;

    private int mSelectedPosition;

    // NOTE: Donot change the order - only add new ones at the end.
    // {@link CarDataActivity#onItemSelected} should be changed if the order in this array is changed.
    private String[] mCarDataAPIMethods = {
            "getCurrentMileage",
            "getTurnSignalIndicator",
            "getFogLightsState",
            "getSteeringAngle",
            "isNavAppStarted",
            "getCurrentRoute"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_data);

        initializeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCarDataManager = PartnerLibrary.getInstance(this).getCarDataManager();
        mNavigationManager = PartnerLibrary.getInstance(this).getNavigationManager();
        mCarDataManager.registerMileageListener(CarDataActivity.this);
        mCarDataManager.registerTurnSignalListener(CarDataActivity.this);
        mCarDataManager.registerFogLightStateListener(CarDataActivity.this);
        mCarDataManager.registerSteeringAngleListener(CarDataActivity.this);
    }

    private void initializeViews() {
        mCarDataGetListSpinner = (Spinner) findViewById(R.id.spinner_car_data_api_calls);
        mResultTextView = (TextView)findViewById(R.id.text_result);
        mTextViewListenerUpdateMileage = (TextView) findViewById(R.id.text_listener_update_mileage);
        mTextViewListenerUpdateFogLights = (TextView) findViewById(R.id.text_listener_update_fog_lights);
        mTextViewListenerUpdateSteeringAngle = (TextView) findViewById(R.id.text_listener_update_steering_angle);
        mTextViewListenerUpdateTurnSignalIndicator = (TextView) findViewById(R.id.text_listener_update_turn_signal_indicator);

        mCarDataGetListSpinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mCarDataAPIMethods);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarDataGetListSpinner.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mCarDataAPIMethods.length || position < 0)
            return;

        Log.d(TAG, "Item selected: " + mCarDataAPIMethods[position]);
        try {
            switch (position) {
                case 0: // getCurrentMileage
                    mResultTextView.setText("Current Mileage: " + mCarDataManager.getCurrentMileage());
                    break;
                case 1: // getTurnSignalIndicator
                    mResultTextView.setText("Turn signal indicator: " + mCarDataManager.getTurnSignalIndicator());
                    break;
                case 2: // getFogLightsState
                    mResultTextView.setText("Fog Lights state: " + mCarDataManager.getFogLightsState());
                    break;
                case 3:
                    mResultTextView.setText("Steering Angle: " + mCarDataManager.getSteeringAngle());
                    break;
                case 4:
                    mResultTextView.setText("Navigation Application State: " + mNavigationManager.isNavStarted());
                    break;
                case 5:
                    mResultTextView.setText("Current Route: " + mNavigationManager.getActiveRoute());
                    break;
                default:
                    Toast.makeText(CarDataActivity.this, "Cannot process, please select again", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(CarDataActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMileageValueChanged(float i) {
        float val = mCarDataManager.getCurrentMileage();

        Log.d(TAG,"Current Mileage Value: " + val);
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateMileage.setText("Odomometer value: " + val);
            }
        }));
    }


    @Override
    public void onFogLightsChanged(VehicleLightState vehicleLightState) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateFogLights.setText("Fog lights value: " + vehicleLightState);
            }
        }));
    }

    @Override
    public void onSteeringAngleChanged(float v) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateSteeringAngle.setText("Steering angle value: " + v);
            }
        }));
    }

    @Override
    public void onTurnSignalStateChanged(VehicleSignalIndicator vehicleSignalIndicator) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateTurnSignalIndicator.setText("Turn Signal Indicator value: " + vehicleSignalIndicator);
            }
        }));
    }
}
