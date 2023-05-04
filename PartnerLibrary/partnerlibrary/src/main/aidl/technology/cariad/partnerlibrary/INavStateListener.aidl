// INavStateListenerCallback.aidl
package technology.cariad.partnerlibrary;

// Declare any non-default types here with import statements

interface INavStateListener {
    /**
     * Callback to trigger once Navigation App state changes
    */
    void onNavStateChanged(boolean started);
}