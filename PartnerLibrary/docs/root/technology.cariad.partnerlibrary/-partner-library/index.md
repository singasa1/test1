//[root](../../../index.md)/[technology.cariad.partnerlibrary](../index.md)/[PartnerLibrary](index.md)

# PartnerLibrary

[JVM]\
public class [PartnerLibrary](index.md)

# Partner Library

 Partner Library provides wrapper apis for different app developers. It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.

#### Author

Sathya Singaravelu

## Constructors

| | |
|---|---|
| [PartnerLibrary](-partner-library.md) | [JVM]<br>public void[PartnerLibrary](-partner-library.md)(Contextcontext) |

## Functions

| Name | Summary |
|---|---|
| [addListener](add-listener.md) | [JVM]<br>public void[addListener](add-listener.md)(ILibStateChangeListenerlistener)<br>This method is to add the listener to get PartnerEnablerServiceConnection status. |
| [getCarDataManager](get-car-data-manager.md) | [JVM]<br>public CarDataManager[getCarDataManager](get-car-data-manager.md)() |
| [initialize](initialize.md) | [JVM]<br>public void[initialize](initialize.md)()<br>This method binds to the PartnerEnabler service. |
| [release](release.md) | [JVM]<br>public void[release](release.md)()<br>This method unbinds the PartnerEnabler service |
| [removeListener](remove-listener.md) | [JVM]<br>public void[removeListener](remove-listener.md)(ILibStateChangeListenerlistener)<br>This method is to remove the listener. |
| [start](start.md) | [JVM]<br>public void[start](start.md)()<br>This method initializes the PartnerEnabler service components |
| [stop](stop.md) | [JVM]<br>public void[stop](stop.md)()<br>This method uninitializes the PartnerEnabler service components |
| [verifyDigitalSignature](verify-digital-signature.md) | [JVM]<br>public boolean[verifyDigitalSignature](verify-digital-signature.md)([String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)packageName)<br>This method verifies the provided package signature matches with signed config provided by the SignatureGenerator tool. |
