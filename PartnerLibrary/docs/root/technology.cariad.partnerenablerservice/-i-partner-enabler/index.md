//[root](../../../index.md)/[technology.cariad.partnerenablerservice](../index.md)/[IPartnerEnabler](index.md)

# IPartnerEnabler

public interface [IPartnerEnabler](index.md)

Interface used to interact with the PartnerEnablerService. Mostly this is used by the PartnerLibrary class. {@hide}

#### Inheritors

| |
|---|
| [Stub](-stub/index.md) |
| [Default](-default/index.md) |

## Types

| Name | Summary |
|---|---|
| [Default](-default/index.md) | [JVM]<br>public class [Default](-default/index.md)<br>Default implementation for IPartnerEnabler. |
| [Stub](-stub/index.md) | [JVM]<br>public abstract class [Stub](-stub/index.md)<br>Local-side IPC implementation stub class. |

## Functions

| Name | Summary |
|---|---|
| [addCarDataChangeListener](add-car-data-change-listener.md) | [JVM]<br>public abstract void[addCarDataChangeListener](add-car-data-change-listener.md)(ICarDataChangeListenerlistener)<br>Registers a listener @link#ICarDataChangeListener to be called when the car data changes. |
| [getCurrentMileage](get-current-mileage.md) | [JVM]<br>public abstract float[getCurrentMileage](get-current-mileage.md)()<br>This method returns the Car current Odometer value. |
| [getFogLightsState](get-fog-lights-state.md) | [JVM]<br>public abstract int[getFogLightsState](get-fog-lights-state.md)()<br>This method returns the Car fog light state. |
| [getSteeringAngle](get-steering-angle.md) | [JVM]<br>public abstract float[getSteeringAngle](get-steering-angle.md)()<br>This method returns the Car steering angle in degrees. |
| [getTurnSignalIndicator](get-turn-signal-indicator.md) | [JVM]<br>public abstract int[getTurnSignalIndicator](get-turn-signal-indicator.md)()<br>This method returns the Car current signal indicator value. |
| [getVehicleIdentityNumber](get-vehicle-identity-number.md) | [JVM]<br>public abstract [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)[getVehicleIdentityNumber](get-vehicle-identity-number.md)()<br>This method returns the Car VIN Number. |
| [initialize](initialize.md) | [JVM]<br>public abstract void[initialize](initialize.md)()<br>This method initializes the required components in the PartnerEnablerService. |
| [release](release.md) | [JVM]<br>public abstract void[release](release.md)()<br>This method releases/destroy the components created in the PartnerEnablerService. |
| [removeCarDataChangeListener](remove-car-data-change-listener.md) | [JVM]<br>public abstract void[removeCarDataChangeListener](remove-car-data-change-listener.md)(ICarDataChangeListenerlistener)<br>Removes the provided listener from receiving the callbacks. |
