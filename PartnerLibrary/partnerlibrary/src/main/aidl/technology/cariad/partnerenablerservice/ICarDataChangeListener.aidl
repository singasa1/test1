// ICarDataChangeListener.aidl
package technology.cariad.partnerenablerservice;

// Declare any non-default types here with import statements

interface ICarDataChangeListener {
    /**
     * Callback to trigger once Partner Enabler service connection state changes
     */
    void onMileageValueChanged(int mileageValue) = 0;

    void onFogLightsChanged(int fogLightState) = 1;

    void onSteeringAngleChanged(int steeringAngle) = 2;

    void onTurnSignalStateChanged(int signalIndicator) = 3;
}