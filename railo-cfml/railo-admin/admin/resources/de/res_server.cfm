<!--- Regional --->
<cfset stText.Regional.Server                = "Hier k�nnen Sie regionale Einstellungen vornehmen, die Vorgabe f�r alle Web-Instanzen sind. Auf die aktuelle Instanz haben diese Werte keinen Einfluss.">
<cfset stText.Regional.Web                   = "Railo l�sst Sie Ihre eigenen individuellen Locale-, Zeitzonen- und Zeitservereinstellungen vorzunehmen.">
<cfset stText.Regional.Locale                = "Locale">
<cfset stText.Regional.LocaleDescription     = "Definiert die Standard-'Locale', die innerhalb einer Web-Instanz verwendet werden soll.">

<cfset stText.Regional.Other                 = " --- andere --- ">
<cfset stText.Regional.TimeZone              = "Zeitzone">
<cfset stText.Regional.TimeZoneDescription   = "Definiert die gew�nschte Zeitzone f�r Railo. Diese Einstellung �ndert die Zeit f�r den Kontext der Webs.<br>">
<cfset stText.Regional.ServerProp.server       	 = "Server Wert">
<cfset stText.Regional.ServerProp.web       = "Server Administrator Wert">
<cfset stText.Regional.TimeServer            = "Time server (NTP)">
<cfset stText.Regional.TimeServerDescription = "Time server der die aktuelle Zeit z�r�ck gibt. Falls gesetzt, wird in Railo diese Zeit anstelle der lokalen Server-Zeit verwendet (Beispiel: swisstime.ethz.ch, time.nist.gov).">
<cfset stText.Regional.TimeServerMissing     = "Bitte w�hlen Sie einen Wert f�r die Zeitzone aus.">
<cfset stText.Regional.DefaultEncoding       = "Standard Encoding">
<cfset stText.Regional.DefaultEncodingDescription = "">
<cfset stText.Mail.missingEncoding                = "Bitte geben Sie einen Wert f�r das Standard Encoding ein">

<!--- Charset --->
<cfset stText.charset.Server                = "Hier definieren Sie das Charset (Zeichensatz) das f�r verschiedene Einsatzwecke verwendet werden soll">
<cfset stText.charset.Web                   = stText.charset.Server>

<cfset stText.charset.webCharset       = "Web Charset">
<cfset stText.charset.webCharsetDescription = "Charset das als standardm�ssiger charset des Output Streams, zum Einlesen von form, url und cgi Scope und zum Schreiben und Lesen des Headers verwendet wird">
<cfset stText.charset.missingWebCharset= "Bitte geben Sie einen Wert f�r das Web Charset ein" >

<cfset stText.charset.templateCharset       			= "Template Charset">
<cfset stText.charset.templateCharsetDescription 		= "Charset das verwendet wird um die verschiedenen Templates (.cfm und .cfc Files) einzulesen">
<cfset stText.charset.missingTemplateCharset	     	= "Bitte geben Sie einen Wert f�r das Template Charset ein" >

<cfset stText.charset.resourceCharset       			= "Ressourcen Charset">
<cfset stText.charset.resourceCharsetDescription 	= "Charset das verwendet wird um die diversen Ressourcen (Files, Zip, Ftp usw.) einzulesen oder zu beschreiben">
<cfset stText.charset.missingResourceCharset	     	= "Bitte geben Sie einen Wert f�r das Ressource Charset ein" >

