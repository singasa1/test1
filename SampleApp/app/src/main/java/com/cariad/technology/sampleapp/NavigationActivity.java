package com.cariad.technology.sampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.ActiveRouteUpdateListener;
import com.volkswagenag.partnerlibrary.NavAppStateListener;
import com.volkswagenag.partnerlibrary.Response;

public class NavigationActivity extends AppCompatActivity implements ActiveRouteUpdateListener, NavAppStateListener, AdapterView.OnItemSelectedListener {

    private final String TAG = NavigationActivity.this.getClass().getSimpleName();

    private Spinner mNavigationGetListSpinner;
    private TextView mResultTextView;
    private TextView mTextViewListenerUpdateNavStarted;
    private TextView mTextViewListenerUpdateActiveRoute;

    private NavigationManager mNavigationManager;

    // NOTE: Donot change the order - only add new ones at the end.
    // {@link NavigationActivity#onItemSelected} should be changed if the order in this array is changed.
    private String[] mNavigationAPIMethods = {
            "isNavAppStarted",
            "getActiveRoute"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initializeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        Response<NavigationManager> navigationManagerResponse = PartnerLibrary.getInstance(this).getNavigationManager();
        if (navigationManagerResponse.status != Response.Status.SUCCESS) {
            logAndShowError("Error obtaining NavigationManager from PartnerLibrary: ", navigationManagerResponse.status);
            return;
        }
        mNavigationManager = navigationManagerResponse.value;

        Response.Status status = mNavigationManager.registerActiveRouteUpdateListener(NavigationActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerActiveRouteUpdateListener failed with ", status);
        }

        status =  mNavigationManager.registerNavStateListener(NavigationActivity.this);
        if (status != Response.Status.SUCCESS) {
            logAndShowError("registerNavStateListener failed with ", status);
        }
    }

    private void initializeViews() {
        mNavigationGetListSpinner = (Spinner) findViewById(R.id.spinner_navigation_api_calls);
        mResultTextView = (TextView)findViewById(R.id.text_result);
        mTextViewListenerUpdateNavStarted = (TextView) findViewById(R.id.text_listener_update_nav_started);
        mTextViewListenerUpdateActiveRoute = (TextView) findViewById(R.id.text_listener_update_active_route);

        mNavigationGetListSpinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mNavigationAPIMethods);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNavigationGetListSpinner.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mNavigationAPIMethods.length || position < 0)
            return;

        Log.d(TAG, "Item selected: " + mNavigationAPIMethods[position]);
        try {
            switch (position) {
                case 0:
                    Response<Boolean> booleanResponse = mNavigationManager.isNavAppStarted();
                    if (booleanResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Navigation Application State: " + booleanResponse.value);
                    } else {
                        logAndShowError("isNavAppStarted call failed with: ", booleanResponse.status);
                    }
                    break;
                case 1:
                    Response<String> stringResponse = mNavigationManager.getActiveRoute();
                    if (stringResponse.status == Response.Status.SUCCESS) {
                        mResultTextView.setText("Current Route: " + stringResponse.value);
                    } else {
                        logAndShowError("getActiveRoute call failed with: ", stringResponse.status);
                    }
                    break;
                default:
                    Toast.makeText(NavigationActivity.this, "Cannot process, please select again", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(NavigationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActiveRouteChange(String activeRoute) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateActiveRoute.setText("Active route value: " + activeRoute);
            }
        }));
    }


    @Override
    public void onNavAppStateChanged(boolean status) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                mTextViewListenerUpdateNavStarted.setText("Navigation app started: " + status);
            }
        }));
    }

    private void logAndShowError(String message, Response.Status status) {
            Log.e(TAG, message + status.toString());
            Toast.makeText(NavigationActivity.this, message + status.toString(), Toast.LENGTH_LONG).show();
    }
}
