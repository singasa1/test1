// IActiveRouteUpdateCallback.aidl
package technology.cariad.partnerlibrary;

// Declare any non-default types here with import statements

interface IActiveRouteUpdateCallback {
    /**
     * Callback to trigger once Navigation App state changes
    */
    void onActiveRouteChange(in String activeRoute, in String encodingType);
}