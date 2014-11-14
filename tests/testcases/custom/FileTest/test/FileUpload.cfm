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

<!--- FileUpload.cfm --->

<!--- I upload a file or batch of files to a temp directory, and then to its final destination directory. I can check to see if it
matches a list of allowed file types, if it has a unique filename, etc. I return a struct (or an array of structs) containing details
about the file(s) uploaded. --->

<!--- params --->
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

<!--- Are directory lists long enough to handle file field lists? --->
<CFLOOP CONDITION="ListLen(Attributes.FileField) GT ListLen(Attributes.TempDir,Attributes.TempDirDelim)">
	<CFSET Attributes.TempDir = ListAppend(Attributes.TempDir,ListLast(Attributes.TempDir,Attributes.TempDirDelim),Attributes.TempDirDelim)>
</CFLOOP>
<CFLOOP CONDITION="ListLen(Attributes.FileField) GT ListLen(Attributes.DestDir,Attributes.DestDirDelim)">
	<CFSET Attributes.DestDir = ListAppend(Attributes.DestDir,ListLast(Attributes.DestDir,Attributes.DestDirDelim),Attributes.DestDirDelim)>
</CFLOOP>

<!--- Do we have a specific subset of file types? --->
<CFIF ListFindNoCase("image,images",Attributes.AllowedFileTypes)>
	<CFSET Attributes.AllowedFileTypes = "jpg,jpeg,gif,tif,tiff,bmp,png">
<CFELSEIF ListFindNoCase("movie,movies,video,videos",Attributes.AllowedFileTypes)>
	<CFSET Attributes.AllowedFileTypes = "rm,qt,mov,mpeg,mpg,mp4,avi,wmv">
<CFELSEIF ListFindNoCase("audio,sound,sounds",Attributes.AllowedFileTypes)>
	<CFSET Attributes.AllowedFileTypes = "mp3,aif,aiff,wav,ram,wma">
<CFELSEIF ListFindNoCase("all,any",Attributes.AllowedFileTypes)>
	<CFSET Attributes.AllowedFileTypes = "">
</CFIF>

<CFSET FileUpload = ArrayNew(1)>

<!--- Create a subdirectory name to use within temp that isn't likely to conflict with anything --->
<CFSET Attributes.TempSubDir = "#DateFormat(Now(),"yyyymmdd")##TimeFormat(Now(),"HHmmss")#-#RandRange(100000,999999)#">

