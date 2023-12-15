//[root](../../../index.md)/[com.volkswagenag.partnerlibrary.demomode](../index.md)/[NavigationManagerDemoModeImpl](index.md)

# NavigationManagerDemoModeImpl

[JVM]\
public class [NavigationManagerDemoModeImpl](index.md)

## Constructors

| | |
|---|---|
| [NavigationManagerDemoModeImpl](-navigation-manager-demo-mode-impl.md) | [JVM]<br>public void[NavigationManagerDemoModeImpl](-navigation-manager-demo-mode-impl.md)(Contextcontext, [Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)&lt;[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)&gt;permissionsRequested) |

## Functions

| Name | Summary |
|---|---|
| [getActiveRoute](get-active-route.md) | [JVM]<br>public Response&lt;String&gt;[getActiveRoute](get-active-route.md)() |
| [isNavAppStarted](is-nav-app-started.md) | [JVM]<br>public Response&lt;Boolean&gt;[isNavAppStarted](is-nav-app-started.md)() |
| [registerActiveRouteUpdateListener](register-active-route-update-listener.md) | [JVM]<br>public Status[registerActiveRouteUpdateListener](register-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener) |
| [registerNavAppStateListener](register-nav-app-state-listener.md) | [JVM]<br>public Status[registerNavAppStateListener](register-nav-app-state-listener.md)(NavAppStateListenerlistener) |
| [startScheduler](start-scheduler.md) | [JVM]<br>public void[startScheduler](start-scheduler.md)()<br>Starts freqeuncy scheduler runnable, that runs every mChangeFrequencySecs seconds and updates values and triggers listeners if necessary. |
| [stopScheduler](stop-scheduler.md) | [JVM]<br>public void[stopScheduler](stop-scheduler.md)()<br>Stops the future that runs every mChangeFrequencySecs seconds to update values. |
| [unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md) | [JVM]<br>public Status[unregisterActiveRouteUpdateListener](unregister-active-route-update-listener.md)(ActiveRouteUpdateListeneractiveRouteUpdateListener) |
| [unregisterNavAppStateListener](unregister-nav-app-state-listener.md) | [JVM]<br>public Status[unregisterNavAppStateListener](unregister-nav-app-state-listener.md)(NavAppStateListenerlistener) |