<!--- Components --->
<cfset stText.Components.Component                        = "Komponente">
<cfset stText.Components.Server                           = "Definert Komponenteneinstellungen, die als Standartwert f�r alle Webs gelten.">
<cfset stText.Components.Web                              = "Definiert wie Komopenten von Railo verarbeitet werden.">
<cfset stText.Components.BaseComponent                    = "Basis/Root Komponente">
<cfset stText.Components.BaseComponentDescription         = "Jede Komponente die nicht explizit eine andere Komponente erbt (Attribut 'extends') erbt diese Komponente, d.h. das jede Komponente diese Komponente direkt oder indirekt erbt.">
<cfset stText.Components.BaseComponentMissing             = "Bitte geben Sie einen Wert f�r die Basis/Root Komponente ein.">
<cfset stText.Components.ComponentDumpTemplate            = "Komponetenausgabe Template (dump)">
<cfset stText.Components.ComponentDumpTemplateDescription = "Wenn Sie ein Komponente direkt �ber den Browser aufrufen, wird dieses Template f�r die Darstellung verwendet. (Beispiel: http://www.railo.ch/ch/railo/common/Example.cfc)">
<cfset stText.Components.ComponentDumpTemplateMissing     = "Bitte geben Sie einen Eintrag f�r das 'dump' Template an.">
<cfset stText.Components.DataMemberAccessType             = "Zugriffsbeschr�nkung f�r Daten Mitglieder einer CFC">
<cfset stText.Components.DataMemberAccessTypeDescription  = "Definiert wie die Variablen des 'this' Scopes (Data Members) ausserhalb einer CFC angesprochen werden k�nnen. Eine strenge Objektorientierung w�rde verlangen, dass ein solcher Zugriff von Aussen gar nicht erst m�glich w�re (access=private).">
<cfset stText.Components.DMATPrivate                      = "private">
<cfset stText.Components.DMATPackage                      = "package">
<cfset stText.Components.DMATPublic                       = "public (CFML Standard)">
<cfset stText.Components.DMATRemote                       = "remote">

<cfset stText.Components.triggerDataMember				  = "Magic functions">
<cfset stText.Components.triggerDataMemberDescription	  = "Wenn eine Eigenschaft einer Komponente fehlt (oder gesch�tzt ist), pr�ft Railo weiter ob ein passender ""getter"" oder ""setter"" verf�gbar ist.
    Beispiel: ""myComponent.properyName"", wenn die Komponente 'myComponent' keine zugreifbare Eigenschaft mit dem Namen 'propertyName' hat,
    sucht Railo nach einer entsprechenden CFC-Funktion (Methode) mit dem Namen ""getPropertyName"". F�r Schreiboperationen auf eine solche Eigenschaft wird nach einer Methode ""setPropertyName"" in der Komponente gesucht.">
