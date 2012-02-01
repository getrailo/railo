start
<cfset version=replace(mid(server.railo.version,1,3),".","-")>
<cfset pw="server">

<cfdump var="#version#">


<cfif version EQ "3-2" or version EQ "3-3" or version EQ "4-0">
	<cfset src="/Users/mic/Projects/Railo/Source2/railo/railo-java/railo-core/">

	<cfset classes="/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_Source2_railo_railo_cfml_railo_admin13565">
	<cfset source="/Users/mic/Projects/Railo/Source2/railo/railo-cfml/railo-admin">
	
	<cfset raFileSrc=src&"src/resource/context/railo-context.ra">
	<cfset raFileBin=src&"bin/resource/context/railo-context.ra">
    
	<cfset trgContextSrc=src&"src/resource/context/">
	<cfset trgContextBin=src&"bin/resource/context/">
    
<cfelseif version EQ "3-1">
	<cfset src="/Users/mic/Projects/Railo/Source2/railo/railo-java/railo-core/">

	<cfset classes="/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_webroot_context314653">
	<cfset source="/Users/mic/Projects/Railo/webroot/context31">
	
	<cfset raFileSrc=src&"src/resource/context/railo-context.ra">
	<cfset raFileBin=src&"bin/resource/context/railo-context.ra">
    
	<cfset trgContextSrc=src&"src/resource/context/">
	<cfset trgContextBin=src&"bin/resource/context/">
<cfelseif version EQ "3-0">
	<cfset classes="/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_webroot_context304653">
	<cfset source="/Users/mic/Projects/Railo/webroot/context30">
	<cfset raFileSrc="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/src/resource/context/railo-context.ra">
	<cfset raFileBin="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/bin/resource/context/railo-context.ra">
	<cfset trgContextSrc="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/src/resource/context/">
	<cfset trgContextBin="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/bin/resource/context/">
<cfelseif version EQ "2-0">
	<cfset classes="/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_webroot_context114653">
	<cfset source="/Users/mic/Projects/Railo/webroot/context11">
	<cfset raFile="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/bin/resource/context/railo-context.ra">
	<cfset trgContext="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/bin/resource/context/">
<cfelse>
	<cfset classes="/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_webroot_context104653">
	<cfset source="/Users/mic/Projects/Railo/webroot/context10">
	<cfset raFile="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/resource/context/railo-context.ra">
	<cfset trgContext="/Users/mic/Projects/Railo/Source/Railo-#version#-Core/resource/context/">
</cfif>

<cfset targetFolder="/Users/mic/Projects/Railo/temp/">


<cfoutput>#source#</cfoutput>
<cfabort>




<cfset dirs="/,admin/resources/language,admin/cdriver,gateway,admin/gdriver,admin/dbdriver,admin/dbdriver/types,templates/error,templates/display,admin/plugin,admin/plugin/Note,admin/plugin/DDNS">

<cfif version GTE "4-0">
	<cfset dirs&=",admin/debug">
<cfelse>
	<cfset dirs&=",templates/debugging">
</cfif>
<!--- 
copy other files --->
<cfoutput><cfloop index="key" list="#dirs#">
	<cfdump var="#source#">
    <cfdirectory directory="#source#/#key#" action="list" name="dir">
	
    <cfloop query="dir">
		<cfif type EQ "file" and name neq ".DS_Store">
        	<cfset ts="#trgContextSrc#/#key#/#name#">
        	<cfset ts=replace(ts,'//','/','all')>
        	<cfset ts=replace(ts,'//','/','all')>
        	
			<cfset tb="#trgContextBin#/#key#/#name#">
        	<cfset tb=replace(tb,'//','/','all')>
        	<cfset tb=replace(tb,'//','/','all')>
        	
			<cfset s="#source#/#key#/#name#">
        	<cfset s=replace(s,'//','/','all')>
        	<cfset s=replace(s,'//','/','all')>
        
			<CFIF FileExists(ts)>
				<CFFILE action="delete" file="#ts#">
			</CFIF>
			<CFIF FileExists(tb)>
				<CFFILE action="delete" file="#tb#">
			</CFIF>
			<cfdump var="#struct(srcExist:#FileExists(s)#,src:'#s#',trgsrc:'#ts#',trgbin:'#tb#')#">
			<cffile action="copy" source="#s#" destination="#ts#">
			<cffile action="copy" source="#s#" destination="#tb#">
		</cfif>
	</cfloop>
<!--- <cfdump var="#dir#"> --->
</cfloop></cfoutput>


<!--- 
Create Mapping --->

<cfif version EQ "3-2" or version EQ "3-3" or version EQ "4-0">
	<cfadmin 
		action="updateMapping"
		type="web"
		password="#pw#"
		
		virtual="/context"
		physical="#source#"
		toplevel="true"
		archive=""
		primary="physical"
		trusted="no">
					<cfadmin 
						action="compileMapping"
						type="web"
						password="#pw#"
						virtual="/context"
						stoponerror="true">

