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
 ---><CFSETTING ENABLECFOUTPUTONLY="Yes">

<CFPARAM NAME="Attributes.FileField" DEFAULT="">
<CFIF IsDefined("Request.PathLevel")>
	<CFPARAM NAME="Attributes.TempDir" DEFAULT="#ExpandPath("#Request.PathLevel#temp")#">
	<CFPARAM NAME="Attributes.DestDir" DEFAULT="#ExpandPath("#Request.PathLevel#files")#">
<CFELSE>
	<CFPARAM NAME="Attributes.TempDir" DEFAULT="#ExpandPath("../temp")#">
	<CFPARAM NAME="Attributes.DestDir" DEFAULT="#ExpandPath("../files")#">
</CFIF>
<CFPARAM NAME="Attributes.TempDirDelim" DEFAULT=",">
<CFPARAM NAME="Attributes.DestDirDelim" DEFAULT=",">
<CFPARAM NAME="Attributes.AllowedFileTypes" DEFAULT="">
<CFPARAM NAME="Attributes.CreateDirectories" DEFAULT="Yes">
<CFPARAM NAME="Attributes.LowerCaseExt" DEFAULT="Yes">
<CFPARAM NAME="Attributes.MakeUnique" DEFAULT="Yes">
<CFPARAM NAME="Attributes.UniqueSuffix" DEFAULT="">
<CFPARAM NAME="Attributes.RemoveChars" DEFAULT="[^.[:alnum:]]">
<CFPARAM NAME="Attributes.ReplaceChar" DEFAULT="_">
<CFPARAM NAME="Attributes.Output" DEFAULT="FileUpload">
<CFPARAM NAME="Attributes.OutputType" DEFAULT="Query">

<!--- validate our input --->
<CFIF Attributes.TempDir IS "" OR Attributes.DestDir IS "">
	<!--- No directories to upload to - this is an error --->
	<CFSET Attributes.FileField = "">
</CFIF>

<CFSET FileUpload = ArrayNew(1)>

<!--- Create a subdirectory name to use within temp that isn't likely to conflict with anything --->
<CFSET Attributes.TempSubDir = "#DateFormat(Now(),"yyyymmdd")##TimeFormat(Now(),"HHmmss")#-#RandRange(100000,999999)#">

	<!--- Define the rest of our struct keys just so they'll exist --->
	<CFSET CurrentFile.Error = "">
	<CFSET CurrentFile.Name = "">
	<CFSET CurrentFile.Ext = "">
	<CFSET CurrentFile.NameNoExt = "">
	<CFSET CurrentFile.Original = "">
	<CFSET CurrentFile.OriginalNoExt = "">
	
	<!--- All everything but the default "DenyAll" in cffileSecurity --->
	<CFMODULE TEMPLATE="cffileSecurity.cfm" ACTION="del" LOCATION="" ERROROUTPUT="CurrentFile.Error">
	<CFIF CurrentFile.Error IS NOT "">
		<cfset caller.caller.exists=true>
	</CFIF>
	<CFSET "Caller.#Attributes.Output#" = FileUpload>

<CFSETTING ENABLECFOUTPUTONLY="No">
