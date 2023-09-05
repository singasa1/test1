package com.volkswagenag.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.volkswagenag.partnerlibrary.PartnerLibraryManager;
import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.Response;
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

    // NOTE: Donot change the order - only add new ones at the end.
    // {@link CarDataActivity#onItemSelected} should be changed if the order in this array is changed.
    private String[] mCarDataAPIMethods = {
            "getCurrentMileage",
            "getTurnSignalIndicator",
            "getFogLightsState",
            "getSteeringAngle",
            "getVehicleIdentityNumber"
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
        Response<CarDataManager> carDataManagerResponse = PartnerLibraryManager.getInstance(this).getCarDataManager();
        if (carDataManagerResponse.status != Response.Status.SUCCESS) {
            logAndShowError("Error obtaining NavigationManager from PartnerLibraryManager: ", carDataManagerResponse.status);
            return;
        }
        mCarDataManager = carDataManagerResponse.value;

        Response.Status status = mCarDataManager.registerMileageListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerMileageListener failed with ", status);
        }

        status = mCarDataManager.registerTurnSignalListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerMileageListener failed with ", status);
        }

        status = mCarDataManager.registerFogLightStateListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerMileageListener failed with ", status);
        }

        status = mCarDataManager.registerSteeringAngleListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerMileageListener failed with ", status);
        }
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
                    Response<Float> floatResponse = mCarDataManager.getCurrentMileage();
                    if (floatResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Current Mileage: " + floatResponse.value);
                    } else {
                        logAndShowError("getCurrentMileage call failed with: ", floatResponse.status);
                    }
                    break;
                case 1: // getTurnSignalIndicator
                    Response<VehicleSignalIndicator> vehicleSignalIndicatorResponse = mCarDataManager.getTurnSignalIndicator();
                    if (vehicleSignalIndicatorResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Turn signal indicator: " + vehicleSignalIndicatorResponse.value);
                    } else {
                        logAndShowError("getTurnSignalIndicator call failed with: ", vehicleSignalIndicatorResponse.status);
                    }
                    break;
                case 2: // getFogLightsState
                    Response<VehicleLightState> vehicleLightStateResponse = mCarDataManager.getFogLightsState();
                    if (vehicleLightStateResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Fog Lights state: " + vehicleLightStateResponse.value);
                    } else {
                        logAndShowError("getFogLightsState call failed with: ", vehicleLightStateResponse.status);
                    }
                    break;
                case 3: // getSteeringAngle
                    floatResponse = mCarDataManager.getSteeringAngle();
                    if (floatResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Steering Angle: " + floatResponse.value);
                    } else {
                        logAndShowError("getSteeringAngle call failed with: ", floatResponse.status);
                    }
                    break;
                case 4:
                    Response<String> stringResponse = mCarDataManager.getVehicleIdentityNumber();
                    if (stringResponse.status == Response.Status.SUCCESS) {
                         mResultTextView.setText("VIN number: " + stringResponse.value);
                    } else {
                        logAndShowError("getVehicleIdentityNumber call failed with: ", stringResponse.status);
                    }
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
        Log.d(TAG,"Current Mileage Value: " + i);
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateMileage.setText("Odomometer value: " + i);
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

    private void logAndShowError(String message, Response.Status status) {
        Log.e(TAG, message + status.toString());
        Toast.makeText(CarDataActivity.this, message + status.toString(), Toast.LENGTH_LONG).show();
    }
}