<cfset stText.Components.useShadow="Allow Variable Scope">
<cfset stText.Components.useShadowDescription="Definiert, ob eine Komponente einen eigenst�ndigen ""Variables"" Scope parallel zum ""this"" Scope hat (CFML Standard) oder nicht. Wenn nicht, dann werden alle nicht spezifizierten Variablen in den ""this"" scope geschrieben.">
<!--- Scopes --->
<cfset stText.Scopes.Server                        = "Definert Scopeeneinstellungen, die als Standardwert f�r alle Webs gelten sollen.">
<cfset stText.Scopes.Web                           = "Hier k�nnen Sie festlegen, wie Railo Scopes abarbeitet.">
<cfset stText.Scopes.Cascading                     = "Cascading">
<cfset stText.Scopes.CascadingDescription          = "Abh�ngig von dieser Einstellung durchsucht Railo gewisse Scopes, um eine im CFML-Code aufgerufene Variable zu finden. Dieses passiert jedoch nur, wenn die Variable ohne vorangestellten Scope aufgerufen wird. (Beispiel: ##myVar## anstelle von ##variables.myVar##)<br>- strict: durchsucht nur den Variables Scope<br>- small:	durchsucht die Scopes: Variables, Cgi, Url und Form<br>- standard (CFML Standard):	durchsucht die Scopes: Variables, Cgi, Url, Form und Cookie">
<cfset stText.Scopes.Strict                        = "strict">
<cfset stText.Scopes.Small                         = "small">
<cfset stText.Scopes.Standard                      = "standard (CFML Default)">
<cfset stText.Scopes.CascadeToResultSet            = "Resultsets durchsuchen">
<cfset stText.Scopes.CascadeToResultSetDescription = "Wenn eine Variable keinen vorangestellten Scope hat (Beispiel: ##myVar## anstelle von ##variables.myVar##), kann Railo (nach dem CFML Standard) auch verf�gbare Query-Resultsets durchsuchen.">
<cfset stText.Scopes.SessionType             		= "Session Typ">
<cfset stText.Scopes.SessionType_j2ee          		= "J2EE">
<cfset stText.Scopes.SessionType_cfml          		= "CFML">
<cfset stText.Scopes.SessionTypeDescription    		= "J2EE Sessions erlauben es Railo Sessions �ber einen J2EE Server-Cluster zu verteilen. Wenn Sie diese Einstellung �ndern verlieren Sie die aktuelle Session und m�ssen sich erneut einloggen.">
<cfset stText.Scopes.SessionManagement             = "Session Management">
<cfset stText.Scopes.SessionManagementDescription  = "Standardm�ssig kann Session Management eingeschaltet werden. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">
<cfset stText.Scopes.ClientManagement              = "Client Management">
<cfset stText.Scopes.ClientManagementDescription   = "Standardm�ssig kann Client Management eingeschaltet werden. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">
<cfset stText.Scopes.DomainCookies                 = "Domain Cookies">
<cfset stText.Scopes.DomainCookiesDescription      = "Domain Cookies ein oder ausschalten. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">
<cfset stText.Scopes.mergeUrlForm                 = "Url und Form kombinieren">
<cfset stText.Scopes.mergeUrlFormDescription       = "Mit dieser Einstellung kann festgelegt werden, ob Railo den Url und Form Scope zu einem einzigen Scope zusammenfasst oder nicht. Beim CFML Standard findet diese Zusammenfassung nicht statt.">
<cfset stText.Scopes.ClientCookies                 = "Client Cookies">
<cfset stText.Scopes.ClientCookiesDescription      = "Client Cookies ein oder ausschalten. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">
<cfset stText.Scopes.TimeoutDaysValue              = "Der Wert Tage f�r ">
<cfset stText.Scopes.TimeoutHoursValue             = "Der Wert Stunden f�r ">
<cfset stText.Scopes.TimeoutMinutesValue           = "Der Wert Minuten f�r ">
<cfset stText.Scopes.TimeoutSecondsValue           = "Der Wert Sekunden f�r ">
<cfset stText.Scopes.TimeoutEndValue               = "den Timeout muss ein ganzzahlige Wert sein.">
<cfset stText.Scopes.SessionTimeout                = "Session Timeout">
<cfset stText.Scopes.SessionTimeoutDescription     = "Legt die Zeit fest, wie lange Railo f�r inaktive Sessions den Session-Scope erh�lt. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">
<cfset stText.Scopes.ApplicationTimeout            = "Application Timeout">
<cfset stText.Scopes.ApplicationTimeoutDescription = "Legt die Zeit fest, wie lange Railo f�r inaktive Applikationen den Application-Scope erh�lt. Diese Verhaltensweise kann von dem Tag CFApplication �bersteuert werden.">

<cfset stText.Scopes.ClientTimeout            = "Client Timeout">
<cfset stText.Scopes.ApplicationTimeoutDescription = "Legt fest wielange Client Scope Daten behalten werden bevor sie gel�scht werden.">


<cfset stText.Scopes.LocalMode="Local Scope Modus">
<cfset stText.Scopes.LocalModeDesc="Definiert wie der Lokal Scope innerhalb einer UDF von einer Variable ohne Scope angesprochen wird.<br>
- always: der Lokal Scope wird immer verwendet<br>
- update (CFML Standard): der Lokal Scope wird nur verwendet, wenn der entsprechende Key darin bereits existiert.">
<cfset stText.Scopes.LocalModeAlways="Always">
<cfset stText.Scopes.LocalModeUpdate="Update (CFML Standard)">




<!--- Application --->
<cfset stText.application.Server                        = "Definert Applikationseinstellungen, die als Standardwert f�r alle Webs gelten.">
<cfset stText.application.Web                           = "Hier k�nnen Sie verschiedene Standardeinstellungen f�r den Applikations Kontext erfassen. Diese Einstellungen k�nnen von dem Tag CFApplication oder der Application.cfc �bersteuert werden.">
<cfset stText.application.RequestTimeout                = "Request Timeout">
<cfset stText.application.RequestTimeoutDescription     = "Legt die Zeit fest, wie lange Railo auf die Beendigung eines Requests warten soll. Das heisst, dass die Ausf�hrung des Requests beim �berschreiten dieser Zeit abgebrochen wird. Diese Verhaltensweise kann von dem Tag CFSetting �bersteuert werden.">

