// IPartnerEnabler.aidl
package technology.cariad.partnerenablerservice;

// Declare any non-default types here with import statements
import technology.cariad.partnerenablerservice.ICarDataChangeListener;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

interface IPartnerEnabler {

    const int VEHICLE_SIGNAL_INDICATOR_NONE = 0;
    const int VEHICLE_SIGNAL_INDICATOR_RIGHT = 1;
    const int VEHICLE_SIGNAL_INDICATOR_LEFT = 2;

    const int VEHICLE_LIGHT_STATE_OFF = 0;
    const int VEHICLE_LIGHT_STATE_ON = 1;
    const int VEHICLE_LIGHT_STATE_DAYTIME_RUNNING = 2;

    /**
     * This method initializes the required components in the PartnerEnablerService.
    */
    void initialize() = 0;

    /**
     * This method returns the PartnerVerifier Service Connection Binder Handler
    */
    ISignatureVerifier getPartnerVerifierService() = 1;

    /**
     * This method releases/destroy the components created in the PartnerEnablerService.
    */
    void release() = 2;

    /**
     * This method returns the Car current Odometer value. continuous change prop type
    */
    int getCurrentMileage() = 3;

    /**
     * This method returns the Car current signal indicator value. on_change prop type
    */
    int getTurnSignalIndicator() = 4;

    /**
     * This method returns the Car fog light state. on_change prop type
    */
    int getFogLightsState() = 5;

    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left. continuous change prop type.
    */
    int getSteeringAngle() = 6;

    /**
     * This method returns the Car VIN Number.
    */
    String getVehicleIdentityNumber() = 7;

    /**
     * Registers a listener to be called when the car data(Steering Angle, FogLights, TurnSignalState, Odometer) has changed.
    */
    void addCarDataChangeListener(in ICarDataChangeListener listener) = 8;

    /**
    * Removes the provided listener from receiving the callbacks.
    */
    void removeCarDataChangeListener(in ICarDataChangeListener listener) = 9;
}