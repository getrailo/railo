<!--- CFX-Tags --->
<cfset stText.CFX.NoAccess                = "Kein Zugriff auf die CFX Funktionalität">
<cfset stText.CFX.CFXTags                 = "Java CFX Tags">
<cfset stText.CFX.Name                    = "Name">
<cfset stText.CFX.Class                   = "Klasse">
<cfset stText.CFX.MissingClassValue       = "Bitte geben Sie einen Wert für die Klasse an. (Zeile ">

<!--- Custom Tags --->
<cfset stText.CustomTags.Physical = "Ressource">
<cfset stText.CustomTags.Archive  = "Archiv">
<cfset stText.CustomTags.Primary  = "Primär">
<cfset stText.CustomTags.Trusted  = "Trusted">
<cfset stText.CustomTags.PhysicalMissing = "Bitte geben Sie einen Wert für die Ressource an. (Zeile ">
<cfset stText.CustomTags.ArchiveMissing  = "Bitte geben Sie einen Wert für den Archivnamen an. (Zeile ">
<cfset stText.CustomTags.CustomtagMappings="Resourcen">
<cfset stText.CustomTags.CustomtagMappingsDesc="Verzeichnisse, welche Railo nach Custom tags überprüft.">
<cfset stText.CustomTags.CustomtagSetting="Einstellungen">
<cfset stText.CustomTags.customTagDeepSearch="Untergeordnete Verzeichnisse einbeziehen">
<cfset stText.CustomTags.customTagDeepSearchDesc="Untergeordnete Verzeichnisse nach Customtags durchsuchen (für Archive nicht unterstützt)">
<cfset stText.CustomTags.customTagLocalSearch="Lokales Verzeichnis einbeziehen">
<cfset stText.CustomTags.customTagLocalSearchDesc="Lokales Verzeichnisse aus welchen der Customtag aufgerufen wird, nach dem Customtag durchsuchen">

<cfset stText.CustomTags.customTagPathCache="Cache">
<cfset stText.CustomTags.customTagPathCacheDesc="Customtag Pfade werden gecached und nicht wieder geprüft">


<cfset stText.CustomTags.extensions="Extensions">
<cfset stText.CustomTags.extensionsDesc="Dies sind die Extensions welche für Customtags verwendet werden und die Rheinefolge in welcher sie gesucht werden.">

<cfset stText.CustomTags.mode.classic="Classic (Railo 1.0 - 3.0)">
<cfset stText.CustomTags.mode.standard="CFML standard">
<cfset stText.CustomTags.mode.mixed="Mixed">
<cfset stText.CustomTags.mode.modern="Modern">
<cfset stText.CustomTags.mode.custom="Custom">

<!--- Mappings --->
<cfset stText.Mappings.Physical                = "Ressource">
<cfset stText.Mappings.Archive                 = "Archiv">
<cfset stText.Mappings.IntroText               = "Bitte beachten Sie, dass nur Seiten, die von Railo verarbeitet werden diese Mappings kennen (cfm, cfml, cfc). Wenn Sie auch Dateien, die nicht von Railo verarbeitet werden verwenden möchten, müssen Sie virtuelle Mappings auf Ihrem Applikationsserver erstellen.">
<cfset stText.Mappings.VirtualHead             = "Virtuell">
<cfset stText.Mappings.PhysicalHead            = "Ressource">
<cfset stText.Mappings.ArchiveHead             = "Archiv">
<cfset stText.Mappings.PrimaryHead             = "Primär">
<cfset stText.Mappings.TrustedHead             = "Trusted">
<cfset stText.Mappings.PhysicalHeadDescription = "Pfad der zu mappenden Ressource (Absolut oder relativ zum Webroot).">
<cfset stText.Mappings.ArchiveHeadDescription  = "Name der Archivdatei (ra oder ras)  (Absolut oder relativ zum Webroot).">
<cfset stText.Mappings.PrimaryHeadDescription  = "Legt fest, wo cfm-Dateien vorrangig gesucht werden.">
<cfset stText.Mappings.TrustedHeadDescription  = "">
<cfset stText.Mappings.PhysicalMissing         = "Bitte geben Sie einen Wert für die Ressource an. (Zeile ">
<cfset stText.Mappings.ArchiveMissing          = "Bitte geben Sie einen Wert für den Archivnamen an. (Zeile ">

<cfset stText.Mappings.archiveTitle = "Archiv erstellen">
<cfset stText.Mappings.archiveDesc = "Erstellen Sie aus einem Mapping ein Railo Archive (ra)">
<cfset stText.Mappings.archiveSecure = "Sicher">
<cfset stText.Mappings.archiveSecureDesc = "Source Dateien aus dem Archiv ausschliessen. Die Source Dateien werden nur für die Ausgabe von Codeprints bei Fehlern verwendet.">

<cfset stText.Buttons.downloadArchive="downloaden">
<cfset stText.Buttons.addArchive="dem Mapping zuweisen">

<cfset stText.Mappings.editDesc = "Hier können Sie ein einzelnes Mapping bearbeiten oder aus einem bestehenden Mapping ein Railo Archiv erstellen.">
<cfset stText.Mappings.compileTitle = "kompilieren">
<cfset stText.Mappings.compileDesc = "Kompilieren Sie alle cfm und cfc-Dateien innerhalb des Mappings">
<cfset stText.Mappings.compileStopOnError = "Bei Fehler stoppen">
<cfset stText.Mappings.compileStopOnErrorDesc = "Legt fest, ob bei einem Fehler die weitere Kompilierung gestoppt werden soll">


<cfset stText.Mappings.ToplevelHead = "Top Level">
<cfset stText.Mappings.TopLevelDesc = "Macht dieses Mapping zugreifbar übers Web (Zugreifbar über Browser oder andere HTTP Zugriffe)">