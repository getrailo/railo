<!--- Datasources --->
<cfset stText.Settings.DatasourceDescriptionCreate     = "Neue Datenquelle erstellen">
<cfset stText.Settings.DatasourceDescriptionUpdate     = "Datenquelle aktualisieren">
<cfset stText.Settings.DatasourceSettings              = "Einstellungen">
<cfset stText.Settings.DatasourceModify                = "Datenquelle ￤ndern oder hinzuf￼gen">

<cfset stText.Settings.PreserveSingleQuotes            = "Escape Single Quotes">
<cfset stText.Settings.PreserveSingleQuotesDescription = "Verdoppelt die einfachen Anf￼hrungszeichen (') in den SQL-Statements, die im Tag CFQuery definiert werden. Wenn diese Option eingeschaltet ist verdoppelt Railo alle einfache Anf￼hrungszeichen. Achtung dies macht ihr Code angreifbar f￼r ""SQL Injection attacks""">


<cfset stText.Settings.ReadOnlyDatasources             = "Globale Datequellen (Datenquellen vom Server Administrator)">
<cfset stText.Settings.ReadOnlyDatasourcesDescription  = "Diese Datenquellen werden im Server Administrator f￼r alle Web Instanzen erstellt und k￶nnen im Web Administrator nicht ge￤ndert werden, diese k￶nnen jedoch ￼berschrieben werden indem sie eine Datenquelle mit dem selben Namen unterhalb anlegen.">
<cfset stText.Settings.ListDatasources                 = "Vorhandene Datenquellen">
<cfset stText.Settings.ListDatasourcesDescWeb          = "">
<cfset stText.Settings.ListDatasourcesDescServer       = "Datenquellen welche hier erstellt wurden, stehen allen Webkontexten zur Verf￼gung, jedoch kann die Konfiguration der Datenquelle nur hier ge￤ndert werden.">

<cfset stText.Settings.DatasourceModify                = "Neue Datenquelle erstellen">
<cfset stText.Settings.Name                            = "Name">
<cfset stText.Settings.Type                            = "Typ">
<cfset stText.Settings.NameMissing                     = "Bitte geben Sie einen Namen f￼r die Datenquelle ein.">
<cfset stText.Settings.DBCheck                         = "Check">


<cfset stText.Settings.dbHost		                   = "Host/Server">
<cfset stText.Settings.dbHostDesc	                   = "Host Name wo die Datenbank liegt">
<cfset stText.Settings.dbDatabase		               = "Datenbank">
<cfset stText.Settings.dbDatabaseDesc	               = "Name der Datenbank, welche verbunden werden soll.">
<cfset stText.Settings.dbPort    		               = "Port">
<cfset stText.Settings.dbPortDesc   	               = "Der Port der Datenbank die angesprochen werden soll.">
<cfset stText.Settings.dbUser    		               = "Benutzername">
<cfset stText.Settings.dbUserDesc   	               = "Benutzername f￼r den Zugriff auf die Datenbank">
<cfset stText.Settings.dbPass    		               = "Passwort">
<cfset stText.Settings.dbPassDesc   	               = "Passwort f￼r den Zugriff auf die Datenbank">
<cfset stText.Settings.dbConnTimeout	               = "Verbindungs Timeout (in Minuten)">
<cfset stText.Settings.dbConnTimeoutDesc   	           = "Definiert wie lange eine stehende Verbindung aufrechterhalten wird, bevor Sie beendet wird.">

<cfset stText.Settings.verifyConnection   	           = "Verbindung pr￼fen">
<cfset stText.Settings.dbConnLimit="Verbindungslimitierung (max)">
<cfset stText.Settings.dbConnLimitInf="- inf -">
<cfset stText.Settings.dbConnLimitDesc="Beschr￤nkt die Anzahl Verbindungen die zur Datenbank aufgebaut werden.">
<cfset stText.Settings.dbBlob    		               = "Blob">
<cfset stText.Settings.dbBlobDesc   	               = "Erlaubt das einbeziehen von BLOBs">
<cfset stText.Settings.dbClob    		               = "Clob">
<cfset stText.Settings.dbClobDesc   	               = "Erlaubt das einbeziehen von CLOBs">
<cfset stText.Settings.dbAllowed    	               = "Erlaubte Operationen">