<CFLOOP FROM="1" TO="#ListLen(Attributes.FileField)#" INDEX="f">
	<!--- Define a struct to track our progress with this file --->
	<CFSET CurrentFile = StructNew()>
	
	<!--- Declare "current" copies of these variables so we don't have to keep calling list items --->
	<CFSET CurrentFile.Field = ListGetAt(Attributes.FileField,f)>
	<CFSET CurrentFile.TempDir = ListGetAt(Attributes.TempDir,f,Attributes.TempDirDelim)>
	<CFSET CurrentFile.TempSubDir = Attributes.TempSubDir>
	<CFSET CurrentFile.DestDir = ListGetAt(Attributes.DestDir,f,Attributes.DestDirDelim)>
	<CFSET CurrentFile.Error = "">
	
	<!--- What slash are we using? Make sure we have a trailing slash after the directory --->
	<CFIF Find("/",CurrentFile.TempDir)>
		<CFIF Right(CurrentFile.TempDir,1) IS NOT "/">
			<CFSET CurrentFile.TempDir = "#CurrentFile.TempDir#/">
		</CFIF>
		<CFSET TempSlash = "/">
	<CFELSE>
		<CFIF Right(CurrentFile.TempDir,1) IS NOT "\">
			<CFSET CurrentFile.TempDir = "#CurrentFile.TempDir#\">
		</CFIF>
		<CFSET TempSlash = "\">
	</CFIF>
	<CFIF Find("/",CurrentFile.DestDir)>
		<CFIF Right(CurrentFile.DestDir,1) IS NOT "/">
			<CFSET CurrentFile.DestDir = "#CurrentFile.DestDir#/">
		</CFIF>
		<CFSET DestSlash = "/">
	<CFELSE>
		<CFIF Right(CurrentFile.DestDir,1) IS NOT "\">
			<CFSET CurrentFile.DestDir = "#CurrentFile.DestDir#\">
		</CFIF>
		<CFSET DestSlash = "\">
	</CFIF>
	
	<!--- Define the rest of our struct keys just so they'll exist --->
	<CFSET CurrentFile.Error = "">
	<CFSET CurrentFile.Name = "">
	<CFSET CurrentFile.Ext = "">
	<CFSET CurrentFile.NameNoExt = "">
	<CFSET CurrentFile.Original = "">
	<CFSET CurrentFile.OriginalNoExt = "">
	
	<!--- Do we need to create these directories? --->
	<CFIF Attributes.CreateDirectories IS NOT "No">
		<CFIF DirectoryExists("#CurrentFile.TempDir##CurrentFile.TempSubDir#") IS 0>
			<CFSET CreateError = "">
			<CFTRY>
				<CFDIRECTORY ACTION="Create" DIRECTORY="#CurrentFile.TempDir##CurrentFile.TempSubDir#" MODE="777">
				<CFCATCH TYPE="Any"><CFSET CurrentFile.Error = ListAppend(CurrentFile.Error,CFCATCH.Message,";")></CFCATCH>
			</CFTRY>
		</CFIF>
		<CFIF DirectoryExists("#CurrentFile.DestDir#") IS 0>
			<CFSET CreateError = "">
			<CFTRY>
				<CFDIRECTORY ACTION="Create" DIRECTORY="#CurrentFile.DestDir#" MODE="777">
				<CFCATCH TYPE="Any"><CFSET CurrentFile.Error = ListAppend(CurrentFile.Error,CFCATCH.Message,";")></CFCATCH>
			</CFTRY>
		</CFIF>
	</CFIF>
	
	<!--- Now do our directories exist? --->
	<CFIF DirectoryExists("#CurrentFile.TempDir##CurrentFile.TempSubDir#") AND 
		DirectoryExists(CurrentFile.DestDir) AND CurrentFile.Error IS "">
		<!--- Does the file actually exist? --->
		<CFIF IsDefined("Caller.Form.#CurrentFile.Field#") AND FileExists(Evaluate("Caller.Form.#CurrentFile.Field#"))>
			<!--- <CFFILE ACTION="Read" FILE="#Evaluate("Caller.Form.#CurrentFile.Field#")#" VARIABLE="CurrentFileData"> --->
			<!---- <CFIF Len(CurrentFileData) GT 0> --->
				<!--- upload file to server --->
				<CFSET "Form.#CurrentFile.Field#" = Evaluate("Caller.Form.#CurrentFile.Field#")>
				<CFFILE ACTION="Upload" DESTINATION="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash#" FILEFIELD="#CurrentFile.Field#" NAMECONFLICT="MakeUnique">
				<CFSET CurrentFile.Original = CFFILE.ClientFile>
				<CFSET CurrentFile.OriginalNoExt = CFFILE.ClientFileName>
				<CFSET CurrentFile.Ext = CFFILE.ServerFileExt>
				<!--- Here's where we need to call the file security module --->
				<CFIF ListLen(Attributes.AllowedFileTypes) GT 0>
					<!--- Allow the specific file types listed --->
					<CFMODULE TEMPLATE="cffileSecurity.cfm" ALLOWONLY="#Attributes.AllowedFileTypes#" 
						LESSDENYALL="#Attributes.AllowedFileTypes#" ACTION="del" LOCATION=""
						ERROROUTPUT="CurrentFile.Error">
				<CFELSE>
					<!--- All everything but the default "DenyAll" in cffileSecurity --->
					<CFMODULE TEMPLATE="cffileSecurity.cfm" ACTION="del" LOCATION=""
						ERROROUTPUT="CurrentFile.Error">
				</CFIF>
				<CFIF CurrentFile.Error IS NOT "">
					<!--- Security module quarantined the file for us, but we need to get rid of the subdirectory of our temp 
						directory --->
					<CFDIRECTORY ACTION="Delete" DIRECTORY="#CurrentFile.TempDir##CurrentFile.TempSubDir#">
				<CFELSE>
					<CFSET CurrentFile.NameNoExt = CFFILE.ServerFileName>
					<!--- Do we need to make the extension lowercase? --->
					<CFIF Attributes.LowerCaseExt IS "Yes">
						<CFFILE ACTION="Rename" SOURCE="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash##CurrentFile.NameNoExt#.#CurrentFile.Ext#"
							DESTINATION="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash##CurrentFile.NameNoExt#.#LCase(CurrentFile.Ext)#">
						<CFSET CurrentFile.Ext = LCase(CurrentFile.Ext)>
					</CFIF>
					
					<!--- Do we need to remove/replace characters? --->
					<CFIF Attributes.RemoveChars IS NOT "">
						<CFSET CurrentFile.NameNoExt = REReplace(CurrentFile.NameNoExt,Attributes.RemoveChars,Attributes.ReplaceChar,"All")>
					</CFIF>
					
					<!--- Is the filename unique? Do we care? --->
					<CFSET UniqueFileNum = "">
					<CFIF Attributes.MakeUnique IS "Yes">
						<!--- are we tacking on a suffix? --->
						<CFIF FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt#.#CurrentFile.Ext#")
							AND Attributes.UniqueSuffix IS NOT "">
							<!--- <CFIF FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt##Attributes.UniqueSuffix#.#CurrentFile.Ext#")> --->
								<!--- keep tacking on new numbers to the filename, to avoid overwriting an existing file --->
								<CFSET UniqueFileNum = 1>
								<CFSET UniqueFileNameExists = FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt##Attributes.UniqueSuffix##UniqueFileNum#.#CurrentFile.Ext#")>
								<CFLOOP CONDITION = "UniqueFileNameExists">
									<CFSET UniqueFileNum = (UniqueFileNum + 1)>
									<CFSET UniqueFileNameExists = FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt##Attributes.UniqueSuffix##UniqueFileNum#.#CurrentFile.Ext#")>
								</CFLOOP>
							<!--- </CFIF> --->
							
							<!--- Rename file --->
							<CFFILE ACTION="Move" SOURCE="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash##CurrentFile.OriginalNoExt#.#CurrentFile.Ext#"
								DESTINATION="#CurrentFile.DestDir##CurrentFile.NameNoExt##Attributes.UniqueSuffix##UniqueFileNum#.#CurrentFile.Ext#">
							
							<!--- Assign the rest of our struct variables --->
							<CFSET CurrentFile.Name = "#CurrentFile.NameNoExt##Attributes.UniqueSuffix##UniqueFileNum#.#CurrentFile.Ext#">
							<CFSET CurrentFile.Ext = "#CurrentFile.Ext#">
							<CFSET CurrentFile.NameNoExt = "#CurrentFile.NameNoExt##Attributes.UniqueSuffix##UniqueFileNum#">
						<CFELSE>
							<CFIF FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt#.#CurrentFile.Ext#")>
								<!--- keep tacking on new numbers to the filename, to avoid overwriting an existing file --->
								<CFSET UniqueFileNum = 1>
								<CFSET UniqueFileNameExists = FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt##UniqueFileNum#.#CurrentFile.Ext#")>
								<CFLOOP CONDITION = "UniqueFileNameExists">
									<CFSET UniqueFileNum = (UniqueFileNum + 1)>
									<CFSET UniqueFileNameExists = FileExists("#CurrentFile.DestDir##CurrentFile.OriginalNoExt##UniqueFileNum#.#CurrentFile.Ext#")>
								</CFLOOP>
							</CFIF>
							
							<!--- Rename file --->
							<CFFILE ACTION="Move" SOURCE="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash##CurrentFile.OriginalNoExt#.#CurrentFile.Ext#"
								DESTINATION="#CurrentFile.DestDir##CurrentFile.NameNoExt##UniqueFileNum#.#CurrentFile.Ext#">
							
							<!--- Assign the rest of our struct variables --->
							<CFSET CurrentFile.Name = "#CurrentFile.NameNoExt##UniqueFileNum#.#CurrentFile.Ext#">
							<CFSET CurrentFile.Ext = "#CurrentFile.Ext#">
							<CFSET CurrentFile.NameNoExt = "#CurrentFile.NameNoExt##UniqueFileNum#">
						</CFIF>
					<CFELSE>
						<!--- Rename file --->
						<CFFILE ACTION="Move" SOURCE="#CurrentFile.TempDir##CurrentFile.TempSubDir##TempSlash##CurrentFile.OriginalNoExt#.#CurrentFile.Ext#"
							DESTINATION="#CurrentFile.DestDir##CurrentFile.NameNoExt#.#CurrentFile.Ext#">
						
						<!--- Assign the rest of our struct variables --->
						<CFSET CurrentFile.Name = "#CurrentFile.NameNoExt#.#CurrentFile.Ext#">
						<CFSET CurrentFile.Ext = "#CurrentFile.Ext#">
						<CFSET CurrentFile.NameNoExt = "#CurrentFile.NameNoExt#">
					</CFIF>
					
					<!--- Get rid of the subdirectory of our temp directory --->
					<CFDIRECTORY ACTION="Delete" DIRECTORY="#CurrentFile.TempDir##CurrentFile.TempSubDir#">
				</CFIF>
			<!--- </CFIF> --->
		</CFIF>
	</CFIF>
	<!--- Add the struct to our array --->
	<CFSET FileUpload[f] = CurrentFile>
