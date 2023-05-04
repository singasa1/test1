// ILibStateChangeListener.aidl
package technology.cariad.partnerlibrary;

// Declare any non-default types here with import statements

interface ILibStateChangeListener {
    /**
     * Callback to trigger once Partner Enabler service connection state changes
     */
    void onLibStateReady(boolean ready);
}