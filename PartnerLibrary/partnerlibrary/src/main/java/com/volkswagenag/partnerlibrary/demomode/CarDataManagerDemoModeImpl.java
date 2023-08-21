package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.util.Log;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.MileageListener;
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

    private static final String TAG = PartnerLibraryDemoModeImpl.class.getSimpleName();
    private static final String CAR_DATA_FILE_NAME = "car_data.json";

    private final Context mContext;
    // listeners
    private final HashSet<MileageListener> mMileageListeners = new HashSet<MileageListener>();
    private final HashSet<TurnSignalListener> mTurnSignalListener = new HashSet<TurnSignalListener>();
    private final HashSet<FogLightStateListener> mFogLightStateListener = new HashSet<FogLightStateListener>();
    private final HashSet<SteeringAngleListener> mSteeringAngleListener = new HashSet<SteeringAngleListener>();
    private final ScheduledExecutorService mSchedulerService;
    //cache
    private AtomicInteger mIndex = new AtomicInteger(0);
    private int mMaxValueOfIndex;
    private int mChangeFrequency;
    private List<Integer> mMileageList;
    private List<VehicleSignalIndicator> mTurnSignalIndicatorList;
    private List<VehicleLightState> mFogLightsStateList;
    private List<Integer> mSteeringAngleList;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateIndexAndUpdateListenersIfNeeded();
        }
    };
    private String mVehicleIdentityNumber;
    private ScheduledFuture<?> mChangeValuesAtFixedRateFuture;

    public CarDataManagerDemoModeImpl(Context context) {
        mContext = context;
        mSchedulerService = Executors.newScheduledThreadPool(1);

        try {
            initializeCache();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logCache();
    }

    /**
     * Starts freqeuncy scheduler runnable, that runs every {@link mChangeFrequency} seconds and
     * updates values and triggers listeners if necessary.
     */
    public void startScheduler() {
        mIndex.set(0);
        mChangeValuesAtFixedRateFuture =
                mSchedulerService.scheduleAtFixedRate(runnable, mChangeFrequency, mChangeFrequency, TimeUnit.SECONDS);
    }

    /**
     * Stops the future that runs every {@link mChangeFrequency} seconds to update values.
     */
    public void stopScheduler() {
        mChangeValuesAtFixedRateFuture.cancel(true);
    }

    @Override
    public void registerMileageListener(MileageListener mileageListener) {
        mMileageListeners.add(mileageListener);
    }

    @Override
    public void unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
    }

    @Override
    public float getCurrentMileage() {
        return mMileageList.get(mIndex.get() % mMileageList.size());
    }

    @Override
    public void registerTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.add(turnSignalListener);
    }

    @Override
    public void unregisterTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.remove(turnSignalListener);
    }

    @Override
    public VehicleSignalIndicator getTurnSignalIndicator() {
        return mTurnSignalIndicatorList.get(mIndex.get() % mTurnSignalIndicatorList.size());
    }

    @Override
    public void registerFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.add(lightStateListener);
    }

    @Override
    public void unregisterFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.remove(lightStateListener);
    }

    @Override
    public VehicleLightState getFogLightsState() {
        return mFogLightsStateList.get(mIndex.get() % mFogLightsStateList.size());
    }

    @Override
    public void registerSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.add(steeringAngleListener);
    }

    @Override
    public void unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.remove(steeringAngleListener);
    }

    @Override
    public float getSteeringAngle() {
        return mSteeringAngleList.get(mIndex.get() % mSteeringAngleList.size());
    }

    @Override
    public String getVehicleIdentityNumber() {
        return mVehicleIdentityNumber;
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

        mChangeFrequency = carDataJSON.getInt("change_frequency_secs");

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
                + "Change Frequency: " + mChangeFrequency + "\n"
                + "Mileage List: " + mMileageList + "\n"
                + "TurnSignalIndicator List: " + mTurnSignalIndicatorList + "\n"
                + "FogLightsState List: " + mFogLightsStateList + "\n"
                + "SteeringAngle List: " + mSteeringAngleList + "\n"
                + "VIN: " + mVehicleIdentityNumber + "\n");
    }
}