<cfset stText.services.update.desc= "
Sie k￶nnen Railo auf eine neue Version patchen, also bekannte Fehler ausb￼geln 
und Optimierungen innerhalb ihrer Version einspielen.">
<cfset stText.services.update.infoTitle= "Info">
<cfset stText.services.update.jira= "F￼r Details besuchen Sie unser {a}Bug Tracking System{/a}">
<cfset stText.services.update.update= "F￼r Ihre Version steht ein Patch zur Verf￼gung, Sie haben die Version {current} installiert, die aktuell verf￼gbare Version ist {avaiable}">
<cfset stText.services.update.noUpdate= "F￼r Ihre Version {current} steht kein Patch zur Verf￼gung">
<cfset stText.services.update.setTitle= "Einstellungen">
<cfset stText.services.update.setDesc= "Definieren Sie wie und wo Ihre Railo Version ihre Patches bezieht. Damit die eine ￄnderung dieser Einstellung aktiv wird, ist ein Neustart von Railo erforderlich.">
<cfset stText.services.update.location= "URL">
<cfset stText.services.update.locDesc= "Definieren Sie die URL von der ein Railo Update bezogen werden kann, typischerweise 'http://www.railo.ch'">
<cfset stText.services.update.type= "Typ">
<cfset stText.services.update.typeDesc= "Definieren Sie hier wie Railo gepatcht werden soll, 'Automatisch' steht daf￼r, dass Railo automatisch, t￤glich nach updates sucht. 'Manuell' bedeutet, dass ein Update nur manuell ausgef￼hrt werden kann.">
<cfset stText.services.update.type_auto= "Automatisch">
<cfset stText.services.update.type_manually= "Manuell">

<cfset stText.services.update.exe= "Update ausf￼hren">
<cfset stText.services.update.exeRun= "Update ausf￼hren">
<cfset stText.services.update.exeDesc= "Spielen Sie die aktuellsten Pacthes f￼r Ihre Version ein. Nach einem erfolgten Update, m￼ssen Sie sich neu in den Administrator einloggen.">
<cfset stText.services.update.restartDesc="Hier k￶nnen Sie Railo neu starten. Nach erfolgtem Restart werden bestehende Sitzungen entfernt. Sie m￼ssen sich in den Administrator erneut einloggen.">
<cfset stText.services.update.restart="Railo neu starten">


<cfset stText.services.update.remove="Installierte Patches entfernen">
<cfset stText.services.update.patch="Installierte Patches">
<cfset stText.services.update.removeRun="Patches entfernen">


<cfset stText.services.update.removeLatest= "Letzten Patch {version} entfernen">
<cfset stText.services.update.removeDesc= "Entfernen Sie alle nach der Grundinstallation nachtr￤glich installierten Patches order nur den Letzten.">


