// IPartnerEnabler.aidl
package technology.cariad.partnerenablerservice;

// Declare any non-default types here with import statements
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

interface IPartnerEnabler {
    /**
     * This method initializes the required components in the PartnerEnablerService.
    */
    void initialize() = 0;

    /**
     * This method returns the PartnerVerifier Service Connection Binder Handler
    */
    ISignatureVerifier getPartnerVerifierService() = 1;

    /**
     * This method releases/destroy the components created in the PartnerEnablerService.
    */
    void release() = 2;
}