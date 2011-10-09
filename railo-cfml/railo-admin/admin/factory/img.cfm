<cfdirectory directory="../factory/img" action="list" name="imgs">
<cfset bins=struct()>
<cfset mimetypes={png:'png',gif:'gif'}>
<cfset base64types={png:'png',gif:'gif'}>
<cfloop query="imgs">
	<cfif findnoCase(".png",imgs.name) or findnoCase(".gif",imgs.name) or findnoCase(".swf",imgs.name) >
		<cffile action="readbinary" file="../factory/img/#imgs.name#" variable="data">
		<cfset data=toBase64(data)>
		<cfset ext=listLast(imgs.name,'.')>
	
	<cfdump eval="expandPath('../resources/img/#imgs.name#')">
		<cffile 
        	action="write" 
            addnewline="no" 
            file="../resources/img/#imgs.name#.cfm" 
            output="#chr(60)#cfsavecontent variable=""c"">#data##chr(60)#/cfsavecontent>#chr(60)#cfoutput>#chr(60)#cfif getBaseTemplatePath() EQ getCurrentTemplatePath()>#chr(60)#cfcontent type=""image/#mimetypes[ext]#"" variable=""##toBinary(c)##"">#chr(60)#cfsetting showdebugoutput=""no"">#chr(60)#cfelse>data:image/#mimetypes[ext]#;base64,##c###chr(60)#/cfif>#chr(60)#/cfoutput>" 
            fixnewline="no">
	</cfif>
</cfloop>


