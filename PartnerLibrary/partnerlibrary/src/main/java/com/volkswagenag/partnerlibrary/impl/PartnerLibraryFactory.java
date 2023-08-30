package com.volkswagenag.partnerlibrary.impl;

import android.content.Context;

import com.volkswagenag.partnerlibrary.PartnerLibraryManager;
import com.volkswagenag.partnerlibrary.demomode.DemoModeUtils;
import com.volkswagenag.partnerlibrary.demomode.PartnerLibraryManagerDemoModeImpl;

public class PartnerLibraryFactory {
    private static boolean ENABLE_DEMO_MODE_CODE = true;

    /**
     * Creates and returns appropriate {@link PartnerLibraryManager} instance to use.
     * @param context
     * @return
     */
    public static PartnerLibraryManager getPartnerLibraryInstance(Context context) {
        if (ENABLE_DEMO_MODE_CODE && DemoModeUtils.isDemoModeEnabled()) {
            return PartnerLibraryManagerDemoModeImpl.getInstance(context);
        } else {
            return PartnerLibraryManagerImpl.getInstance(context);
        }
    }
}