<cfset stText.application.scriptProtect       			= "Script-Protect">
<cfset stText.application.scriptProtectDescription 		= "Durch die Konfiguration von Script-Protect sch�tzen Sie ihr System vor 'Cross-Site Scripting'">
<cfset stText.application.missingScriptProtect	     	= "Bitte geben Sie einen Wert f�r das Script-Protect ein" >

<cfset stText.application.scriptProtectNone       			= "Script-Protect ist nicht aktiv">
<cfset stText.application.scriptProtectAll       			= "Script-Protect �berpr�ft alle Scopes ob die Daten von ausserhalb kommen (cgi, cookie, form, url)">
<cfset stText.application.scriptProtectCustom       		= "Definieren Sie individuell welche Scopes gepr�ft werden sollen und welche nicht">

<!--- Proxy --->
<cfset stText.proxy.Server                        = "Definieren Sie einen Proxy, welcher an diversen Stellen (cfhttp, cfftp, cfmail ...) in Railo Verwendung findet">
<cfset stText.proxy.Web                           = stText.proxy.Server>


<cfset stText.proxy.enableProxy       			= "Proxy verwenden">
<cfset stText.proxy.enableProxyDescription 		= "URL eines Proxy Server im Stil 'http://myproxyserver.org/'">

<cfset stText.proxy.server       			= "Server">
<cfset stText.proxy.serverDescription 		= "URL eines Proxy Server im Stil 'http://myproxyserver.org/'">

<cfset stText.proxy.port       				= "Port">
<cfset stText.proxy.PortDescription 		= "Port f�r den Proxy Server (Vorgabe:80)">
<cfset stText.proxy.missingPort     		= "Bitte geben Sie einen Wert f�r den Proxy Port an" >

<cfset stText.proxy.username       			= "Benutzername">
<cfset stText.proxy.usernameDescription 	= "Benutzername f�r den Proxy">

<cfset stText.proxy.password       			= "Passwort">
<cfset stText.proxy.passwordDescription 	= "Passwort f�r den Proxy">

<cfset stText.proxy.enable="Proxy verwenden">
<cfset stText.proxy.disable="Proxy nicht verwenden">
<cfset stText.proxy.settings="Proxy Einstellungen">


<!--- Listener --->
<cfset stText.application.listener="Application Listener">
<cfset stText.application.listenerDescription="Legt fest, wie Request verarbeitet und welche Templates einbezogen werden sollen.">

<!--- Type --->
<cfset stText.application.listenerType="Type">
<cfset stText.application.listenerTypeDescription="W�hlen Sie welche Art von Listener verwendet werden soll">

<cfset stText.application.listenerType_none="Kein">
<cfset stText.application.listenerTypeDescription_none="Bei einem Request wird ausschliesslich die entsprechende Datei aufgerufen">

<cfset stText.application.listenerType_classic="Klassisch (CFML < 7)">
<cfset stText.application.listenerTypeDescription_classic="Klassisches Handling. Railo sucht nur nach der Datei 'Application.cfm' und der entsprechenden Datei 'onRequestEnd.cfm'">

<cfset stText.application.listenerType_modern="Modern">
<cfset stText.application.listenerTypeDescription_modern="Modernes Handling. Railo sucht nur nach der Datei 'Application.cfc'">

<cfset stText.application.listenerType_mixed="Gemischt (CFML >= 7)">
<cfset stText.application.listenerTypeDescription_mixed="Gemischtes Handling. Railo sucht sowohl nach der Datei 'Application.cfm/onRequestEnd.cfm' wie auch nach der Datei 'Application.cfc'">

	<!--- Mode --->
<cfset stText.application.listenerMode="Mode">
<cfset stText.application.listenerModeDescription="Definiert wo nach den Dateien 'Application.cfc/application.cfm' gesucht werden soll. Beim Typ 'none' ist diese Einstellung ohne Bedeutung.">

<cfset stText.application.listenerMode_curr="Aktuell">
<cfset stText.application.listenerModeDescription_curr="Sucht nur im aktuellen Verzeichnis nach der Datei 'Application.cfc/Application.cfm'.">

<cfset stText.application.listenerMode_root="Root (Wurzelverzeichnis)">
<cfset stText.application.listenerModeDescription_root="Sucht nur im Webroot Verzeichnis nach der Datei 'Application.cfc/Application.cfm'.">

