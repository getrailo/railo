<!--- Search --->
<cfset stText.Search.Description           = "Hier können Sie Search Collections verwalten, erstellen, indexieren und löschen. Standardmässig verwendet Railo LUCENE als Suchengine.">
<cfset stText.Search.Collections           = "Collections">
<cfset stText.Search.Collection            = "Collection">
<cfset stText.Search.Name                  = "Name">
<cfset stText.Search.Missing_Name          = "Bitte geben Sie einen Wert für den Namen der Collection an.">
<cfset stText.Search.Mapped                = "Mapped">
<cfset stText.Search.Online                = "Online">
<cfset stText.Search.External              = "Extern">
<cfset stText.Search.Language              = "Sprache">
<cfset stText.Search.Last_Update           = "Letzte Aktualisierung">
<cfset stText.Search.Path                  = "Pfad">
<cfset stText.Search.Missing_Path          = "Bitte geben Sie einen gültigen Wert für den Pfad der Collection an.">
<cfset stText.Search.PathAction            = "Hinzufügen/Aktualisieren des Pfadindex">
<cfset stText.Search.FileExtensions        = "Dateierweiterungen">
<cfset stText.Search.FileExtensionsMissing = "Bitte geben Sie einen Wert für Erweiterungen die indexiert werden sollen an.">
<cfset stText.Search.DirectoryPath         = "Verzeichnispfad">
<cfset stText.Search.DirectoryPathMissing  = "Bitte geben Sie einen Wert für den zu indexierenden Pfad an.">
<cfset stText.Search.IndexSubdirs          = "Unterverzeichnisse einschliessen">
<cfset stText.Search.URL                   = "URL">
<cfset stText.Search.CreateCol             = "Collection erstellen">
<cfset stText.Search.SearchTheCollection   = "Collection durchsuchen">
<cfset stText.Search.ResultOfTheSearch     = "Ergebnisse der Suche">
<cfset stText.Search.SearchTerm            = "Suchbegriff eingeben">
<cfset stText.Search.SearchTermMissing     = "Bitte geben Sie einen Suchbegriff ein.">
<cfset stText.Search.Result     = "Resultate {startrow} - {endrow} von {recordcount} Resultaten gesucht in {recordssearched} Records">
<cfset stText.Search.NoResult     = "Kein Resultat für Ihre Anfrage">

<!--- Search Language Array --->
<cfset stText.SearchLng = Array(
				Array("arabic", "Arabisch"),
				Array("simplified_chinese", "Chinesisch (vereinfacht)"),
				Array("traditional_chinese", "Chinesisch (traditionell)"),
				Array("czech", "Tschechisch"),
				Array("danish", "Dänisch"),
				Array("dutch", "Holländisch"),
				Array("english", "Englisch"),
				Array("finnish", "Finnisch"),
				Array("french", "Französisch"),
				Array("german", "Deutsch"),
				Array("greek", "Griechisch"),
				Array("hebrew", "Hebräisch"),
				Array("hungarian", "Ungarisch"),
				Array("italian", "Italienisch"),
				Array("japanese", "Japanisch"),
				Array("korean", "Koreanisch"),
				Array("norwegian", "Norwegisch"),
				Array("bokmal", "Norwegisch (Bokmal)"),
				Array("nynorsk", "Norwegisch (Nynorsk)"),
				Array("polish", "Polnisch"),
				Array("portuguese", "Portugesisch"),
				Array("russian", "Russisch"),
				Array("spanish", "Spanisch"),
				Array("swedish", "Schwedisch"),
				Array("turkish", "Türkisch"))>