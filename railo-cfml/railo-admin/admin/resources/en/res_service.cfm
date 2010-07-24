<!--- Datasources --->
<cfset stText.Settings.DatasourceDescriptionCreate     = "Create new datasource connection">
<cfset stText.Settings.DatasourceDescriptionUpdate     = "Update datasource connection">
<cfset stText.Settings.DatasourceSettings              = "Settings">
<cfset stText.Settings.DatasourceModify                = "Modify and add datasources">
<cfset stText.Settings.PreserveSingleQuotes            = "Preserve single quotes">
<cfset stText.Settings.PreserveSingleQuotesDescription = "Preserve single quotes ("") in the SQL defined with the tag cfquery">
<cfset stText.Settings.ReadOnlyDatasources             = "Global Datasources (Datasources from Server Administrator)">
<cfset stText.Settings.ReadOnlyDatasourcesDescription  = "These datasources have been created in the Server Administrator and are shared with ALL web contexts. You cannot modify these datasources in the Web Administrator, but you can override them by creating your own datasource with the same name.">

<cfset stText.Settings.ListDatasources                 = "Datasources">
<cfset stText.Settings.ListDatasourcesDescWeb          = "">
<cfset stText.Settings.ListDatasourcesDescServer       = "Datasources created here are available for ALL web contexts to use. The settings for these datasources can only be modified in this Server Administrator area.">

<cfset stText.Settings.DatasourceModify                = "Create new datasource">
<cfset stText.Settings.Name                            = "Name">
<cfset stText.Settings.Type                            = "Type">
<cfset stText.Settings.NameMissing                     = "Please enter a name for the datasource">
<cfset stText.Settings.DBCheck                         = "Check">

<cfset stText.Settings.dbHost		                   = "Host/Server">
<cfset stText.Settings.dbHostDesc	                   = "Host name where the database server is located">
<cfset stText.Settings.dbDatabase		               = "Database">
<cfset stText.Settings.dbDatabaseDesc	               = "Name of the database to connect">
<cfset stText.Settings.dbPort    		               = "Port">
<cfset stText.Settings.dbPortDesc   	               = "The port to connect the database">
<cfset stText.Settings.dbUser    		               = "Username">
<cfset stText.Settings.dbUserDesc   	               = "The username for the database">
<cfset stText.Settings.dbPass    		               = "Password">
<cfset stText.Settings.dbPassDesc   	               = "The password for the database">
<cfset stText.Settings.dbConnTimeout	               = "Connection timeout (in minutes)">
<cfset stText.Settings.dbConnTimeoutDesc   	           = "Define a time in minutes for how long a connection is kept alive before it will be closed">


<cfset stText.Settings.verifyConnection   	           = "Verify connection">
<cfset stText.Settings.dbConnLimit="Connection limit (max)">
<cfset stText.Settings.dbConnLimitInf="- inf -">
<cfset stText.Settings.dbConnLimitDesc="Restricts the maximal connections at time">
<cfset stText.Settings.dbBlob    		               = "Blob">
<cfset stText.Settings.dbBlobDesc   	               = "Enable binary large object retrieval (<abbr title=""binary large object"">BLOB</abbr>)">
<cfset stText.Settings.dbClob    		               = "Clob">
<cfset stText.Settings.dbClobDesc   	               = "Enable long text retrieval (<abbr title=""character large object"">CLOB</abbr>)">
<cfset stText.Settings.dbAllowed    	               = "Allowed operations">

<cfset stText.services.update.desc= "You can patch Railo in order to receive the latest bugfixes and improved version.">
<cfset stText.services.update.infoTitle= "Info">
<cfset stText.services.update.jira= "For details go to our {a}Bug Tracking System{/a}">
<cfset stText.services.update.update= "A patch {available} is available for your current version {current}.">
<cfset stText.services.update.noUpdate= "There is no patch available for version {current}">
<cfset stText.services.update.setTitle= "Properties">
<cfset stText.services.update.setDesc= "Define where Railo gets its patches. Railo will restart automatically after the update in order for the changes to take effect.">
<cfset stText.services.update.location= "URL">
<cfset stText.services.update.locDesc= "Define the URL where Railo gets its updates. Typically ""http://www.railo.ch""">
<cfset stText.services.update.type= "Type">
<cfset stText.services.update.typeDesc= "Define how Railo will be patched. ""Automatic"" means that Railo searches automatically for updates once a day. ""Manual"" means that you can update Railo only manually here.">
<cfset stText.services.update.type_auto= "Automatic">
<cfset stText.services.update.type_manually= "Manual">

<cfset stText.services.update.exe= "Execute update">
<cfset stText.services.update.exeRun= "execute update">
<cfset stText.services.update.exeDesc= "Apply the latest patch for your version. After the update has been installed Railo will be restarted, all sessions will be cleared and you have to login again.">


<cfset stText.services.update.remove="Remove installed patches">
<cfset stText.services.update.removeDesc="Remove all installed patches or only latest.">
<cfset stText.services.update.patch="Installed patches">
<cfset stText.services.update.removeRun="Remove patches">
<cfset stText.services.update.removeLatest="Remove latest patch {version}">

<cfset stText.services.update.restartDesc="Restart the Railo engine. All sessions will be cleared and you will have to login again.">
<cfset stText.services.update.restart="Restart Railo">

