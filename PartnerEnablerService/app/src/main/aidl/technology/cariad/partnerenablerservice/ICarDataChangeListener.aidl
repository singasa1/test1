// ICarDataChangeListener.aidl
package technology.cariad.partnerenablerservice;

// Declare any non-default types here with import statements

interface ICarDataChangeListener {
    /**
     * Callback to trigger once Partner Enabler service connection state changes
     */
    void onMileageValueChanged(float mileageValue) = 0;

    void onFogLightsChanged(int fogLightState) = 1;

    void onSteeringAngleChanged(float steeringAngle) = 2;

    void onTurnSignalStateChanged(int signalIndicator) = 3;
}