package com.cariad.technology.sampleapp;

import android.content.Context;

import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.CarDataManager;

/**
 * Common place for all activities to get the PartnerLibrary Managers.
 * This is a Singleton and is only here for distributing PartnerLibrary Managers - like an acting
 * ViewModel for all the activities in this sample application.
 */
class PartnerLibraryManager {
    private static final String TAG = PartnerLibraryManager.class.getSimpleName();
    private static volatile PartnerLibraryManager partnerLibraryManagerInstance = null;

    private PartnerLibrary mPartnerLibrary;

    private PartnerLibraryManager(Context context) {
        mPartnerLibrary = new PartnerLibrary(context);
    }

    public static PartnerLibraryManager getInstance(Context context) {
        synchronized (PartnerLibraryManager.class) {
            if (partnerLibraryManagerInstance == null) {
                partnerLibraryManagerInstance = new PartnerLibraryManager(context);
            }
        }
        return partnerLibraryManagerInstance;
    }

    public PartnerLibrary getPartnerLibrary() {
        return mPartnerLibrary;
    }

    public CarDataManager getCarDataManager() {
        return mPartnerLibrary.getCarDataManager();
    }



}
