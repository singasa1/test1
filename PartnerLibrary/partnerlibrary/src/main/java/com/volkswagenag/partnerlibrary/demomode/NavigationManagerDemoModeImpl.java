package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.volkswagenag.partnerlibrary.ActiveRouteUpdateListener;
import com.volkswagenag.partnerlibrary.NavAppStateListener;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.Response;

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

public class NavigationManagerDemoModeImpl implements NavigationManager {
    private static final String TAG = NavigationManagerDemoModeImpl.class.getSimpleName();
    private static final String NAVIGATION_DATA_FILE_NAME = "navigation_data.json";

    private final Context mContext;
    private final HashSet<ActiveRouteUpdateListener> mActiveRouteUpdateListeners = new HashSet<>();
    private final HashSet<NavAppStateListener> mNavigationAppStateListeners = new HashSet<>();
    private final ScheduledExecutorService mSchedulerService;
    private final Set<String> mPermissionsRequested;

    // cache
    private final AtomicInteger mIndex = new AtomicInteger(0);
    private ScheduledFuture<?> mChangeValuesAtFixedRateFuture;
    private int mChangeFrequencySecs;
    private int mMaxValueOfIndex;
    private List<Boolean> mIsNavStartedList;
    private List<String> mActiveRoutesList;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateIndexAndUpdateListenersIfNeeded();
        }
    };

    public NavigationManagerDemoModeImpl(Context context, Set<String> permissionsRequested) throws JSONException, IOException {
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
                mSchedulerService.scheduleAtFixedRate(runnable, mChangeFrequencySecs,
                        mChangeFrequencySecs, TimeUnit.SECONDS);
    }

    /**
     * Stops the future that runs every {@link mChangeFrequencySecs} seconds to update values.
     */
    public void stopScheduler() {
        mChangeValuesAtFixedRateFuture.cancel(true);
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerNavStateListener(NavAppStateListener listener) {
        if (!mPermissionsRequested.contains(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mNavigationAppStateListeners.add(listener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterNavStateListener(NavAppStateListener listener) {
        mNavigationAppStateListeners.remove(listener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<Boolean> isNavAppStarted() {
        if (!mPermissionsRequested.contains(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(
                Response.Status.SUCCESS,
                new Boolean(mIsNavStartedList.get(mIndex.get() % mIsNavStartedList.size())));
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        if (!mPermissionsRequested.contains(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)) {
            return Response.Status.PERMISSION_DENIED;
        }
        mActiveRouteUpdateListeners.add(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        mActiveRouteUpdateListeners.remove(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<String> getActiveRoute() {
        if (!mPermissionsRequested.contains(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)) {
            return new Response(Response.Status.PERMISSION_DENIED);
        }
        return new Response(Response.Status.SUCCESS,
                mActiveRoutesList.get(mIndex.get() % mActiveRoutesList.size()));
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
            for (ActiveRouteUpdateListener activeRouteUpdateListener :
                    mActiveRouteUpdateListeners) {
                activeRouteUpdateListener.onActiveRouteChange(mActiveRoutesList.get(next % mActiveRoutesList.size()));
            }
        }

        if (mIsNavStartedList.get(previous % mIsNavStartedList.size())
                != mIsNavStartedList.get(next % mIsNavStartedList.size())) {
            for (NavAppStateListener navigationListener : mNavigationAppStateListeners) {
                navigationListener.onNavAppStateChanged(mIsNavStartedList.get(next % mIsNavStartedList.size()));
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
