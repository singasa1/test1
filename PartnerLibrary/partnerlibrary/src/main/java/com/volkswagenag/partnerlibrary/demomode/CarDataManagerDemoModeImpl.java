package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.MileageListener;
import com.volkswagenag.partnerlibrary.PartnerLibraryManager;
import com.volkswagenag.partnerlibrary.Response;
import com.volkswagenag.partnerlibrary.SteeringAngleListener;
import com.volkswagenag.partnerlibrary.TurnSignalListener;
import com.volkswagenag.partnerlibrary.VehicleLightState;
import com.volkswagenag.partnerlibrary.VehicleSignalIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CarDataManagerDemoModeImpl implements CarDataManager {

    private static final String TAG = CarDataManagerDemoModeImpl.class.getSimpleName();
    private static final String CAR_DATA_FILE_NAME = "car_data.json";

    private final Context mContext;
    // listeners
    private final HashSet<MileageListener> mMileageListeners = new HashSet<>();
    private final HashSet<TurnSignalListener> mTurnSignalListener = new HashSet<>();
    private final HashSet<FogLightStateListener> mFogLightStateListener = new HashSet<>();
    private final HashSet<SteeringAngleListener> mSteeringAngleListener = new HashSet<>();
    private final ScheduledExecutorService mSchedulerService;
    private final Set<String> mPermissionsRequested;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateIndexAndUpdateListenersIfNeeded();
        }
    };
    private ScheduledFuture<?> mChangeValuesAtFixedRateFuture;

    //cache
    private final AtomicInteger mIndex = new AtomicInteger(0);
    private int mMaxValueOfIndex;
    private int mChangeFrequencySecs;
    private List<Float> mMileageList;
    private List<VehicleSignalIndicator> mTurnSignalIndicatorList;
    private List<VehicleLightState> mFogLightsStateList;
    private List<Float> mSteeringAngleList;
    private String mVehicleIdentityNumber;

    public CarDataManagerDemoModeImpl(Context context, Set<String> permissionsRequested) throws JSONException, IOException{
        mContext = context;
        mSchedulerService = Executors.newScheduledThreadPool(1);
        mPermissionsRequested = permissionsRequested;
        initializeCache();
        logCache();
    }

    /**
     * Starts freqeuncy scheduler runnable, that runs every {@link mChangeFrequencySecs} seconds and
     * updates values and triggers listeners if necessary.
     */
    public void startScheduler() {
        mIndex.set(0);
        mChangeValuesAtFixedRateFuture =
                mSchedulerService.scheduleAtFixedRate(runnable, mChangeFrequencySecs, mChangeFrequencySecs, TimeUnit.SECONDS);
    }

    /**
     * Stops the future that runs every {@link mChangeFrequencySecs} seconds to update values.
     */
    public void stopScheduler() {
        mChangeValuesAtFixedRateFuture.cancel(true);
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    public Response.Status registerMileageListener(MileageListener mileageListener) {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mMileageListeners.add(mileageListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    public Response<Float> getCurrentMileage() {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS, mMileageList.get(mIndex.get() % mMileageList.size()).floatValue());
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    public Response.Status registerTurnSignalListener(TurnSignalListener turnSignalListener) {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mTurnSignalListener.add(turnSignalListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.remove(turnSignalListener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    public Response<VehicleSignalIndicator> getTurnSignalIndicator() {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS,
                mTurnSignalIndicatorList.get(mIndex.get() % mTurnSignalIndicatorList.size()));
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)
    public Response.Status registerFogLightStateListener(FogLightStateListener lightStateListener) {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mFogLightStateListener.add(lightStateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.remove(lightStateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)
    public Response<VehicleLightState> getFogLightsState() {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS,
                mFogLightsStateList.get(mIndex.get() % mFogLightsStateList.size()));
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    public Response.Status registerSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mSteeringAngleListener.add(steeringAngleListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.remove(steeringAngleListener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    public Response<Float> getSteeringAngle() {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS,
                mSteeringAngleList.get(mIndex.get() % mSteeringAngleList.size()).floatValue());
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_INFO_VIN)
    public Response<String> getVehicleIdentityNumber() {
        if (!mPermissionsRequested.contains(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_INFO_VIN)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS, mVehicleIdentityNumber);
    }

    /**
     * Update the index to the next one and trigger listeners if any of the values have changed.
     */
    private void updateIndexAndUpdateListenersIfNeeded() {
        int previous = mIndex.get();
        int next = (previous + 1) % mMaxValueOfIndex;
        mIndex.set(next);

        if (mMileageList.get(previous % mMileageList.size())
                != mMileageList.get(next % mMileageList.size())) {
            for (MileageListener mileageListener : mMileageListeners) {
                mileageListener.onMileageValueChanged(mMileageList.get(next % mMileageList.size()));
            }
        }

        if (mTurnSignalIndicatorList.get(previous % mTurnSignalIndicatorList.size())
                != mTurnSignalIndicatorList.get(next % mTurnSignalIndicatorList.size())) {
            for (TurnSignalListener turnSignalListener : mTurnSignalListener) {
                turnSignalListener.onTurnSignalStateChanged(
                        mTurnSignalIndicatorList.get(next % mTurnSignalIndicatorList.size()));
            }
        }


        if (mFogLightsStateList.get(previous % mFogLightsStateList.size())
                != mFogLightsStateList.get(next % mFogLightsStateList.size())) {
            for (FogLightStateListener fogLightStateListener : mFogLightStateListener) {
                fogLightStateListener.onFogLightsChanged(
                        mFogLightsStateList.get(next % mFogLightsStateList.size()));
            }
        }

        if (mSteeringAngleList.get(previous % mSteeringAngleList.size())
                != mSteeringAngleList.get(next % mSteeringAngleList.size())) {
            for (SteeringAngleListener steeringAngleListener : mSteeringAngleListener) {
                steeringAngleListener.onSteeringAngleChanged(
                        mSteeringAngleList.get(next % mSteeringAngleList.size()));
            }
        }
    }


    private void initializeCache() throws JSONException, IOException {
        JSONObject carDataJSON = DemoModeUtils.readFromFile(mContext, CAR_DATA_FILE_NAME);

        mChangeFrequencySecs = carDataJSON.getInt("change_frequency_secs");

        mMileageList = DemoModeUtils.getFloatList(carDataJSON.getJSONArray("mileage_list"));
        mMaxValueOfIndex = mMileageList.size();

        mTurnSignalIndicatorList = DemoModeUtils.getConvertedList(
                carDataJSON.getJSONArray("turn_signal_indicator_list"),
                strValue -> VehicleSignalIndicator.valueOf(strValue));
        mMaxValueOfIndex = Integer.max(mMaxValueOfIndex, mTurnSignalIndicatorList.size());

        mFogLightsStateList = DemoModeUtils.getConvertedList(
                carDataJSON.getJSONArray("fog_lights_state_list"),
                strValue -> VehicleLightState.valueOf(strValue));
        mMaxValueOfIndex = Integer.max(mMaxValueOfIndex, mFogLightsStateList.size());

        mSteeringAngleList =
                DemoModeUtils.getFloatList(carDataJSON.getJSONArray("steering_angle_list"));
        mMaxValueOfIndex = Integer.max(mMaxValueOfIndex, mSteeringAngleList.size());

        mVehicleIdentityNumber = carDataJSON.getString("vehicle_identity_number");
    }

    private void logCache() {
        Log.d(TAG, "Index: " + mIndex.get() + " \n"
                + "MaxValueOfIndex: " + mMaxValueOfIndex + "\n"
                + "Change Frequency: " + mChangeFrequencySecs + "\n"
                + "Mileage List: " + mMileageList + "\n"
                + "TurnSignalIndicator List: " + mTurnSignalIndicatorList + "\n"
                + "FogLightsState List: " + mFogLightsStateList + "\n"
                + "SteeringAngle List: " + mSteeringAngleList + "\n"
                + "VIN: " + mVehicleIdentityNumber + "\n");
    }
}
