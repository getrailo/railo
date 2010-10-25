<!--- Components --->
<cfset stText.Components.Component                        = "Komponente">
<cfset stText.Components.Server                           = "Definert Komponenteneinstellungen, die als Standartwert für alle Webs gelten.">
<cfset stText.Components.Web                              = "Definiert wie Komopenten von Railo verarbeitet werden.">

<cfset stText.Components.BaseComponent                    = "Basis/Root Komponente">
<cfset stText.Components.BaseComponentDescription         = "Jede Komponente die nicht explizit eine andere Komponente erbt (Attribut 'extends') erbt diese Komponente, d.h. das jede Komponente diese Komponente direkt oder indirekt erbt.">
<cfset stText.Components.BaseComponentMissing             = "Bitte geben Sie einen Wert für die Basis/Root Komponente ein.">

<cfset stText.Components.AutoImport                    = "Auto Import">
<cfset stText.Components.AutoImportDescription         = "Die folgende package Definition wird für jedes Template automatisch geladen.">
<cfset stText.Components.AutoImportMissing             = "Bitte geben Sie einen Wert für Auto Import ein.">

<cfset stText.Components.ComponentDumpTemplate            = "Komponetenausgabe Template (dump)">
<cfset stText.Components.ComponentDumpTemplateDescription = "Wenn Sie ein Komponente direkt über den Browser aufrufen, wird dieses Template für die Darstellung verwendet. (Beispiel: http://www.railo.ch/ch/railo/common/Example.cfc)">
<cfset stText.Components.ComponentDumpTemplateMissing     = "Bitte geben Sie einen Eintrag für das 'dump' Template an.">
<cfset stText.Components.DataMemberAccessType             = "Zugriffsbeschränkung für Daten Mitglieder einer CFC">
<cfset stText.Components.DataMemberAccessTypeDescription  = "Definiert wie die Variablen des 'this' Scopes (Data Members) ausserhalb einer CFC angesprochen werden können. Eine strenge Objektorientierung würde verlangen, dass ein solcher Zugriff von Aussen gar nicht erst möglich wäre (access=private).">
<cfset stText.Components.DMATPrivate                      = "private">
<cfset stText.Components.DMATPackage                      = "package">
<cfset stText.Components.DMATPublic                       = "public (CFML Standard)">
<cfset stText.Components.DMATRemote                       = "remote">

<cfset stText.Components.Physical = "Ressource">
<cfset stText.Components.Archive  = "Archiv">
<cfset stText.Components.Primary  = "Primär">
<cfset stText.Components.Trusted  = "Trusted">
<cfset stText.Components.PhysicalMissing = "Bitte geben Sie einen Wert für die Ressource an. (Zeile ">
<cfset stText.Components.ArchiveMissing  = "Bitte geben Sie einen Wert für den Archivnamen an. (Zeile ">
<cfset stText.Components.componentMappings="Zusätzliche Resourcen">
<cfset stText.Components.componentMappingsDesc="Zusätzliche Verzeichnisse, welche Railo nach Komponenten überprüft.">


<cfset stText.Components.componentLocalSearch="Lokales Verzeichnis einbeziehen">
<cfset stText.Components.componentLocalSearchDesc="Relativ zum aktuellen Verzeichnis nach einer aufgerufenen Komponente suchen.">
<cfset stText.Components.componentMappingSearch="Mappings einbeziehen">
<cfset stText.Components.componentMappingSearchDesc="Die Komponente wird auch innerhalb der Mappings gesucht, damit wird auch das implizite Mapping ""/"" einbezogen, welches aufs webroot zeigt">


<cfset stText.Components.componentPathCache="Cache">
<cfset stText.Components.componentPathCacheDesc="Kompenten Pfade werden gecached und nicht wieder geprüft">