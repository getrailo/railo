<cfset stText.Security.Title                       = "Standard Zugriffsrechte">
<cfset stText.Security.TitleDescription            = "Hier können Sie die Zugriffsrechte festlegen, die für alle Instanzen verwendet werden sollen. Falls zugelassen, können diese Einstellungen in den lokalen 'Web Administratoren' überschrieben werden.">
<cfset stText.Security.WebAdministrator            = "Web Administrator">
<cfset stText.Security.WebAdministratorDescription = "Hier können Sie die Zugriffsrechte für die Einstellungen festlegen, die in den 'Web Administratoren' überschrieben werden können.">
<cfset stText.Security.Settings                    = "Einstellungen (Region, Komponenten, Scope)">
<cfset stText.Security.SettingsDescription         = "Die Einstellungen Region, Komponenten und Scope können im 'Web Administrator' geändert werden.">
<cfset stText.Security.Mail                        = "Mail">
<cfset stText.Security.MailDescription             = "Die Maileinstellungen  können im 'Web Administrator' geändert werden.">
<cfset stText.Security.Datasource                  = "Datasource">
<cfset stText.Security.DatasourceDescription       = "Definiert wieviele Datenquellen im 'Web Administrator' hinzugefügt, gelöscht und geändert werden können.">
<cfset stText.Security.DatasourceTextes=struct('-1':"unbeschränkt")>

<cfset stText.Security.Mapping                     = "Mappings">
<cfset stText.Security.MappingDescription          = "Mappings können 'Web Administrator' hinzugefügt, gelöscht und geändert werden.">
<cfset stText.Security.Remote                     = "Remote">
<cfset stText.Security.RemoteDescription          = "Es wird erlaubt das der User seine Einstellungen mit einem anderen railo synchronisiern kann">

<cfset stText.Security.CustomTag                   = "Custom Tags">
<cfset stText.Security.CustomTagDescription        = "Custom tags können im 'Web Administrator' hinzugefügt, gelöscht und geändert werden.">

<cfset stText.Security.Cache                   = "Cache">
<cfset stText.Security.CacheDescription        = "Cache Settings können im 'Web Administrator' hinzugefügt, gelöscht und geändert werden.">

<cfset stText.Security.Gateway                   		= "Gateway">
<cfset stText.Security.GatewayDescription        = "Gateway Settings können im 'Web Administrator' hinzugefügt, gelöscht und geändert werden.">

<cfset stText.Security.CFX                         = "CFX">
<cfset stText.Security.CFXDescription              = "Die Einstellungen für die CFX Tags können geändert werden. Im 'Server Administrator' global definierte Tags können auch verwendet werden.">
<cfset stText.Security.Debugging                   = "Debugging">
<cfset stText.Security.DebuggingDescription        = "Die Einstellungen für Debugging können im 'Web Administrator' geändert werden.">
<cfset stText.Security.Search                      = "Suche">
<cfset stText.Security.SearchDescription           = "Die Einstellungen für Suche können im 'Web Administrator' geändert werden.">
<cfset stText.Security.ScheduledTask               = "Scheduled Task">
<cfset stText.Security.ScheduledTaskDescription    = "Die Einstellungen für Scheduled Task können im 'Web Administrator' geändert werden.">

