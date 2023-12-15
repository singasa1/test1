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
package technology.cariad.partnerenablerservice.modules;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import android.car.hardware.property.CarPropertyManager;
import android.car.Car;
import android.content.Context;
import android.util.Log;

/**
 * Hilt module initializes all static variable needed to be injected to different component classes.
 */
@Module
@InstallIn(SingletonComponent.class)
public class PartnerEnablerServiceModule {
    static CarPropertyManager mCarPropertyManager;
    @Provides
    public static CarPropertyManager provideCarPropertyManager(@ApplicationContext Context context) {
        if (mCarPropertyManager == null) {
            mCarPropertyManager =
                    (CarPropertyManager) Car.createCar(context).getCarManager(Car.PROPERTY_SERVICE);
            if (mCarPropertyManager == null) {
                Log.e("PartnerEnablerServiceModule", "Failed to get CarPropertyManager");
            }
        }
        return mCarPropertyManager;
    }
}