<cfset stText.application.listenerMode_curr2root="Aktuell bis Root (CFML Default)">
<cfset stText.application.listenerModeDescription_curr2root="Sucht nach der Datei 'Application.cfc/Application.cfm' vom aktuellen Verzeichnis aus zur�ck bis zum Webroot Verzeichnis.">

<cfset stText.err.errorTemplate[404]="Missing Template Error (404)">
<cfset stText.err.errorTemplateDescription[404]="Template das beim fehlen eines template verwendet wird. Diese Einstellung kann �ber den Tag CFError �bersteuert werden.">
<cfset stText.err.errorTemplate[500]="General Error Template (500)">
<cfset stText.err.errorTemplateDescription[500]="Template das in einem Fehlerfall verwendet werden soll. Diese Einstellung kann �ber den Tag CFError �bersteuert werden.">


<cfset stText.err.errorStatusCode="Status Code">
<cfset stText.err.errorStatusCodeDescription="Soll im Fehlerfall ein abweichender Statuscode zur�ckgegeben werden oder soll 200 beibehalten werden">


<cfset stText.err.descr="Geben Sie ein individuelles Error-Template an.">

<!--- Output --->
<cfset stText.setting.web="Kontrolle der Ausgabe von Railo">
<cfset stText.setting.whitespace="Whitespace management">
<cfset stText.setting.whitespaceDescription="Unterdr�ckt alle Whitespace (Leerzeichen, Tabs und Zeilenumbr�che), die in der Ausgabe einem Whitespace folgen">

<cfset stText.setting.showVersion="Railo Version ausgeben">
<cfset stText.setting.showVersionDescription="Railo Version im Response Header zur�ckgeben">

<cfset stText.setting.server=stText.setting.web>

<cfset stText.application.AllowURLRequestTimeout="Request timeout via URL">
<cfset stText.application.AllowURLRequestTimeoutDesc="Angabe des Parameters [RequestTimeout] in der URL ber�cksichtigen (Verhalten wie CFML 5, 7 & 8)">

<cfset stText.setting.cacheDesc="Verschiedene Cache and Performance Einstellungen um die Ausf�hrung zu verbessern.">

<cfset stText.setting.inspectTemplate="Templates inspizieren (CFM/CFC)">


<cfsavecontent variable="stText.setting.inspectTemplateNeverDesc">
When checked, any requested found to currently reside in the template cache will not be inspected for potential updates. For sites where templates are not updated during the life of the server, this minimizes file system overhead.
</cfsavecontent>
<cfsavecontent variable="stText.setting.inspectTemplateOnceDesc">
When checked, any requested files will be inspected only once for potential updates within a request. For sites where templates are not expected to reflect updates within the same request, this minimizes file system overhead. 
</cfsavecontent>
<cfsavecontent variable="stText.setting.inspectTemplateAlwaysDesc">
When checked, any requested files found to currently reside in the template cache will always be inspected for potential updates. For sites where templates are updated during the life of the server or within request.
</cfsavecontent>

<cfset stText.setting.inspectTemplateNever="Nie ( Beste Performance )">
<cfset stText.setting.inspectTemplateOnce="Einmal ( Gut )">
<cfset stText.setting.inspectTemplateAlways="Immer ( Schlecht )">


<cfset stText.setting.templateCache="Template Cache">
<cfset stText.setting.templateCacheClear="Template Cache leeren">
<cfset stText.setting.templateCacheClearCount="Template Cache leeren ( {count} Element(e) )">
<cfset stText.setting.templateCacheClearDesc="Dr�cken Sie den Button oberhalb um den Template Cache zu leeren.">

<cfset stText.setting.queryCache="Query Cache">
<cfset stText.setting.queryCacheClear="Query Cache leeren">
<cfset stText.setting.queryCacheClearCount="Query Cache leeren ( {count} Element(e) )">
<cfset stText.setting.queryCacheClearDesc="Dr�cken Sie den Button oberhalb um den Query Cache zu leeren.">

<cfset stText.setting.componentCache="Komponentenpfad Cache">
<cfset stText.setting.componentCacheClear="Komponentenpfad Cache leeren ( {count} Element(e) )">
<cfset stText.setting.componentCacheClearDesc="Dr�cken Sie den Button oberhalb um den Komponentenpfad Cache zu leeren.">