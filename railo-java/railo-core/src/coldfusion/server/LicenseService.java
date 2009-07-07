package coldfusion.server;

import java.util.Date;
import java.util.Map;

public interface LicenseService extends Service {

	public abstract void setLicenseKey(String arg0);

	public abstract String getLicenseKey();

	public abstract boolean isEntKey(String arg0);

	public abstract boolean isValidKey(String arg0);

	public abstract boolean isValidOldKey(String arg0);

	public abstract boolean isUpgradeKey(String arg0);

	public abstract Map getRequiredKeyInfo(String arg0);

	public abstract void init() throws Exception;

	public abstract boolean isValid();

	public abstract int getMajorVersion();

	public abstract Date getInstallDate();

	public abstract Date getExpirationDate();

	public abstract int getEvalDays();

	public abstract long getEvalDaysLeft();

	public abstract boolean isExpired();

	public abstract String getEdition();

	public abstract boolean isEnterprise();

	public abstract boolean isStandard();

	public abstract boolean isDeveloper();

	public abstract boolean isUpgrade();

	public abstract boolean isReportPack();

	public abstract boolean isEducational();

	public abstract boolean isDevNet();

	public abstract boolean isVolume();

	public abstract Map getProperties();

	public abstract String getOSPlatform();

	public abstract String getAppServerPlatform();

	public abstract String getVendor();

	public abstract int getServerType();

	public abstract long getVerityLimit();

	public abstract boolean allowJSP();

	public abstract boolean allowCFImport();

	public abstract boolean allowSandboxSecurity();

	public abstract int getCPUNumber();

	public abstract boolean isSingleIP();

	public abstract boolean allowAdvMgmt();

	public abstract boolean isValidIP(String arg0);

	public abstract String getAllowedIp();

	public abstract boolean allowFastMail();

	public abstract boolean allowEventService();

	public abstract boolean allowOracleOEM();

	public abstract boolean allowSybaseOEM();

	public abstract boolean allowInformixOEM();

	public abstract boolean allowDB2OEM();

	public abstract boolean isJadoZoomLoaded();

	public abstract void registerWithWatchService();

	public abstract void setEnableWatch(boolean arg0);

}