package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.util.Log;

import com.volkswagenag.partnerlibrary.ActiveRouteUpdateListener;
import com.volkswagenag.partnerlibrary.NavStateListener;
import com.volkswagenag.partnerlibrary.NavigationManager;

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

public class NavigationManagerDemoModeImpl implements NavigationManager {
    private static final String TAG = NavigationManagerDemoModeImpl.class.getSimpleName();
    private static final String NAVIGATION_DATA_FILE_NAME = "navigation_data.json";

    private final Context mContext;
    private final HashSet<ActiveRouteUpdateListener> mActiveRouteUpdateListeners = new HashSet<>();
    private final HashSet<NavStateListener> mNavigationStateListeners = new HashSet<>();
    private final ScheduledExecutorService mSchedulerService;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateIndexAndUpdateListenersIfNeeded();
        }
    };

    private ScheduledFuture<?> mChangeValuesAtFixedRateFuture;

    // cache
    private AtomicInteger mIndex = new AtomicInteger(0);
    private int mChangeFrequencySecs;
    private int mMaxValueOfIndex;
    private List<Boolean> mIsNavStartedList;
    private List<String> mActiveRoutesList;

    public NavigationManagerDemoModeImpl(Context context) {
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
    public void registerNavStateListener(NavStateListener listener) {
        mNavigationStateListeners.add(listener);
    }

    @Override
    public void unregisterNavStateListener(NavStateListener listener) {
        mNavigationStateListeners.remove(listener);
    }

    @Override
    public boolean isNavStarted() {
        return mIsNavStartedList.get(mIndex.get() % mIsNavStartedList.size());
    }

    @Override
    public void registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        mActiveRouteUpdateListeners.add(activeRouteUpdateListener);
    }

    @Override
    public void unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        mActiveRouteUpdateListeners.remove(activeRouteUpdateListener);
    }

    @Override
    public String getActiveRoute() {
        return mActiveRoutesList.get(mIndex.get() % mActiveRoutesList.size());
    }

    private void updateIndexAndUpdateListenersIfNeeded() {
        int previous = mIndex.get();
        int next = (mIndex.get() + 1) % mMaxValueOfIndex;
        mIndex.set(next);

        // send active route update only if nav started for that index is true
        if (mActiveRoutesList.get(previous % mActiveRoutesList.size())
                != mActiveRoutesList.get(next % mActiveRoutesList.size()) &&
                !mActiveRoutesList.get(next % mActiveRoutesList.size()).isEmpty() &&
                mIsNavStartedList.get(next % mIsNavStartedList.size())) {
            for (ActiveRouteUpdateListener activeRouteUpdateListener : mActiveRouteUpdateListeners) {
                activeRouteUpdateListener.onActiveRouteChange(mActiveRoutesList.get(next % mActiveRoutesList.size()));
            }
        }

        if (mIsNavStartedList.get(previous % mIsNavStartedList.size())
               != mIsNavStartedList.get(next % mIsNavStartedList.size())) {
            for (NavStateListener navigationListener : mNavigationStateListeners) {
                navigationListener.onNavStateChanged(mIsNavStartedList.get(next % mIsNavStartedList.size()));
            }
        }
    }

    private void initializeCache() throws JSONException, IOException {
        JSONObject navDataJSON = DemoModeUtils.readFromFile(mContext, NAVIGATION_DATA_FILE_NAME);

        mChangeFrequencySecs = navDataJSON.getInt("change_frequency_secs");
        mIsNavStartedList = DemoModeUtils.getConvertedList(
                navDataJSON.getJSONArray("nav_started"),
                strValue -> Boolean.parseBoolean(strValue));
        mMaxValueOfIndex = mIsNavStartedList.size();

        mActiveRoutesList = DemoModeUtils.getConvertedList(
                navDataJSON.getJSONArray("active_route"),
                strValue -> strValue);
        mMaxValueOfIndex = Integer.max(mMaxValueOfIndex, mActiveRoutesList.size());
    }

    private void logCache() {
        Log.d(TAG, "Index: " + mIndex.get() + " \n"
                + "MaxValueOfIndex: " + mActiveRoutesList.size() + "\n"
                + "Change Frequency: " + mChangeFrequencySecs + "\n"
                + "Nav started status: " + mIsNavStartedList + "\n"
                + "Active routes: " + mActiveRoutesList);
    }
}
