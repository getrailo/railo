
<cfset srcDir = expandPath( "./" )>
<cfset dstDir = expandPath( "../resources/" )>


<cfloop array="#[ "img", "css", "js" ]#" item="subdir">
		

	<cfdirectory action="list" directory="#srcDir#/#subdir#" name="qFiles">

	<cfloop query="#qFiles#">
		
		<cf_generateres filename="#qFiles.name#" srcDir="#srcDir#" dstDir="#dstDir#">
	</cfloop>
</cfloop>