<cfset stText.Security.CFMLEnvironment             = "CFML Umgebung">
<cfset stText.Security.CFMLEnvironmentDescription  = "Einstellungen die festlegen wie Railo Code mit dem Hostsystem interagiert.">
<cfset stText.Security.File                        = "Dateizugriff">
<cfset stText.Security.FileDescription             = "Legt fest, wie Railo mit dem lokalen Dateisystem in einer Instanz interagieren kann.<br>- none: erlaubt keinen Zugriff auf das lokale Dateisystem<br>- local: erlaubt nur Dateizugriffe innerhalb des Webrootverzeichnisses<br>- all: erlaubt vollen Dateizugriff auf das Hostsystem.<br>">
<cfset stText.Security.FileAll                     = "all">
<cfset stText.Security.FileLocal                   = "local">
<cfset stText.Security.FileNone                    = "none">
<cfset stText.Security.JavaAccess                  = "Direkter Java Zugriff">
<cfset stText.Security.JavaAccessDescription       = "Erlaubt den Zugriff auf Java Methoden und Eigenschaften aus Railo Code (Beispiel: stringValue.substring(2,5)). Wenn Sie direkten Java Zugrif zulassen, kann dies ein potentielles Sicherheitsrisiko darstellen.">
<cfset stText.Security.Functions                   = "Tags & Funktionen">
<cfset stText.Security.FunctionsDescription        = "Tags and Funktionen, die ein potentielles Sicherheitsrisiko für das Hostsystem darstellen.">
<cfset stText.Security.TagExecute                  = "Tag CFExecute">
<cfset stText.Security.TagExecuteDescription       = "Dieser Tag kann verwendet werden um Prozesse auf dem lokalen Hostsystem auszuführen.">
<cfset stText.Security.TagImport                   = "Tag CFImport">
<cfset stText.Security.TagImportDescription        = "Dieser Tag kann verwendet werden um JSP und Railo Tag Bibliotheken zu importieren.">
<cfset stText.Security.TagObject                   = "Tag CFObject / <br>Funktion createObject">
<cfset stText.Security.TagObjectDescription        = "Mit dem Tag CFObject und der Funktion createObject können Java Objekte geladen werden. Wenn Sie diese Option ausschalten, können Sie nur noch Objekte des Typs 'component' erstellen.">
<cfset stText.Security.TagRegistry                 = "Tag CFRegistry">
<cfset stText.Security.TagRegistryDescription      = "Mit dem Tag CFRegistry haben Sie vollen Zugriff auf die Registry des Hostsystems.">
<cfset stText.Security.CFXTags                     = "CFX tags">
<cfset stText.Security.CFXTagsDescription          = "Mit Hilfe von CFX Tags können Java Klassen geladen werden, die ev. volles Zugriffsrecht auf das Hostsystem gewähren.">


<cfset stText.Security.desc="Definieren Sie die Zugriffsrechte für die verschiedenen Web Kontexte (webapps). Unter dem Reiter 'Allgemein' legen Sie die Standardrechte für alle Web Kontexte fest. Individuelle Einstellungen können Sie für jedes einzelne Web im Reiter 'Individuell' definieren.">
<cfset stText.Security.tabGeneral="Allgemein">
<cfset stText.Security.tabSpecial="Individuell">
<cfset stText.Security.generalDesc="Definieren Sie hier die allgemeinen Sicherheitseinstellungen, welche als Standard für Web Kontexte (webapps) gelten sollen.">
<cfset stText.Security.specialDesc="Definieren Sie hier Sicherheitseinstellungen für einzelne Web Kontexte (webapps).">
<cfset stText.Security.specListTitle="Spzifische Web Kontexte">
<cfset stText.Security.specListText="Web Kontexte für die es bereits einen spezifische Einstellung gibt. Sie können diese Kontexte anpassen oder löschen.">
<cfset stText.Security.specListHost="Hostname">
<cfset stText.Security.specListPath="Pfad">
<cfset stText.Security.specListWebContext="Web Kontext">
<cfset stText.Security.specListNewTitle="Neuen Web Kontext erstellen">



<cfset stText.Security.general="Allgemeiner Zugriff">
<cfset stText.Security.generalDesc="Definiert den allgemeinen Zugriff auf den Administrator und das Tad cfadmin">


<cfset stText.Security.accessWrite="Schreibzugriff">
<cfset stText.Security.accessWriteDesc="definiert den Schreibzugriff">

<cfset stText.Security.accessRead="Lesezugriff">
<cfset stText.Security.accessReadDesc="definiert den Lesezugriff">

<cfset stText.Security.FilePath="Verzeichnis">
   <cfset stText.Security.FileCustom="Zusätzliche Verzeichnisse">
   <cfset stText.Security.FileCustomDesc="Sie können zusätzliche Verzeichnisse definieren, wo der Filezugriff erlaubt ist.">