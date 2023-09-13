package com.volkswagenag.nav.route.simplifier; 

interface IRouteSimplifier {

    /**
     * Minor interface version
     */
    const int VERSION = 1;
    
    /**
     * Requests the route guidance for the active route and returns
     * a JSON string with the current route encoded using flexible
     * polyline encoding (https://github.com/heremaps/flexible-polyline)
     *
     * @return JSON string with current route
     *  e.g.: {"version" : 1, "route": "<route encoded as polyline>"}
     */
    String getSimplifiedActiveRoute() = 10;
    
    /**
     * Request the minor version of the interface that is provided by the service.
     */
    int getIfcVersion() = 20; 
}
