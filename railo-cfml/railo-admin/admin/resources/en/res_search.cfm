<!--- Search --->
<cfset stText.Search.Description           = "Here you can manage, create, populate and delete search collections. By default, railo uses Apache Lucene as the search engine.<br/><br/>">
<cfset stText.Search.Collections           = "Collections">
<cfset stText.Search.Collection            = "Collection">
<cfset stText.Search.Name                  = "Name">
<cfset stText.Search.Missing_Name          = "Please enter a name for the collection">
<cfset stText.Search.Mapped                = "Mapped">
<cfset stText.Search.Online                = "Online">
<cfset stText.Search.External              = "External">
<cfset stText.Search.Language              = "Language">
<cfset stText.Search.Last_Update           = "Last update">
<cfset stText.Search.Path                  = "Path">
<cfset stText.Search.Missing_Path          = "Please enter a valid path for the collection">
<cfset stText.Search.PathAction            = "Add/Update path index">
<cfset stText.Search.FileExtensions        = "File extensions">
<cfset stText.Search.FileExtensionsMissing = "Please enter the file extensions to be indexed">
<cfset stText.Search.DirectoryPath         = "Directory path">
<cfset stText.Search.DirectoryPathMissing  = "Please enter a path to be indexed">
<cfset stText.Search.IndexSubdirs          = "Index subdirectories">
<cfset stText.Search.URL                   = "URL">
<cfset stText.Search.CreateCol             = "Create collection">
<cfset stText.Search.SearchTheCollection   = "Search the collection">
<cfset stText.Search.ResultOfTheSearch     = "Results of the search">
<cfset stText.Search.SearchTerm            = "Enter the searchterm">
<cfset stText.Search.SearchTermMissing     = "Please enter a searchterm">
<cfset stText.Search.Result     = "Results {startrow} - {endrow} of about {recordcount} searched in {recordssearched} Records">
<cfset stText.Search.NoResult     = "No Result for your criteria">

<!--- Search Language Array --->
<cfset stText.SearchLng = Array(
				Array("arabic", "Arabic"),
				Array("simplified_chinese", "Chinese (simplified)"),
				Array("traditional_chinese", "Chinese (traditional)"),
				Array("czech", "Czech"),
				Array("danish", "Danish"),
				Array("dutch", "Dutch"),
				Array("english", "English"),
				Array("finnish", "Finnish"),
				Array("french", "French"),
				Array("german", "German"),
				Array("greek", "Greek"),
				Array("hebrew", "Hebrew"),
				Array("hungarian", "Hungarian"),
				Array("italian", "Italian"),
				Array("japanese", "Japanese"),
				Array("korean", "Korean"),
				Array("norwegian", "Norwegian"),
				Array("bokmal", "Norwegian (Bokmal)"),
				Array("nynorsk", "Norwegian (Nynorsk)"),
				Array("polish", "Polish"),
				Array("portuguese", "Portuguese"),
				Array("russian", "Russian"),
				Array("spanish", "Spanish"),
				Array("swedish", "Swedish"),
				Array("turkish", "Turkish"))>