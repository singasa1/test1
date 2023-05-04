// IPartnerEnabler.aidl
package technology.cariad.partnerenablerservice;

// Declare any non-default types here with import statements
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

interface IPartnerEnabler {

    void initialize() = 0;

    // Get VerifierLibrary binder handle
    ISignatureVerifier getPartnerVerifierService() = 1;

    void release() = 2;
}