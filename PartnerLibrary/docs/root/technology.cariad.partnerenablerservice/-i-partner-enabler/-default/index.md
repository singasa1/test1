//[root](../../../../index.md)/[technology.cariad.partnerenablerservice](../../index.md)/[IPartnerEnabler](../index.md)/[Default](index.md)

# Default

[JVM]\
public class [Default](index.md)

Default implementation for IPartnerEnabler.

## Constructors

| | |
|---|---|
| [IPartnerEnabler.Default](-i-partner-enabler.-default.md) | [JVM]<br>public void[IPartnerEnabler.Default](-i-partner-enabler.-default.md)() |

## Functions

| Name | Summary |
|---|---|
| [addCarDataChangeListener](add-car-data-change-listener.md) | [JVM]<br>public void[addCarDataChangeListener](add-car-data-change-listener.md)(ICarDataChangeListenerlistener)<br>Registers a listener @link#ICarDataChangeListener to be called when the car data changes. |
| [asBinder](as-binder.md) | [JVM]<br>public IBinder[asBinder](as-binder.md)() |
| [getCurrentMileage](get-current-mileage.md) | [JVM]<br>public float[getCurrentMileage](get-current-mileage.md)()<br>This method returns the Car current Odometer value. |
| [getFogLightsState](get-fog-lights-state.md) | [JVM]<br>public int[getFogLightsState](get-fog-lights-state.md)()<br>This method returns the Car fog light state. |
| [getSteeringAngle](get-steering-angle.md) | [JVM]<br>public float[getSteeringAngle](get-steering-angle.md)()<br>This method returns the Car steering angle in degrees. |
| [getTurnSignalIndicator](get-turn-signal-indicator.md) | [JVM]<br>public int[getTurnSignalIndicator](get-turn-signal-indicator.md)()<br>This method returns the Car current signal indicator value. |
| [getVehicleIdentityNumber](get-vehicle-identity-number.md) | [JVM]<br>public [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)[getVehicleIdentityNumber](get-vehicle-identity-number.md)()<br>This method returns the Car VIN Number. |
| [initialize](initialize.md) | [JVM]<br>public void[initialize](initialize.md)()<br>This method initializes the required components in the PartnerEnablerService. |
| [release](release.md) | [JVM]<br>public void[release](release.md)()<br>This method releases/destroy the components created in the PartnerEnablerService. |
| [removeCarDataChangeListener](remove-car-data-change-listener.md) | [JVM]<br>public void[removeCarDataChangeListener](remove-car-data-change-listener.md)(ICarDataChangeListenerlistener)<br>Removes the provided listener from receiving the callbacks. |
