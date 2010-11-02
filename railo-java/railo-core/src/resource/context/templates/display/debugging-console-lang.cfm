<cfif cookie.RAILO_ADMIN_LANG eq "en">
	<cfset sNoDebugInfoAvailable = "No debug information available...">
	<cfset sTitleRequestTime     = "Request Time">
	<cfset sTitleExecTime        = "Execution Time">
	<cfset sTitleCalledURL       = "Called URL">
	<cfset sTitleOptions         = "Options">
	<cfset sCallersIPAddress     = "The caller's IP address">
	<cfset sTimeStampRecorded    = "Recording timestamp of the request">
	<cfset sTotalRequestTime     = "Total time of the request">
	<cfset sRequestedURL         = "Requested URL. Click on it in order to see the Debug output">
	<cfset sReplay               = "replay request (new window)">
	<cfset sStore2File           = "Stores the debugging output to a file">
	<cfset sReload               = "Reload Page">
	<cfset sEvery                = "every">
	<cfset sSeconds              = "seconds">
	<cfset sExecTimes            = "Execution times">
	<cfset sTitleExecTimes       = "Show execution times in debug output">
	<cfset sDisplayAsTree        = "display as tree">
	<cfset sTitleDispAsTree      = "Displays the templates as a tree hierarchy in calling order">
	<cfset sShowSQLStatements    = "SQL">
	<cfset sTitleShowSQL         = "Show SQL queries in debug output">
	<cfset sHideSQLStatements    = "Hide statements">
	<cfset sPlainSQLStatements   = "Output plain SQL Statements">
	<cfset sTitleHideSQL         = "Hides the SQL statements. Additional information for sortorder 'queryname' and 'file'">
	<cfset sDisplay              = "Display">
	<cfset sTitleDisplay         = "Elements exceeding this number according sortorder will be supressed">
	<cfset sFiles                = "templates">
	<cfset sQueries              = "queries">
	<cfset sSortOrder            = "Sortorder">
	<cfset sFilterOwnAddress     = "Filter own IP Address">
	<cfset sTitleOwnAddress      = "Displays only requests from your current IP address">
	<cfset sFilterTemplates      = "Filter strings (separated by comma)">
	<cfset sTitleFilterTemplates = "Only URL's matching one of the patterns will be debugged">
	<cfset sIncludeOnlyTemplates = "include all except">
	<cfset sTitleIncludeOnly     = "Debug all URL's that do not contain one of the following strings">
	<cfset sExcludeOnlyTemplates = "exclude all except">
	<cfset sTitleExcludeOnly     = "Debug all URL's that contain one of the following strings">
	<cfset sSave                 = "save settings">
	<cfset sResetSettings        = "reset settings">
	<cfset sExecutionTimeLimit   = "Display only requests running more than: ">
<cfelse>
	<cfset sNoDebugInfoAvailable = "Keine debug Informationen vorhanden...">
	<cfset sTitleRequestTime     = "Request Zeit">
	<cfset sTitleExecTime        = "Ausführungszeit">
	<cfset sTitleCalledURL       = "Aufgerufene URL">
	<cfset sTitleOptions         = "Optionen">
	<cfset sCallersIPAddress     = "IP Adresse des Aufrufers">
	<cfset sTimeStampRecorded    = "Zeitstempel des Request">
	<cfset sTotalRequestTime     = "Gesamtzeit für den Request">
	<cfset sRequestedURL         = "Aufgerufene URL. Klicken Sie drauf, um die Debugging Informationen anzusehen">
	<cfset sReplay               = "Request aufrufen (neues Fenster)">
	<cfset sStore2File           = "Speichert die Debuggingausgabe in einer Datei">
	<cfset sReload               = "Seite neu laden">
	<cfset sEvery                = "Alle">
	<cfset sSeconds              = "Sekunden">
	<cfset sExecTimes            = "Ausführungszeiten">
	<cfset sTitleExecTimes       = "Zeigt die Ausführungszeiten der einzelnen Templates in der Debugging Ausgabe an">
	<cfset sDisplayAsTree        = "als Baum">
	<cfset sTitleDispAsTree      = "Stellt die aufgerufenen Templates als Baumstruktur entsprechend der Aufrufreihenfolge dar">
	<cfset sShowSQLStatements    = "SQL">
	<cfset sTitleShowSQL         = "Zeigt die aufgerufenen SQL Queries in der Debugging Ausgabe an">
	<cfset sHideSQLStatements    = "Statements verbergen">
	<cfset sPlainSQLStatements   = "SQL Statements direkt ausgeben">
	<cfset sTitleHideSQL         = "Verbirgt die SQL Statements. Zusätzliche Informationen bei Sortierung 'queryname' und 'file'">
	<cfset sDisplay              = "Zeige">
	<cfset sTitleDisplay         = "Elemente die diese zahl entsprechend der Sortierung übersteigen werden unterdrückt">
	<cfset sFiles                = "Templates">
	<cfset sQueries              = "Queries">
	<cfset sSortOrder            = "Sortierung">
	<cfset sFilterOwnAddress     = "Eigene IP Adresse filtern">
	<cfset sTitleOwnAddress      = "Zeigt nur requests der eigenen IP Adress an">
	<cfset sFilterTemplates      = "Filtermuster (getrennt durch Komma)">
	<cfset sTitleFilterTemplates = "Nur URL's die einem der Muster entsprechen werden debuggt">
	<cfset sIncludeOnlyTemplates = "alle ausser">
	<cfset sTitleIncludeOnly     = "Debuggt alle URL's, die keine der folgenden Zeichenketten enthalten">
	<cfset sExcludeOnlyTemplates = "keine ausser">
	<cfset sTitleExcludeOnly     = "Debuggt nur URL's die eine der folgenden Zeichenketten enthalten">
	<cfset sSave                 = "Einstellungen speichern">
	<cfset sResetSettings        = "Einstellungen zurücksetzen">
	<cfset sExecutionTimeLimit   = "Nur Requests anzeigen die länger dauern als:">
</cfif>
