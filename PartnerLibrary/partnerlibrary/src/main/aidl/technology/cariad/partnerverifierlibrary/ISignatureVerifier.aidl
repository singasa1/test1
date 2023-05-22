// ISignatureVerifier.aidl
package technology.cariad.partnerverifierlibrary;

// Declare any non-default types here with import statements

interface ISignatureVerifier {
    /**
     * verifies the given packagename digital signature.
     * and returns true if the signature matches else returns false.
     */
    boolean verifyDigitalSignature(String packageName);
}