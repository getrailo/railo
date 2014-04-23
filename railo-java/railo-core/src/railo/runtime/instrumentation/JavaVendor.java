package railo.runtime.instrumentation;

import railo.commons.lang.StringUtil;

/**
 * Utilities for dealing with different Java vendors.
 */
public enum JavaVendor {
    IBM("com.ibm.tools.attach.VirtualMachine"), SUN("com.sun.tools.attach.VirtualMachine"),
    // When in doubt, try the Sun implementation.
    OTHER("com.sun.tools.attach.VirtualMachine");

    static {
        String vendor = System.getProperty("java.vendor");
        if (StringUtil.containsIgnoreCase(vendor, "SUN MICROSYSTEMS")) {
            _vendor = SUN;
        } 
        else if (StringUtil.containsIgnoreCase(vendor, "IBM")) {
            _vendor = IBM;
        } 
        else {
            _vendor = OTHER;
        }
    }
    
    private static final JavaVendor _vendor;
    private String _virtualMachineClass = null;
    
    private JavaVendor(String vmClass) {
        _virtualMachineClass = vmClass;
    }

    /**
     * This static worker method returns the current Vendor.
     */
    public static JavaVendor getCurrentVendor() {
        return _vendor;
    }
    
    /**
     * This static worker method returns <b>true</b> if the current implementation is IBM.
     */
    public boolean isIBM() {
        return _vendor == IBM;
    }

    /**
     * This static worker method returns <b>true</b> if the current implementation is Sun.
     */
    public boolean isSun() {
        return _vendor == SUN;
    }
    
    public String getVirtualMachineClassName() {
        return _virtualMachineClass;
    }
}