<cfset stText.remote.urlMissing               = "Die Remote Client URL fehlt">
<cfset stText.remote.serverusername           = "Username">
<cfset stText.remote.serverusernameDesc       = "Http Access Auhtentication Username">
<cfset stText.remote.serverpassword           = "Passwort">
<cfset stText.remote.serverpasswordDesc       = "Http Access Auhtentication Passwort">
<cfset stText.remote.LabelMissing             = "Die Bezeichnung f￼r den Remote Client fehlt">
<cfset stText.remote.passwordMissing          = "Das Administrator Passwort f￼r den Remote Client fehlt">
<cfset stText.remote.securityKeyMissing       = "Der Sicherheitsschl￼ssel  f￼r den Remote Client fehlt">
<cfset stText.remote.adminPassword.web        = "Passwort f￼r den Remote Web Administrator">
<cfset stText.remote.adminPassword.server     = "Passwort f￼r den remote Server Administrator">
<cfset stText.remote.adminPasswordDesc.web    = "Passwort f￼r den Zugriff auf den Remote Railo Web Administrator">
<cfset stText.remote.adminPasswordDesc.server = "Passwort f￼r den Zugriff auf den Remote Railo Server Administrator">
<cfset stText.remote.securityKey              = "Sicherheitsschl￼ssel">
<cfset stText.remote.securityKeyDesc          = "Geben Sie hier den Schl￼ssel des Remote Clients an. Sie finden ihn unter Remote-Scherheitsschl￼ssel im Administrator des Remote Clients">
<cfset stText.remote.proxyServer              = "Server">
<cfset stText.remote.proxyServerDesc          = "Proxy Server (Host)">
<cfset stText.remote.proxyPort                = "Port">
<cfset stText.remote.proxyPortDesc            = "Proxy Server Port">
<cfset stText.remote.proxyUsername            = "Username">
<cfset stText.remote.proxyPassword            = "Passwort">
<cfset stText.remote.listClients              = "Liste der Clients">
<cfset stText.remote.newClient                = "Neuen Remote Client erstellen">
<cfset stText.remote.desc                     = "Definiert Clients, mit welchen die Einstellungen dieses Administrators, synchronisiert werden sollen">
<cfset stText.remote.label                    = "Bezeichnung">
<cfset sttext.remote.securityKeyTitleDesc     = "Falls dieser Web Administrator von einem anderen Server aus synchronisiert werden soll, m￼ssen sie den unten angegebenen Schl￼ssel bei der Definition der Remote Client Verbindung angeben.">
<cfset stText.remote.ot.name                  = "Name">
<cfset stText.remote.ot.url                   = "URL">
<cfset stText.remote.ot.action                = "Aktion">
<cfset stText.remote.ot.lastExecution         = "Letzte Ausf￼hrung">
<cfset stText.remote.ot.nextExecutionInMinutes         = "N￤chste Ausf￼hrung in Minuten">
<cfset stText.remote.ot.exetime               = "Ausf￼hrungszeit">
<cfset stText.remote.ot.error                 = "Fehlermeldungen">
<cfset stText.remote.ot.tries                 = "Anzahl Versuche">
<cfset stText.remote.ot.triesLeft             = "Anzahl verbleibender Versuche">
<cfset stText.remote.ot.state                 = "Status">
<cfset stText.remote.ot.overviewDesc          = "Liste der fehlgeschlagenen Tasks. Die hier gr￼n aufgelisteten werden nochmals ausgef￼hrt. Die rot gelisteten Tasks konnten nicht ausgef￼hrt werden.">
<cfset stText.remote.ot.detailDesc.green      = "Es wurde bereits <tries> mal versucht diesen Task auszuf￼hren. <triesleft> Versuche stehen noch aus">
<cfset stText.remote.ot.detailDesc.red        = "Dieser Task konnte trotz <tries> Versuchen nicht korrekt ausgef￼hrt werden">
<cfset stText.remote.ot.fromto                = "1-100 von <recordcount> offenen Tasks">
<cfset stText.remote.ot.noOt                  = "Zur Zeit gibt es keine offenen Tasks">
<cfset stText.remote.sync.title               = "Remote Client Synchronisation">
<cfset stText.remote.sync.desc                = "Definieren sie, mit welchen Clients die hier gemachten Einstellungen synchronisiert werden sollen">
<cfset stText.remote.downloadArchive          = "Bei ""<Buttons.downloadArchive>"" wird der Download des Archivs auf dem Remote Client ignoriert, das Archiv wird jedoch angelegt. In diesem Fall empfiehlt es sich den Remote Client nicht einzubeziehen.">

<cfset stText .remote.ot.type="Typ">
<cfset stText .remote.detail.update="Remote Client updaten">
<cfset stText .remote.detail.updateDesc="">
<cfset stText .remote.detail.create="Remote Client erstellen">
<cfset stText .remote.detail.createDesc="">
<cfset stText .remote.usage.title="Verwendung">
<cfset stText .remote.usage.sync="Admin Synchronisation">
<cfset stText .remote.usage.cluster="Cluster Scope">
<cfset stText .remote.usage.desc="Definiert f￼r was alles der Remote Client verwendet werden soll">
<cfset stText .remote.connection="Verbindung">
<cfset stText .remote.connectionDesc="Verbindung zum Remote Client, komplette URL (inkl. Port) und HTTP Access">
<cfset stText .remote.proxy="Proxy Settings">
<cfset stText .remote.proxyDesc="Proxy Settings welche f￼r die Verbindung verwendet werden sollen">
<cfset stText .remote.adminAccess="Admin Access">
<cfset stText .remote.adminAccessDesc="Definiert den Zugang zum Remote Client, dessen Password und Security Key, die Security key kann innerhalb des Remote Client bezogen werden">
<cfset stText .remote.url="URL">
<cfset stText .remote.urlServer="Server">
<cfset stText .remote.urlServerDesc="Remote Client Server (Beispiel: http://railo.ch)">
<cfset stText .remote.urlServerMissing="Remote Client Server">
<cfset stText .remote.urlPath="Pfad">
<cfset stText .remote.urlPathDesc="Pfad zur Admin.cfc (Beispiel: /railo-context/admin.cfc?wsdl)">
<cfset stText .remote.urlPathMissing="Der Pfad zur Admin.cfc fehlt">

<cfset stText.remote.from="von">
<cfset stText.remote.to="bis">
<cfset stText.remote.previous="vorherige">
<cfset stText.remote.next="n￤chste">
