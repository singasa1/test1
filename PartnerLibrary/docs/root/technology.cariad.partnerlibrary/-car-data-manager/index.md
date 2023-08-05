//[root](../../../index.md)/[technology.cariad.partnerlibrary](../index.md)/[CarDataManager](index.md)

# CarDataManager

[JVM]\
public class [CarDataManager](index.md)

# Partner Library

 Partner Library provides wrapper apis for different app developers. It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.

#### Author

Sathya Singaravelu

## Constructors

| | |
|---|---|
| [CarDataManager](-car-data-manager.md) | [JVM]<br>public void[CarDataManager](-car-data-manager.md)(IPartnerEnablerservice) |

## Functions

| Name | Summary |
|---|---|
| [getCurrentMileage](get-current-mileage.md) | [JVM]<br>public float[getCurrentMileage](get-current-mileage.md)()<br>This method gets the Car current Odometer value from PartnerEnablerService |
| [getFogLightsState](get-fog-lights-state.md) | [JVM]<br>public VehicleLightState[getFogLightsState](get-fog-lights-state.md)()<br>This method gets the current fog lights state from PartnerEnablerService |
| [getSteeringAngle](get-steering-angle.md) | [JVM]<br>public float[getSteeringAngle](get-steering-angle.md)()<br>This method returns the Car steering angle in degrees. |
| [getTurnSignalIndicator](get-turn-signal-indicator.md) | [JVM]<br>public VehicleSignalIndicator[getTurnSignalIndicator](get-turn-signal-indicator.md)()<br>This method gets the current turn signal indicator value from PartnerEnablerService |
| [getVehicleIdentityNumber](get-vehicle-identity-number.md) | [JVM]<br>public [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)[getVehicleIdentityNumber](get-vehicle-identity-number.md)()<br>This method returns the Car VIN Number. |
| [registerFogLightStateListener](register-fog-light-state-listener.md) | [JVM]<br>public void[registerFogLightStateListener](register-fog-light-state-listener.md)(FogLightStateListenerlightStateListener)<br>This method is to add the listener to get Fog Lights State value. |
| [registerMileageListener](register-mileage-listener.md) | [JVM]<br>public void[registerMileageListener](register-mileage-listener.md)(MileageListenermileageListerer)<br>This method is to add the listener to get Odometer/Mileage value. |
| [registerSteeringAngleListener](register-steering-angle-listener.md) | [JVM]<br>public void[registerSteeringAngleListener](register-steering-angle-listener.md)(SteeringAngleListenersteeringAngleListener)<br>This method is to add the listener to get steering angle value. |
| [registerTurnSignalListener](register-turn-signal-listener.md) | [JVM]<br>public void[registerTurnSignalListener](register-turn-signal-listener.md)(TurnSignalListenerturnSignalListener)<br>This method is to add the listener to get Vehicle Turn Signal State value. |
| [unregisterFogLightStateListener](unregister-fog-light-state-listener.md) | [JVM]<br>public void[unregisterFogLightStateListener](unregister-fog-light-state-listener.md)(FogLightStateListenerlightStateListener)<br>This method is to remove the listener. |
| [unregisterMileageListener](unregister-mileage-listener.md) | [JVM]<br>public void[unregisterMileageListener](unregister-mileage-listener.md)(MileageListenermileageListener)<br>This method is to remove the listener. |
| [unregisterSteeringAngleListener](unregister-steering-angle-listener.md) | [JVM]<br>public void[unregisterSteeringAngleListener](unregister-steering-angle-listener.md)(SteeringAngleListenersteeringAngleListener)<br>This method is to remove the listener. |
| [unregisterTurnSignalListener](unregister-turn-signal-listener.md) | [JVM]<br>public void[unregisterTurnSignalListener](unregister-turn-signal-listener.md)(TurnSignalListenerturnSignalListener)<br>This method is to remove the listener. |
