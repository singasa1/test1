package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.util.Log;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.MileageListener;
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
    private final HashSet<MileageListener> mMileageListeners = new HashSet<MileageListener>();
    private final HashSet<TurnSignalListener> mTurnSignalListener = new HashSet<TurnSignalListener>();
    private final HashSet<FogLightStateListener> mFogLightStateListener = new HashSet<FogLightStateListener>();
    private final HashSet<SteeringAngleListener> mSteeringAngleListener = new HashSet<SteeringAngleListener>();
    private final ScheduledExecutorService mSchedulerService;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateIndexAndUpdateListenersIfNeeded();
        }
    };

    private ScheduledFuture<?> mChangeValuesAtFixedRateFuture;

    //cache
    private AtomicInteger mIndex = new AtomicInteger(0);
    private int mMaxValueOfIndex;
    private int mChangeFrequencySecs;
    private List<Integer> mMileageList;
    private List<VehicleSignalIndicator> mTurnSignalIndicatorList;
    private List<VehicleLightState> mFogLightsStateList;
    private List<Integer> mSteeringAngleList;
    private String mVehicleIdentityNumber;

    public CarDataManagerDemoModeImpl(Context context) throws JSONException, IOException{
        mContext = context;
        mSchedulerService = Executors.newScheduledThreadPool(1);
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
    public Response.Status registerMileageListener(MileageListener mileageListener) {
        mMileageListeners.add(mileageListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response<Float> getCurrentMileage() {
        return new Response<>(Response.Status.SUCCESS, mMileageList.get(mIndex.get() % mMileageList.size()).floatValue());
    }

    @Override
    public Response.Status registerTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.add(turnSignalListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.remove(turnSignalListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response<VehicleSignalIndicator> getTurnSignalIndicator() {
        return new Response<>(Response.Status.SUCCESS,
                mTurnSignalIndicatorList.get(mIndex.get() % mTurnSignalIndicatorList.size()));
    }

    @Override
    public Response.Status registerFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.add(lightStateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.remove(lightStateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response<VehicleLightState> getFogLightsState() {
        return new Response<>(Response.Status.SUCCESS,
                mFogLightsStateList.get(mIndex.get() % mFogLightsStateList.size()));
    }

    @Override
    public Response.Status registerSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.add(steeringAngleListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.remove(steeringAngleListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response<Float> getSteeringAngle() {
        return new Response<>(Response.Status.SUCCESS,
                mSteeringAngleList.get(mIndex.get() % mSteeringAngleList.size()).floatValue());
    }

    @Override
    public Response<String> getVehicleIdentityNumber() {
        return new Response<>(Response.Status.SUCCESS, mVehicleIdentityNumber);
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

        mMileageList = DemoModeUtils.getIntegerList(carDataJSON.getJSONArray("mileage_list"));
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
                DemoModeUtils.getIntegerList(carDataJSON.getJSONArray("steering_angle_list"));
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
