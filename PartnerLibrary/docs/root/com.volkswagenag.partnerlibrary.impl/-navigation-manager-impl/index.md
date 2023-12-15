//[root](../../../index.md)/[com.volkswagenag.partnerlibrary.impl](../index.md)/[NavigationManagerImpl](index.md)

# NavigationManagerImpl

[JVM]\
public class [NavigationManagerImpl](index.md)

# NavigationManagerImpl

 Navigation Manager Impl provides implementation for NavigationManager wrapper apis for navigation related data such as navigation app state and route. Note: initialize must be called, to bind to the PartnerEnablerService, before calling any methods in this interface.

#### Author

CARIAD Inc

## Constructors

| | |
|---|---|
| [NavigationManagerImpl](-navigation-manager-impl.md) | [JVM]<br>public void[NavigationManagerImpl](-navigation-manager-impl.md)(IPartnerEnablerservice) |

## Functions

| Name | Summary |
|---|---|
| [getActiveRoute](get-active-route.md) | [JVM]<br>public Response&lt;String&gt;[getActiveRoute](get-active-route.md)() |
| [isNavAppStarted](is-nav-app-started.md) | [JVM]<br>public Response&lt;Boolean&gt;[isNavAppStarted](is-nav-app-started.md)() |
| [registerActiveRouteUpdateListener](register-active-route-update-listener.md) | [JVM]<br>public Status[registerActiveRouteUpdateListener](register-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener) |
| [registerNavAppStateListener](register-nav-app-state-listener.md) | [JVM]<br>public Status[registerNavAppStateListener](register-nav-app-state-listener.md)(NavAppStateListenernavAppStateListener) |
| [unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md) | [JVM]<br>public Status[unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener) |
| [unregisterNavAppStateListener](unregister-nav-app-state-listener.md) | [JVM]<br>public Status[unregisterNavAppStateListener](unregister-nav-app-state-listener.md)(NavAppStateListenernavAppStateListener) |
