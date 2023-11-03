//[root](../../../index.md)/[com.volkswagenag.partnerlibrary](../index.md)/[PartnerLibraryManager](index.md)

# PartnerLibraryManager

[JVM]\
public interface [PartnerLibraryManager](index.md)

# PartnerLibraryManager

 PartnerLibraryManager provides APIs to initialize, get and use Partner APIs. Use this interface to maintain the connection with PartnerEnabler Service and to get other Partner API instances.

#### Author

CARIAD Inc

## Functions

| Name | Summary |
|---|---|
| [addListener](add-listener.md) | [JVM]<br>public abstract void[addListener](add-listener.md)(ILibStateChangeListenerlistener)<br>Add the ILibStateChangeListener which will be called when PartnerEnabler Service Connection status changes. |
| [getCarDataManager](get-car-data-manager.md) | [JVM]<br>public abstract Response&lt;CarDataManager&gt;[getCarDataManager](get-car-data-manager.md)()<br>Get CarDataManager instance to get car related data/information. |
| [getInstance](get-instance.md) | [JVM]<br>public static [PartnerLibraryManager](index.md)[getInstance](get-instance.md)(Contextcontext)<br>Returns the Singleton instance of [PartnerLibraryManager](index.md) to access Partner APIs |
| [getNavigationManager](get-navigation-manager.md) | [JVM]<br>public abstract Response&lt;NavigationManager&gt;[getNavigationManager](get-navigation-manager.md)()<br>Get NavigationManager instance to get current route. |
| [initialize](initialize.md) | [JVM]<br>public abstract Status[initialize](initialize.md)()<br>Binds to the PartnerEnabler Service. |
| [release](release.md) | [JVM]<br>public abstract Status[release](release.md)()<br>Un-binds from the PartnerEnabler service |
| [removeListener](remove-listener.md) | [JVM]<br>public abstract void[removeListener](remove-listener.md)(ILibStateChangeListenerlistener)<br>Remove the registered ILibStateChangeListener. |
| [start](start.md) | [JVM]<br>public abstract Status[start](start.md)()<br>Initializes the PartnerEnabler service components. |
| [stop](stop.md) | [JVM]<br>public abstract Status[stop](stop.md)()<br>Uninitializes the PartnerEnabler service components. |
