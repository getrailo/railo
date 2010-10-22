<cfdirectory directory="../factory/img" action="list" name="imgs">
<cfset bins=struct()>
<cfloop query="imgs">
	<cfif findnoCase(".png",imgs.name) or findnoCase(".gif",imgs.name) or findnoCase(".swf",imgs.name) >
		<cffile action="readbinary" file="../factory/img/#imgs.name#" variable="data">
		<cfset data=toBase64(data)>
		
	
	<cfdump eval="expandPath('../resources/img/#imgs.name#')">
		<cffile action="write" addnewline="no" file="../resources/img/#imgs.name#.cfm" output="data:image/#right(imgs.name,3)#;base64,#data#" fixnewline="no">
	</cfif>
</cfloop>
