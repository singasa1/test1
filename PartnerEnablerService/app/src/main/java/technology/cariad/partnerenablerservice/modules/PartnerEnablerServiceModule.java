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
