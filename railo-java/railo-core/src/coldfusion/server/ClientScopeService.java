

package coldfusion.server;

import java.util.Map;

import railo.runtime.PageContext;

public interface ClientScopeService extends Service {

	//public abstract ClientScope GetClientScope(PageContext pc,ClientScopeKey arg1, Properties arg2);

	public abstract int GetClientId(PageContext pc);

	public abstract String GetCFTOKEN();

	public abstract void PersistClientVariablesForRequest();

	//public abstract void UpdateGlobals(PageContext pc, ClientScope arg1);

	public abstract String GetDefaultDSN();

	public abstract boolean IsValidDSN(String arg0);

	public abstract Map getClientstores();

	public abstract Map getSettings();

	public abstract void PurgeExpiredClients();

}