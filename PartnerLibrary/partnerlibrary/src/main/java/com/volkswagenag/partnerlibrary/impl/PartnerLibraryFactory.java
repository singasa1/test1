/*
 * CONFIDENTIAL CARIAD Estonia AS
 *
 * (c) 2023 CARIAD Estonia AS, All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of CARIAD Estonia AS (registry code 14945253).
 * The intellectual and technical concepts contained herein are proprietary to CARIAD Estonia AS. and may be covered by
 * patents, patents in process, and are protected by trade secret or copyright law.
 * Usage or dissemination of this information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from CARIAD Estonia AS.
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret of CARIAD Estonia AS.
 * Any reproduction, modification, distribution, public performance, or public display of or through use of this source
 * code without the prior written consent of CARIAD Estonia AS is strictly prohibited and in violation of applicable
 * laws and international treaties. The receipt or possession of this source code and/ or related information does not
 * convey or imply any rights to reproduce, disclose or distribute its contents or to manufacture, use or sell anything
 * that it may describe in whole or in part.
 */
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
