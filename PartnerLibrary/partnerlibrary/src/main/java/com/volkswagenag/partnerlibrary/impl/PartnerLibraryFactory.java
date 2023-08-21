package com.volkswagenag.partnerlibrary.impl;

import android.content.Context;

import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.demomode.DemoModeUtils;
import com.volkswagenag.partnerlibrary.demomode.PartnerLibraryDemoModeImpl;

public class PartnerLibraryFactory {
    private static boolean ENABLE_DEMO_MODE_CODE = true;

    /**
     * Creates and returns appropriate {@link PartnerLibrary} instance to use.
     * @param context
     * @return
     */
    public static PartnerLibrary getPartnerLibraryInstance(Context context) {
        if (ENABLE_DEMO_MODE_CODE && DemoModeUtils.isDemoModeEnabled()) {
            return PartnerLibraryDemoModeImpl.getInstance(context);
        } else {
            return PartnerLibraryImpl.getInstance(context);
        }
    }
}
