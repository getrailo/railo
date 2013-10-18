<cfsetting showdebugoutput="no">

<!--- Upload document --->
<CFMODULE TEMPLATE="FileUpload.cfm" FILEFIELD="ResumeFileName_File"
	TEMPDIR="#ExpandPath("{temp-directory}")#" DESTDIR="#getDirectoryFromPath(getCurrentTemplatePath())#/res/" 
	ALLOWEDFILETYPES="pdf,doc,docx,rtf,txt" LOWERCASEEXT="Yes" MAKEUNIQUE="Yes" OUTPUTTYPE="Struct">
<cfoutput>#serialize(fileUpload)#</cfoutput>
