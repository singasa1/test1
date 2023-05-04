// ISignatureVerifier.aidl
package technology.cariad.partnerverifierlibrary;

// Declare any non-default types here with import statements

interface ISignatureVerifier {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean verifyDigitalSignature(String packageName);
}