//[root](../../../index.md)/[com.volkswagenag.partnerlibrary](../index.md)/[NavigationManager](index.md)/[getActiveRoute](get-active-route.md)

# getActiveRoute

[JVM]\

public abstract Response&lt;String&gt;[getActiveRoute](get-active-route.md)()

Returns the route guidance of the current active route from Navigation Application. 

Requires Permission: PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE

#### Return

&lt; with value: A JSON string with the current route encoded using flexible polyline encoding. ex: {&quot;version&quot;: 1, &quot;route&quot;: &quot;&quot;} OR null - if there is no active route.
