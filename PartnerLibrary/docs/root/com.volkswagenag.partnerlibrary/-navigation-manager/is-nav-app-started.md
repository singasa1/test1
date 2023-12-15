//[root](../../../index.md)/[com.volkswagenag.partnerlibrary](../index.md)/[NavigationManager](index.md)/[isNavAppStarted](is-nav-app-started.md)

# isNavAppStarted

[JVM]\

public abstract Response&lt;Boolean&gt;[isNavAppStarted](is-nav-app-started.md)()

Returns the Navigation Application state. 

Requires Permission: PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE

#### Return

&lt; with value: true - if Navigation Application state is fully operable. false - if Navigation Application state is Loading, NavDB Error, NoLicense, etc,.