<cfset stText.remote.urlMissing               = "The remote client URL is missing">
<cfset stText.remote.serverusername           = "Username">
<cfset stText.remote.serverusernameDesc       = "Http Access Auhtentication">
<cfset stText.remote.serverpassword           = "Password">
<cfset stText.remote.serverpasswordDesc       = "Http Access Auhtentication">
<cfset stText.remote.LabelMissing             = "The label for the remote client is missing">
<cfset stText.remote.passwordMissing          = "The Administrator password for the remote client is missing">
<cfset stText.remote.securityKeyMissing       = "The security key for the remote client is missing">
<cfset stText.remote.adminPassword.web        = "Password for the remote Web Administrator">
<cfset stText.remote.adminPassword.server     = "Password for the remote Server Administrator">
<cfset stText.remote.adminPasswordDesc.web    = "Password für the access to the remote Railo Web Administrator">
<cfset stText.remote.adminPasswordDesc.server = "Password für the access to the remote Railo Server Administrator">
<cfset stText.remote.securityKey              = "Security Key">
<cfset stText.remote.securityKeyDesc          = "Please enter the key of the remote clients. You can find it under Remote security key in the administrator of the remote clients">
<cfset stText.remote.proxyServer              = "Server">
<cfset stText.remote.proxyServerDesc          = "Proxy Server (Host)">
<cfset stText.remote.proxyPort                = "Port">
<cfset stText.remote.proxyPortDesc            = "Proxy Server Port">
<cfset stText.remote.proxyUsername            = "Username">
<cfset stText.remote.proxyPassword            = "Password">
<cfset stText.remote.listClients              = "List of the clients">
<cfset stText.remote.newClient                = "Create new remote client">
<cfset stText.remote.desc                     = "Here you can define clients, which will synchronize their settings with the ones of the current administrator">
<cfset stText.remote.label                    = "Label">
<cfset sttext.remote.securityKeyTitleDesc     = "In case this Web Administrator is to be synchronized by another server, you have to enter the security key below in the distant definition of the remote client.">
<cfset stText.remote.ot.name                  = "Name">
<cfset stText.remote.ot.url                   = "URL">
<cfset stText.remote.ot.action                = "Action">
<cfset stText.remote.ot.lastExecution         = "Last execution">
<cfset stText.remote.ot.nextExecution         = "Next execution">
<cfset stText.remote.ot.nextExecutionInMinutes= "Next execution in minutes">
<cfset stText.remote.ot.exetime               = "Execution time">
<cfset stText.remote.ot.error                 = "Error messages">
<cfset stText.remote.ot.tries                 = "Number of tries">
<cfset stText.remote.ot.triesLeft             = "Number of remaining tries">
<cfset stText.remote.ot.state                 = "State">
<cfset stText.remote.ot.overviewDesc          = "List of failed tasks. The tasks listed in green will be reexecuted. The ones in red have failed all attempts of executuion and wont be executed again.">
<cfset stText.remote.ot.detailDesc.green      = "This task has been executed <tries> times. There are <triesleft> tries left">
<cfset stText.remote.ot.detailDesc.red        = "Even after <tries> tries, this task couln't be executed properly">
<cfset stText.remote.ot.fromto                = "1-100 of <recordcount> open tasks">
<cfset stText.remote.ot.noOt                  = "There are currently no open tasks">
<cfset stText.remote.sync.title               = "Remote Client synchronization">
<cfset stText.remote.sync.desc                = "Define the clients that will be synchronized with the current administrator.">
<cfset stText.remote.downloadArchive          = "When calling ""<Buttons.downloadArchive>"", the archive on the remote client will be created but the download ignored, In this case you shouln't include remote synchronization.">

<cfset stText .remote.ot.type="Type">
<cfset stText .remote.detail.update="Update Remote Client">
<cfset stText .remote.detail.updateDesc="">
<cfset stText .remote.detail.create="Create Remote Client">
<cfset stText .remote.detail.createDesc="">
<cfset stText .remote.usage.title="Usage">
<cfset stText .remote.usage.sync="Admin Synchronisation">
<cfset stText .remote.usage.cluster="Cluster Scope">
<cfset stText .remote.usage.desc="Define for what the Remote Client is used">
<cfset stText .remote.connection="Connection">
<cfset stText .remote.connectionDesc="Connection to the Remote Client, URL (with Port) and HTTP Access">
<cfset stText .remote.proxy="Proxy Settings">
<cfset stText .remote.proxyDesc="Proxy Settings should be used for connection">
<cfset stText .remote.adminAccess="Admin Access">
<cfset stText .remote.adminAccessDesc="Define Access to Remote Client, the Password and the Security Key, the Security key is provided by the Remote Client itself">
<cfset stText .remote.url="URL">
<cfset stText .remote.urlServer="Server">
<cfset stText .remote.urlServerDesc="Remote Client Server (Beispiel: http://railo.ch)">
<cfset stText .remote.urlServerMissing="Remote Client Server">
<cfset stText .remote.urlPath="Path">
<cfset stText .remote.urlPathDesc="Path to Admin.cfc (example: /railo-context/admin.cfc?wsdl)">
<cfset stText .remote.urlPathMissing="the path to the Admin.cfc is missing">