</CFLOOP>

<!--- Did we upload anything at all? --->
<CFIF ArrayLen(FileUpload) IS 0>
	<CFSET FileUpload[1] = StructNew()>
	<CFSET FileUpload[1].Field = "">
	<CFSET FileUpload[1].Name = "">
	<CFSET FileUpload[1].Ext = "">
	<CFSET FileUpload[1].NameNoExt = "">
	<CFSET FileUpload[1].Original = "">
	<CFSET FileUpload[1].OriginalNoExt = "">
	<CFSET FileUpload[1].TempDir = "">
	<CFSET FileUpload[1].DestDir = "">
	<CFSET FileUpload[1].Error = "No files were uploaded. Make sure that FILEFIELD is not an empty string, and that TEMPDIR and DESTDIR, if declared, are not empty.">
</CFIF>

<!--- What output type do we want? --->
<CFIF Attributes.OutputType IS "Array">
	<!--- Array --->
	<CFSET "Caller.#Attributes.Output#" = FileUpload>
<CFELSEIF Attributes.OutputType IS "Struct">
	<!--- Struct - Only contains information on the first file! --->
	<CFSET "Caller.#Attributes.Output#" = FileUpload[1]>
<CFELSE>
	<!--- Default: Query --->
	<CFSET qryFileUpload = QueryNew(StructKeyList(FileUpload[1]))>
	<CFLOOP FROM="1" TO="#ArrayLen(FileUpload)#" INDEX="f">
		<CFSET t = QueryAddRow(qryFileUpload)>
		<CFLOOP LIST="#StructKeyList(FileUpload[1])#" INDEX="ColumnName">
			<CFSET t = QuerySetCell(qryFileUpload,ColumnName,Evaluate("FileUpload[#f#].#ColumnName#"),f)>
		</CFLOOP>
	</CFLOOP>
	
	<CFSET "Caller.#Attributes.Output#" = qryFileUpload>
</CFIF>

<CFSETTING ENABLECFOUTPUTONLY="No">
