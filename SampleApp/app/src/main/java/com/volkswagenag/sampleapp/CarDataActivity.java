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

import java.text.SimpleDateFormat;
import java.util.Date;

public class CarDataActivity extends AppCompatActivity implements MileageListener, FogLightStateListener, SteeringAngleListener, TurnSignalListener {

    private final String TAG = CarDataActivity.this.getClass().getSimpleName();
    private TextView mTextViewListenerUpdateMileage;
    private TextView mTextViewListenerUpdateFogLights;
    private TextView mTextViewListenerUpdateSteeringAngle;
    private TextView mTextViewListenerUpdateTurnSignalIndicator;
    private TextView mTextViewVIN;

    private CarDataManager mCarDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_data);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
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
            logAndShowError("registerTurnSignalListener failed with ", status);
        }

        status = mCarDataManager.registerFogLightStateListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerFogLightListener failed with ", status);
        }

        status = mCarDataManager.registerSteeringAngleListener(CarDataActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerSteeringAngleListener failed with ", status);
        }

        String currentDate = getCurrentDate();
        Response<Float> floatResponse = mCarDataManager.getCurrentMileage();
        if (floatResponse.status == Response.Status.SUCCESS) {
            mTextViewListenerUpdateMileage.setText(floatResponse.value + "   (" + currentDate + ")");
        } else {
            logAndShowError("getCurrentMileage call failed with: ", floatResponse.status);
        }

        Response<VehicleSignalIndicator> vehicleSignalIndicatorResponse = mCarDataManager.getTurnSignalIndicator();
        if (vehicleSignalIndicatorResponse.status == Response.Status.SUCCESS) {
            mTextViewListenerUpdateTurnSignalIndicator.setText(vehicleSignalIndicatorResponse.value + "   (" + currentDate + ")");
        } else {
            logAndShowError("getTurnSignalIndicator call failed with: ", vehicleSignalIndicatorResponse.status);
        }

        Response<VehicleLightState> vehicleLightStateResponse = mCarDataManager.getFogLightsState();
        if (vehicleLightStateResponse.status == Response.Status.SUCCESS) {
            mTextViewListenerUpdateFogLights.setText(vehicleLightStateResponse.value + "   (" + currentDate + ")");
        } else {
            logAndShowError("getFogLightsState call failed with: ", vehicleLightStateResponse.status);
        }

        floatResponse = mCarDataManager.getSteeringAngle();
        if (floatResponse.status == Response.Status.SUCCESS) {
            mTextViewListenerUpdateSteeringAngle.setText(floatResponse.value + "   (" + currentDate + ")");
        } else {
            logAndShowError("getSteeringAngle call failed with: ", floatResponse.status);
        }

        Response<String> stringResponse = mCarDataManager.getVehicleIdentityNumber();
        if (stringResponse.status == Response.Status.SUCCESS) {
            mTextViewVIN.setText("" + stringResponse.value);
        } else {
            logAndShowError("getVehicleIdentityNumber call failed with: ", stringResponse.status);
        }
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date()); // Find todays date
        Log.e(TAG, "Richa: " + currentDateTime);
        return currentDateTime;
    }
    private void initializeViews() {
        mTextViewListenerUpdateMileage = (TextView) findViewById(R.id.text_listener_update_mileage);
        mTextViewListenerUpdateFogLights = (TextView) findViewById(R.id.text_listener_update_fog_lights);
        mTextViewListenerUpdateSteeringAngle = (TextView) findViewById(R.id.text_listener_update_steering_angle);
        mTextViewListenerUpdateTurnSignalIndicator = (TextView) findViewById(R.id.text_listener_update_turn_signal_indicator);
        mTextViewVIN = (TextView) findViewById(R.id.text_vehicle_identification_number);
    }
    @Override
    public void onMileageValueChanged(float i) {
        Log.d(TAG,"Current Mileage Value: " + i);
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateMileage.setText(i + "   (" + getCurrentDate() + ")");
            }
        }));
    }

    @Override
    public void onFogLightsChanged(VehicleLightState vehicleLightState) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateFogLights.setText(vehicleLightState + "   (" + getCurrentDate() + ")");
            }
        }));
    }

    @Override
    public void onSteeringAngleChanged(float v) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateSteeringAngle.setText(v + "   (" + getCurrentDate() + ")");
            }
        }));
    }

    @Override
    public void onTurnSignalStateChanged(VehicleSignalIndicator vehicleSignalIndicator) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateTurnSignalIndicator.setText(vehicleSignalIndicator + "   (" + getCurrentDate() + ")");
            }
        }));
    }

    private void logAndShowError(String message, Response.Status status) {
        Log.e(TAG, message + status.toString());
        Toast.makeText(CarDataActivity.this, message + status.toString(), Toast.LENGTH_LONG).show();
    }
}
