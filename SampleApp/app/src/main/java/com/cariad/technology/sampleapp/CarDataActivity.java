package com.cariad.technology.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import technology.cariad.partnerlibrary.CarDataManager;
import technology.cariad.partnerlibrary.MileageListener;

public class CarDataActivity extends AppCompatActivity implements MileageListener, AdapterView.OnItemSelectedListener {

    private final String TAG = CarDataActivity.this.getClass().getSimpleName();

    private Spinner mCarDataGetListSpinner;
    private TextView mResultTextView;
    private TextView mListenerUpdateTextView;

    private CarDataManager mCarDataManager;

    private int mSelectedPosition;

    // NOTE: Donot change the order - only add new ones at the end.
    // {@link CarDataActivity#onItemSelected} should be changed if the order in this array is changed.
    private String[] mCarDataAPIMethods = {
            "getCurrentMileage",
            "getTurnSignalIndicator",
            "getFogLightsState",
            "getSteeringAngle",
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
        mCarDataManager = PartnerLibraryManager.getInstance(this).getCarDataManager();
        mCarDataManager.registerMileageListener(CarDataActivity.this);
    }

    private void initializeViews() {
        mCarDataGetListSpinner = (Spinner) findViewById(R.id.spinner_car_data_api_calls);
        mResultTextView = (TextView)findViewById(R.id.text_result);
        mListenerUpdateTextView = (TextView) findViewById(R.id.text_listener_update);

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
        mListenerUpdateTextView.setText("Odomometer value: " + val);
    }


}