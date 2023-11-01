//[root](../../../index.md)/[com.volkswagenag.partnerlibrary](../index.md)/[NavigationManager](index.md)

# NavigationManager

[JVM]\
public interface [NavigationManager](index.md)

# Navigation Manager

 Navigation Manager provides wrapper apis for navigation related data such as navigation app state and route. Note: initialize must be called, to bind to the PartnerEnablerService, before calling any methods in this interface.

#### Author

CARIAD Inc

## Functions

| Name | Summary |
|---|---|
| [getActiveRoute](get-active-route.md) | [JVM]<br>public abstract Response&lt;String&gt;[getActiveRoute](get-active-route.md)()<br>Returns the route guidance of the current active route from Navigation Application. |
| [isNavAppStarted](is-nav-app-started.md) | [JVM]<br>public abstract Response&lt;Boolean&gt;[isNavAppStarted](is-nav-app-started.md)()<br>Returns the Navigation Application state. |
| [registerActiveRouteUpdateListener](register-active-route-update-listener.md) | [JVM]<br>public abstract Status[registerActiveRouteUpdateListener](register-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener)<br>Add the ActiveRouteUpdateListener listener to get the active guided route from Navigation App. |
| [registerNavAppStateListener](register-nav-app-state-listener.md) | [JVM]<br>public abstract Status[registerNavAppStateListener](register-nav-app-state-listener.md)(NavAppStateListenerlistener)<br>Add the NavAppStateListener listener, which is called when Navigation Core App status changes. |
| [unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md) | [JVM]<br>public abstract Status[unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener)<br>Remove the registered ActiveRouteUpdateListener listener that is registered to get the active route from Navigation App. |
| [unregisterNavAppStateListener](unregister-nav-app-state-listener.md) | [JVM]<br>public abstract Status[unregisterNavAppStateListener](unregister-nav-app-state-listener.md)(NavAppStateListenerlistener)<br>Remove the registered NavAppStateListener listener. |
