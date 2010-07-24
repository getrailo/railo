<!--- CFX-Tags --->
<cfset stText.CFX.NoAccess                = "No access to CFX functionality">
<cfset stText.CFX.CFXTags                 = "Java CFX tags">
<cfset stText.CFX.Name                    = "Name">
<cfset stText.CFX.Class                   = "Class">
<cfset stText.CFX.MissingClassValue       = "Please enter a value for the class (row ">

<!--- Custom Tags --->
<cfset stText.CustomTags.Physical = "Resource">
<cfset stText.CustomTags.Archive  = "Archive">
<cfset stText.CustomTags.Primary  = "Primary">
<cfset stText.CustomTags.Trusted  = "Trusted">
<cfset stText.CustomTags.PhysicalMissing = "Please enter a value for the resource (row ">
<cfset stText.CustomTags.ArchiveMissing  = "Please enter a value for the archive name (row ">
<cfset stText.CustomTags.CustomtagMappings="Resources">
<cfset stText.CustomTags.CustomtagMappingsDesc="Directories that Railo checks for Custom Tags.">

<cfset stText.CustomTags.CustomtagSetting="Settings">
<cfset stText.CustomTags.customTagDeepSearch="Search subdirectories">
<cfset stText.CustomTags.customTagDeepSearchDesc="Search for custom tags in subdirectories (not supported for archives)">

<cfset stText.CustomTags.customTagLocalSearch="Search local">
<cfset stText.CustomTags.customTagLocalSearchDesc="Search in the caller directory for the custom tag">


<cfset stText.CustomTags.extensions="Extensions">
<cfset stText.CustomTags.extensionsDesc="These are the extensions used for Custom Tags, in the order they are searched.">

<cfset stText.CustomTags.mode.classic="Classic (Railo 1.0 - 3.0)">
<cfset stText.CustomTags.mode.standard="CFML standard">
<cfset stText.CustomTags.mode.mixed="Mixed">
<cfset stText.CustomTags.mode.modern="Modern">
<cfset stText.CustomTags.mode.custom="Custom">

<!--- Mappings --->
<cfset stText.Mappings.Physical                = "Resource">
<cfset stText.Mappings.Archive                 = "Archive">
<cfset stText.Mappings.IntroText               = "Please note, that only pages processed by Railo are aware of these mappings (cfm, cfml, cfc). If you want to use files not processed by Railo for these special mapping directories, you have to add virtual mappings to these directories to your application server.">
<cfset stText.Mappings.VirtualHead             = "Virtual">
<cfset stText.Mappings.PhysicalHead            = "Resource">
<cfset stText.Mappings.ArchiveHead             = "Archive">
<cfset stText.Mappings.PrimaryHead             = "Primary">
<cfset stText.Mappings.TrustedHead             = "Trusted">
<cfset stText.Mappings.PhysicalHeadDescription = "Path of the resource to map (absolute or relative to the webroot)">
<cfset stText.Mappings.ArchiveHeadDescription  = "Name of the archive file (ra or ras), (absolute or relative to the webroot)">
<cfset stText.Mappings.PrimaryHeadDescription  = "Sets where Railo-files will first be searched">
<cfset stText.Mappings.TrustedHeadDescription  = "">
<cfset stText.Mappings.PhysicalMissing         = "Please enter a value for the resource (row ">
<cfset stText.Mappings.ArchiveMissing          = "Please enter a value for the archive name (row ">

<cfset stText.Mappings.archiveTitle = "create archive">
<cfset stText.Mappings.archiveDesc = "Generate a Railo archive (ra) from an existing mapping">
<cfset stText.Mappings.archiveSecure = "Secured">
<cfset stText.Mappings.archiveSecureDesc = "Exclude source files from archive. Source files are only used for codeprints in case of an error">

<cfset stText.Buttons.downloadArchive="download archive">
<cfset stText.Buttons.addArchive="assign archive to mapping">


<cfset stText.Mappings.editDesc = "Here you can edit a certain mapping or create a Railo archive out of an existing one.">
<cfset stText.Mappings.compileTitle = "compile">
<cfset stText.Mappings.compileDesc = "Compile all cfm and cfc files inside the mapping">
<cfset stText.Mappings.compileStopOnError = "Stop on error">
<cfset stText.Mappings.compileStopOnErrorDesc = "Sets whether the compile process should be aborted on errors">


<cfset stText.Mappings.ToplevelHead = "Top Level">
<cfset stText.Mappings.TopLevelDesc = "Make this mapping web-accessible (i.e., accessible via a browser or  
remote HTTP call)">