<cfelseif version EQ "3-1">
	<cfadmin 
		action="updateMapping"
		type="web"
		password="#pw#"
		
		virtual="/context"
		physical="/context31"
		toplevel="true"
		archive=""
		primary="physical"
		trusted="no">
					<cfadmin 
						action="compileMapping"
						type="web"
						password="#pw#"
						virtual="/context"
						stoponerror="true">
<cfelseif version EQ "3-0">
	<cfadmin 
		action="updateMapping"
		type="web"
		password="#pw#"
		
		virtual="/context"
		physical="/context30"
		toplevel="true"
		archive=""
		primary="physical"
		trusted="no">
					<cfadmin 
						action="compileMapping"
						type="web"
						password="#pw#"
						virtual="/context"
						stoponerror="true">


<cfelseif version EQ "2-0">
	<cfadmin 
		action="updateMapping"
		type="web"
		password="#pw#"
		
		virtual="/context"
		physical="/context11"
		toplevel="true"
		archive=""
		primary="physical"
		trusted="no">
					<cfadmin 
						action="compileMapping"
						type="web"
						password="#pw#"
						virtual="/context"
						stoponerror="true">

<cfelse>
<cfthrow message="no longer supported for 1.0, uncomment the following lines for support">
<!---
	<cfadmin 
		action="updateMapping"
		type="web"
		password="#pw#"

		virtual="/context"
		physical="/context10"
		archive=""
		primary="physical"
		trusted="no">
<rc:compiler 
	template="/context" 
	recursive="true" 
	onlyChanges="false">
--->
</cfif>


	
	compiled
<!--- 
copy classe s ---> 

<cffunction name="translatePath">
	<cfargument name="path">
    <cfset path=replace(path,classes,'')>
    <cfset path=target&path>
	<cfreturn path>
</cffunction>

<cffunction name="translatePath2">
	<cfargument name="path">
    <cfset path=replace(path,source,'')>
    <cfset path=target&path>
	<cfreturn path>
</cffunction>






<cfset target=targetFolder&"railo-context/">

<cfif not DirectoryExists(target)>
	<cfdirectory directory="#target#" action="create">
</cfif>
<cfset target=target&"tmp/">


<cfif not DirectoryExists(target)>    
    <cfdirectory directory="#target#" action="list" name="tmp" recurse="no">
    <cfloop query="tmp">
        <cfdirectory directory="#target#" action="delete" recurse="yes">
    </cfloop>
</cfif>


			
<cfif not DirectoryExists(target)>
	<cfdirectory directory="#target#" action="create">
</cfif>
<cfset target=target&server.railo.version&"/">
<cfif not DirectoryExists(target)>
	<cfdirectory directory="#target#" action="create">
</cfif>



<!--- create temp dir ---->
<cfdirectory directory="#classes#" action="list" name="dir" recurse="true">
<cfdump var="#classes#" label="CLASSeS" >
<cfloop query="dir">
	<cfif dir.type EQ "dir">
		<cfset path=translatePath(dir.directory)&"/#dir.name#">
        <cfif not DirectoryExists(path) and not FindNoCase("factory",path)>
			<cfdirectory directory="#path#" action="create">
		</cfif>
	</cfif>
</cfloop>

create dirs
<!--- create temp class files ---->
<cfloop query="dir">
	<cfif dir.type EQ "file">
		<cfset path=translatePath(dir.directory)>
		<cfset targetFile="#path#/#dir.name#">
		<cfif FileExists(targetFile)>
			<cffile action="delete" file="#targetFile#">
		</cfif>
		<cfif not FindNoCase("/admin/factory/",targetFile)>
		<cffile action="copy" destination="#targetFile#" source="#dir.directory#/#dir.name#">
        </cfif>
	</cfif>
</cfloop>
create files

<!--- 
copy source --->

<cfdirectory directory="#source#" action="list" name="dir" recurse="true" filter="*.cf*">
<cfflush>

<cfloop query="dir">


	<cfif dir.type EQ "file">
		<cfset path=translatePath2(dir.directory)>
		<cfset targetFile="#path#/#dir.name#">
		<cfif FileExists(targetFile)>
			<cffile action="delete" file="#targetFile#">
		</cfif>
		<cfset d=getDirectoryFromPath(targetFile)>
		<cfif not DirectoryExists(d) and not FindNoCase("factory",d)>>
			<cfdirectory action="create" mode="777" directory="#d#">
		</cfif>
		<cfif not FindNoCase("/admin/factory/",targetFile)>
        	<cfdump var="- #targetFile#">
			<cffile action="copy" destination="#targetFile#" source="#dir.directory#/#dir.name#">
		</cfif>
	</cfif>
	
</cfloop>
copy source fils
<!--- 
compress --->

<cfif isDefined('raFileSrc')>
<cfset compress("zip",target,raFileSrc,false)>
</cfif>

<cfif isDefined('raFileBin')>
<cfset compress("zip",target,raFileBin,false)>
</cfif>

<cfif isDefined('raFile')>
<cfset compress("zip",target,raFile,false)>
</cfif>
compreessed



