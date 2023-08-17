// IPartnerEnabler.aidl
package technology.cariad.partnerenablerservice;

interface IPartnerEnabler {
    /**
     * This method initializes the required components in the PartnerEnablerService.
    */
    void initialize() = 0;
    /**
     * This method releases/destroy the components created in the PartnerEnablerService.
    */
    void release() = 2;
}