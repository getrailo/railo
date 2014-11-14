<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.resultDir=getDirectoryFromPath(getCurrentTemplatePath())&"FileTest/test/res/">
		<cfset variables.resDir=getDirectoryFromPath(getCurrentTemplatePath())&"FileTest/resources/">
		<cfset variables.resPDFFile=resDir&'res.pdf'>
		<cfset variables.resPNGFile=resDir&'res.png'>
		<cfset variables.targetURL=createURL("FileTest/test/act_UpdateReviewer.cfm")>
		
		<cfif !directoryExists(variables.resultDir)>
			<cfdirectory action="create" directory="#variables.resultDir#">
		</cfif>
	</cffunction>
	
	<cffunction name="afterTests">
		<cfdirectory action="delete" directory="#variables.resultDir#" recurse="true">
	</cffunction>
	
	
	<cffunction name="testSucessfullFileUpload" output="yes">
		
		<cfhttp method="post" multipart="yes" result="local.result" url="#variables.targetURL#" addtoken="false">
			<cfhttpparam type="file" 
						name="ResumeFileName_File" 
						file="#variables.resPDFFile#">
		</cfhttp>
		<cfset assertEquals(200,result.status_code)>
		<cfset fileUpload=evaluate(trim(result.filecontent))>
		
		<cfset assertEquals("",fileUpload.error)>
		<cfset assertEquals("PDF",fileUpload.ext)>
		<cfset assertEquals("ResumeFileName_File",fileUpload.field)>
		<cfset assertEquals(true,fileExists(fileUpload.destdir&fileupload.name))>
	</cffunction>
	
	
	<cffunction name="testFailingFileUpload" output="yes">
		
		<cfhttp method="post" multipart="yes" result="local.result" url="#variables.targetURL#" addtoken="false">
			<cfhttpparam type="file" 
						name="ResumeFileName_File" 
						file="#variables.resPNGFile#">
		</cfhttp>
		<cfset assertEquals(200,result.status_code)>
		<cfset fileUpload=evaluate(trim(result.filecontent))>
		<cfset assertEquals("<br>file extension not allowed or doesn't exist",fileUpload.error)>
		<cfset assertEquals("PNG",fileUpload.ext)>
		<cfset assertEquals("ResumeFileName_File",fileUpload.field)>
		<cfset assertEquals(false,fileExists(fileUpload.destdir&fileupload.name))>
	</cffunction>


<cfscript>
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
</cfscript>

</cfcomponent>