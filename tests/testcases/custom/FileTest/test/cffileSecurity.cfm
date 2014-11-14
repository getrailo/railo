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
 ---><cfsetting enablecfoutputonly="yes">

<cfset thisVersion = "1.10">
<cfset thisDate = "2010-10-06">


<!---  EXAMPLE USAGE

<cfmodule template="cffileSecurity.cfm" MailTo="EMAIL_ADDRESS@jpl.nasa.gov"

   example uses of optional parameters...
		location="404.html"
		
		allowOnly="images,txt" 
		REallowOnly=".gif$,.jpg$,.jpeg$" 
		
		denyAll="doc,pdf,garbage"  
		REdenyAll=".asp$,.com$,viagra"  
		lessREdenyAll="cfm"
		
		badWords="sysTable"
		REbadWords="sysTable$"
		lessREbadWords="cmd.exe"

		checkFileContents="no"
		
		action="delete"  (other equivalent actions = del, rem, remove = sends email, deletes file from system)
		action="allow"   (other equivalent actions = report, ignore = sends email, but allows continued processing)
						 (default, and none of the above will default to "quarantine" = sends email, renames file with .quarantine)
		
		email2MailTo="no"
		email2Security="no"
>
--->




<cfset reject = "no">

<!--- turns on debugging output, also goes to next page without a cflocation so that output can be seen --->
<cfparam name="attributes.IS_debug" default="no"> 
<!--- if there is an "incident" where do I sent the offender? --->
<cfparam name="attributes.location" default="index.cfm">
<!--- the email (or list of emails) of the developer/lead responsible for reviewing possible false postitives --->
<cfparam name="attributes.MailTo" default="[NULL]">
					
<cfif attributes.MailTo eq "[NULL]">
	<!--- if MailTo is not defined, attempt to get one from application variable --->
	<cfif structKeyExists(application,"sendEmailError")>
		<cfset attributes.MailTo = application.sendEmailError>
	</cfif>

</cfif>

<!--- unless under attack and being swamped, do not turn these two values off --->
<cfparam name="attributes.email2MailTo" default="yes"><!--- stops sending email to developer --->
<cfparam name="attributes.email2Security" default="yes"><!--- stops sending email to security --->


<!--- BEGIN local variable, unable to be overwritten by same named attributes variable --->
	<cfparam name="allowOnly" default=""> 
	<cfparam name="REallowOnly" default="">
	<cfparam name="denyAll" default="exe,cfm,zip,pl,cgi,asp,jsp,php,js,com,dll,shtml,htm,html">
	<cfparam name="REdenyAll" default="">
	<cfparam name="badWords" default="cmd.exe">
	<cfparam name="REbadWords" default="">
<!--- END local variable, unable to be overwritten by same named attributes variable --->

<cfset reason = ""><!--- used when sending email to security/developer --->

<!--- BEGIN user defined optional parameters --->
	<cfparam name="attributes.checkFileContents" default="yes">
	<cfparam name="attributes.action" default="rename"><!--- mid level security, doesn't pass through, but doesn't delete either --->
	<cfparam name="attributes.ErrorOutput" default=""><!--- can pass back an error reason to the caller if this is defined --->
	<!--- BEGIN adds to predefined list --->
		<cfparam name="attributes.allowOnly" default=""> 
		<cfparam name="attributes.denyAll" default="">
		<cfparam name="attributes.REallowOnly" default="">
		<cfparam name="attributes.REdenyAll" default="">
		<cfparam name="attributes.badWords" default="">
		<cfparam name="attributes.REbadWords" default="">
	<!--- END adds to predefined list --->
	<!--- BEGIN removes from the predefined list --->
		<cfparam name="attributes.lessallowOnly" default=""><!--- removes from the predefined list --->
		<cfparam name="attributes.lessREallowOnly" default="">
		<cfparam name="attributes.lessdenyAll" default="">
		<cfparam name="attributes.lessREdenyAll" default="">
		<cfparam name="attributes.lessbadWords" default="">
		<cfparam name="attributes.lessREbadWords" default="">
	<!--- END removes from the predefined list --->
<!--- END user defined optional parameters --->

<!--- Determining what operating system this is running on, and uses appropriate slash --->
<cfif find("/",#expandpath(".")#)><cfset OS_Slash="/"><cfelse><cfset OS_Slash="\"></cfif>

<!--- expand the definition of image to these values --->
<cfif findnocase("image",attributes.REallowOnly)>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"images","jpg,jpeg,gif,png,bmp,tif,tiff")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"image","jpg,jpeg,gif,png,bmp,tif,tiff")>
</cfif>
<!--- expand the definition of movie to these values --->
<cfif findnocase("movie",attributes.REallowOnly) or findnocase("video",attributes.REallowOnly)>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"movies","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"movie","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"videos","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"video","mov,qtime,qt,swf,avi,mpeg,mpg")>
</cfif>
<!--- expand the definition of audio to these values --->
<cfif findnocase("audio",attributes.REallowOnly) or findnocase("sound",attributes.REallowOnly)>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"audio","mp3,aif,aiff,wav,ram,wma")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"sounds","mp3,aif,aiff,wav,ram,wma")>
	<cfset attributes.REallowOnly=replacenocase(attributes.REallowOnly,"sound","mp3,aif,aiff,wav,ram,wma")>
</cfif>

<!--- expand the definition of image to these values --->
<cfif findnocase("image",attributes.allowOnly)>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"images","jpg,jpeg,gif,png,bmp,tif,tiff")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"image","jpg,jpeg,gif,png,bmp,tif,tiff")>
</cfif>
<!--- expand the definition of movie to these values --->
<cfif findnocase("movie",attributes.REallowOnly) or findnocase("video",attributes.REallowOnly)>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"movies","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"movie","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"videos","mov,qtime,qt,swf,avi,mpeg,mpg")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"video","mov,qtime,qt,swf,avi,mpeg,mpg")>
</cfif>
<!--- expand the definition of audio to these values --->
<cfif findnocase("audio",attributes.allowOnly) or findnocase("sound",attributes.allowOnly)>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"audio","mp3,aif,aiff,wav,ram,wma")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"sounds","mp3,aif,aiff,wav,ram,wma")>
	<cfset attributes.allowOnly=replacenocase(attributes.allowOnly,"sound","mp3,aif,aiff,wav,ram,wma")>
</cfif>



<!--- BEGIN adding or removing from predefined lists, items passed thorugh as module parameters --->

	<!--- REGULAR EXPRESSION ALLOW ONLY --->
	<cfif len(trim(attributes.REallowOnly)) gt 0>
		<cfset attributes.REallowOnly = listappend(REallowOnly,attributes.REallowOnly)>
	<cfelse>
		<cfset attributes.REallowOnly = REallowOnly>
	</cfif>
	
	<!--- looping through all the words, in order to remove those passed through the module as less parameters --->
	<cfloop list="#attributes.lessREallowOnly#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.REallowOnly#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.REallowOnly = listdeleteat(attributes.REallowOnly,atCount)>
		</cfif>
	</cfloop>
	
	<!--- ALLOW ONLY --->
	<cfif len(trim(attributes.allowOnly)) gt 0>
		<cfset attributes.allowOnly = listappend(allowOnly,attributes.allowOnly)>
	<cfelse>
		<cfset attributes.allowOnly = allowOnly>
	</cfif>
	

	<!--- looping through all the words, in order to remove those passed through the module as less parameters --->
	<cfloop list="#attributes.lessallowOnly#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.allowOnly#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.allowOnly = listdeleteat(attributes.allowOnly,atCount)>
		</cfif>
	</cfloop>
	
	
	
	<!--- REGULAR EXPRESSION DENY ALLL --->
	<cfif len(trim(attributes.REdenyAll)) gt 0>
		<cfset attributes.REdenyAll = listappend(REdenyAll,attributes.REdenyAll)>
	<cfelse>
		<cfset attributes.REdenyAll = REdenyAll>
	</cfif>
	<!--- looping through all the words, in order to remove those passed through the module as less parameters --->
	<cfloop list="#attributes.lessREdenyAll#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.REdenyAll#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.REdenyAll = listdeleteat(attributes.REdenyAll,atCount)>
		</cfif>
	</cfloop>
	
	<!--- adding to the list, items passed thorugh as module parameters --->
	<cfif len(trim(attributes.denyAll)) gt 0>
		<cfset attributes.denyAll = listappend(denyAll,attributes.denyAll)>
	<cfelse>
		<cfset attributes.denyAll = denyAll>
	</cfif>
	
	<!--- DENY ALLL --->
	<cfloop list="#attributes.lessdenyAll#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.denyAll#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.denyAll = listdeleteat(attributes.denyAll,atCount)>
		</cfif>
	</cfloop>
	
	
	
	
	
	<!--- REGULAR EXPRESSION BAD WORDS IN FILE CONTENT --->
	<cfif len(trim(attributes.REbadWords)) gt 0>
		<cfset attributes.REbadWords = listappend(REbadWords,attributes.REbadWords)>
	<cfelse>
		<cfset attributes.REbadWords = REbadWords>
	</cfif>
	<!--- looping through all the words, in order to remove those passed through the module as less parameters --->
	<cfloop list="#attributes.lessREbadWords#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.REbadWords#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.REbadWords = listdeleteat(attributes.REbadWords,atCount)>
		</cfif>
	</cfloop>
	<cfif len(trim(attributes.badWords)) gt 0>
		<cfset attributes.badWords = listappend(badWords,attributes.badWords)>
	<cfelse>
		<cfset attributes.badWords = badWords>
	</cfif>
	
	<!--- BAD WORDS IN FILE CONTENT --->
	<cfloop list="#attributes.lessbadWords#" index="notbad">
		<cfset theCount = 0>
		<cfset atCount = 0>
		<cfloop list="#attributes.badWords#" index="bad">
			<cfset theCount = theCount +1>
			<cfif lcase(bad) eq lcase(notbad)><cfset atCount = theCount></cfif>
		</cfloop>
		<cfif atCount gt 0>
			<!--- if this item is found, removing from final list --->
			<cfset attributes.badWords = listdeleteat(attributes.badWords,atCount)>
		</cfif>
	</cfloop>

<!--- END adding or removing from predefined lists, items passed thorugh as module parameters --->



<!---   -------------------- THE CORE --------------------   --->
<!---	Keep your arms and legs inside the car at all times  --->
<!----
TODO add this lines and it will work
<cfif isdefined("caller.cffile") and !isdefined("cffile")>
	<cfset cffile=caller.cffile>
</cfif>
---->
<cfif isdefined("cffile")>
	<cfif isStruct(cffile)>
		<cfif attributes.IS_debug eq "yes"><cfoutput>CFFILE is Defined <br></cfoutput></cfif>
		<cfif structKeyExists(cffile,"ATTEMPTEDSERVERFILE")>
			<cfif attributes.IS_debug eq "yes"><cfoutput>CFFILE.ATTEMPTEDSERVERFILE is #cffile.ATTEMPTEDSERVERFILE# <br></cfoutput></cfif>
				<cfif fileExists("#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#")>
					<cfif attributes.IS_debug eq "yes"><cfoutput>#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#<br></cfoutput></cfif>


					<!--- Determine file extension --->
					<cfset attributes.fileExtension = listlast(cffile.attemptedserverfile,".")>



	<!--- loop through denys, first make sure it's not bad to begin with --->
					<cfloop list="#attributes.denyAll#" index="denyThis">
						<cfif findnocase(denyThis,attributes.fileExtension)>
							<!--- found a bad file extension --->
							<cfset reject = "yes">
							<cfset reason="#reason#<br>found #denyThis# in the extension">
							<cfbreak>
						</cfif>
					</cfloop>
					
					<cfif reject neq "yes"> <!--- don't bother if we're allready rejecting !!! --->
						<cfloop list="#attributes.REdenyAll#" index="denyThis">
							<cfif refindnocase(denyThis,#cffile.attemptedserverfile#)>
								<!--- found a taboo regular expression in this filename --->
								<cfset reject = "yes">
								<cfset reason="#reason#<br>found #denyThis# in the filename">
								<cfbreak>
							</cfif>
						</cfloop>
					</cfif>



	<!--- loop through allows --->
					<cfif reject neq "yes">  <!--- don't bother if we're allready rejecting !!! --->
						<!--- loop through denys, first make sure it's not bad to begin with --->
						<cfif attributes.allowOnly neq "" and attributes.allowOnly neq "[NULL]" and len(trim(attributes.allowOnly)) neq 0>
							<cfset reject = "yes">  <!--- allow none by default --->
							<cfloop list="#attributes.allowOnly#" index="allowThis">
								<cfif findnocase(allowThis,attributes.fileExtension)>
									<!--- found a fileextension type that we are willing to accept --->
									<cfset reject = "no">
									<cfbreak>
								</cfif>
							</cfloop>				
							<!--- if reject = no at this point... one of the REallowOnly have been seen --->
							<cfif reject eq "yes">
								<cfset reason="#reason#<br>file extension not allowed or doesn't exist">
							</cfif>
						</cfif>  <!--- attributes.REallowOnly neq "" and attributes.REallowOnly neq "[NULL]" --->

					
			<!--- loop through REallows --->
						<cfif attributes.REallowOnly neq "" and attributes.REallowOnly neq "[NULL]" and len(trim(attributes.REallowOnly)) neq 0>
							<cfset reject = "yes">  <!--- allow none by default --->
							<cfloop list="#attributes.REallowOnly#" index="allowThis">
								<cfif refindnocase(allowThis,#cffile.attemptedserverfile#)>
									<!--- found a regular expression filename type that we are willing to accept --->
									<cfset reject = "no">
									<cfbreak>
								</cfif>
							</cfloop>				
							<!--- if reject = no at this point... one of the REallowOnly have been seen --->
							<cfif reject eq "yes">
								<cfset reason="#reason#<br>count not find any of #attributes.REallowOnly# in the filename">
							</cfif>
						</cfif>  <!--- attributes.REallowOnly neq "" and attributes.REallowOnly neq "[NULL]" --->
					</cfif> <!--- reject neq yes --->
					
					
					
					
					
					
	<!--- now make doubly sure that really bad words aren't present --->
					<cfif reject neq "yes" and attributes.checkFileContents eq "yes">
						<!--- read the contents of the file into a variable, and search that variable for bad stuff --->
						<cffile action="read" file="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#" variable="theContents">
						
						<cfloop list="#attributes.badWords#" index="thisBadWord">
							<cfif findnocase(thisBadWord,#theContents#)>
								<!--- found something bad in the contents of the uploaded file --->
								<cfset reject = "yes">
								<cfset reason="#reason#<br>found #thisBadWord# in the file content">
								
								<cfbreak>
							</cfif>
						</cfloop>
						
						<cfloop list="#attributes.REbadWords#" index="thisBadWord">
							<cfif refindnocase(thisBadWord,#theContents#)>
								<!---  found something in a regular expression bad in the contents of the uploaded file  --->
								<cfset reject = "yes">
								<cfset reason="#reason#<br>found #thisBadWord# in the file content">
								
								<cfbreak>
							</cfif>
						</cfloop>
					
					</cfif>				
				</cfif>  <!--- end of if fileExists --->
		</cfif>  <!--- end of if structKeyExists --->
	</cfif>  <!--- end of if isStruct(cffile) --->
</cfif> 




<cfif reject eq "yes">



<!--- take action upon the file --->
		<cfif findnocase("ignore",attributes.action) or findnocase("report",attributes.action) or findnocase("allow",attributes.action)>
			<!--- do no further processing... email will be sent notifying security and developer --->		
		<cfelseif findnocase("del",attributes.action) or  findnocase("rem",attributes.action)>
			<!--- delete this file immediately... don't play with it --->
			<cffile action="DELETE"  file="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#">			
			<!--- continue to send email to security and developer, then redirect to #attributes.Location# --->
		<cfelse> 
			<!--- by default, just rename/quarantene it --->
			<cffile action="MOVE"  
			source="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#"			
			destination="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#.quarantine"
			>			
			<!--- abend --->
		</cfif> <!--- ignore, report, allow --->

<!--- do we need to set an error variable? --->
		<cfif attributes.ErrorOutput neq "">
			<cfset "caller.#attributes.ErrorOutput#" = reason>
		</cfif>


	<cfif attributes.email2Security neq "no">
		<cfoutput>

		<cfmail to="ejkoeber@jpl.nasa.gov" from="ejkoeber@jpl.nasa.gov" subject="[ERROR cffileSecurity] #application.applicationname#" type="html">

				<table border="1" cellpadding="0" cellspacing="0">
				<tr><td>Reason for this&nbsp;&nbsp;&nbsp;</td><td>#reason#</td></tr>
				<tr><td>File In Question&nbsp;&nbsp;&nbsp;</td><td>#cffile.attemptedserverfile#</td></tr>
				<tr><td>Directory of File&nbsp;&nbsp;&nbsp;</td><td>#cffile.serverDirectory#</td></tr>
				<tr><td>Action Taken&nbsp;&nbsp;&nbsp;</td><td>#attributes.action#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td>CGI TemplatPath&nbsp;&nbsp;&nbsp;</td><td>#CGI.CF_TEMPLATE_PATH#</td></tr>
				<Tr><td>referrer&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_REFERER#</td></Tr>
				<Tr><td>UserAgent&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_USER_Agent#</td></Tr>
				<tr><td>Version/Date&nbsp;&nbsp;&nbsp;</td><td>#thisVersion# / #thisDate#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td>CGI HTTP_HOST&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_HOST#</td></tr>
				<tr><td>CGI QUERY_STRING&nbsp;&nbsp;&nbsp;</td><td>#CGI.QUERY_STRING#</td></tr>
				<tr><td>CGI REMOTE_ADDR&nbsp;&nbsp;&nbsp;</td><td>#CGI.REMOTE_ADDR#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
					
				<tr><td>MailTo&nbsp;&nbsp;&nbsp;</td><td>#attributes.MailTo#</td></tr>
				<tr><td>allowOnly&nbsp;&nbsp;&nbsp;</td><td>#attributes.allowOnly#</td></tr>
				<tr><td>denyAll&nbsp;&nbsp;&nbsp;</td><td>#attributes.denyAll#</td></tr>
				<tr><td>badWords&nbsp;&nbsp;&nbsp;</td><td>#attributes.badWords#</td></tr>
				<tr><td>Reg-Exp allowOnly&nbsp;&nbsp;&nbsp;</td><td>#attributes.REallowOnly#</td></tr>
				<tr><td>Reg-Exp denyAll&nbsp;&nbsp;&nbsp;</td><td>#attributes.REdenyAll#</td></tr>
				<tr><td>Reg-Exp badWords&nbsp;&nbsp;&nbsp;</td><td>#attributes.REbadWords#</td></tr>
				<tr><td>checkFileContents&nbsp;&nbsp;&nbsp;</td><td>#attributes.checkFileContents#</td></tr>
				</table>
				<p></p>


				<cfif isDefined("form")><p>form</p><cfdump var="#form#"></cfif>
				<cfif isDefined("url")><p>url</p><cfdump var="#url#"></cfif>
				<cfif isDefined("error")><p>Error<cfdump var="#error#"></cfif>
				<cfif isDefined("application")><p>application</p><cfdump var="#application#"></cfif>
				<cfif isDefined("client")><p>client</p><cfdump var="#client#"></cfif>
				<cfif isDefined("attributes")><p>attributes</p><cfdump var="#attributes#"></cfif>
				<cfif isDefined("request")><p>request</p><cfdump var="#request#"></cfif>
				<cfif isDefined("CGI")><p>cgi</p><cfdump var="#CGI#"></cfif>
				<cfif isDefined("Session")><p>session</p><cfdump var="#Session#"></cfif>
				<cfif isDefined("cookie")><p>Cookie</p><cfdump var="#cookie#"></cfif>
				<cftry>
					<cfif isDefined("server")>
						<p>server</p>
						<cfdump var="#server#">
					</cfif>
					<cfcatch type="any">
						Sorry, server information is unavailable<p>
					</cfcatch>
				</cftry>
				<cfif findnocase("ignore",attributes.action) or findnocase("report",attributes.action) or findnocase("allow",attributes.action)>
					<cfmailparam file="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#">
				<cfelseif findnocase("del",attributes.action) or  findnocase("rem",attributes.action)>
					<!--- file deleted, nothing to attach --->
				<cfelse> 
					<cfmailparam file="#cffile.serverDirectory##OS_Slash##cffile.attemptedserverfile#.quarantine">
				</cfif>
		</cfmail>	
		</cfoutput>

	</cfif> <!--- end of attributes.email2Security neq "no" --->
	

	<!--- if requested.... send email alert to developer and security --->
	<cfif attributes.email2MailTo neq "no">
		<cfif isdefined("attributes.MailTo")><!--- only if we have an email address --->
			<cfif len(trim(attributes.MailTo)) gt 0 and attributes.MailTo neq "[NULL]"><!--- that isn't nothing --->


			<cfoutput>

			<cfmail to="#attributes.mailto#" from="ejkoeber@jpl.nasa.gov" subject="[ERROR cffileSecurity] #application.applicationname#" type="html">

				<table border="1" cellpadding="0" cellspacing="0">
				<tr><td>Reason for this&nbsp;&nbsp;&nbsp;</td><td>#reason#</td></tr>
				<tr><td>File In Question&nbsp;&nbsp;&nbsp;</td><td>#cffile.attemptedserverfile#</td></tr>
				<tr><td>Directory of File&nbsp;&nbsp;&nbsp;</td><td>#cffile.serverDirectory#</td></tr>
				<tr><td>Action Taken&nbsp;&nbsp;&nbsp;</td><td>#attributes.action#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td>CGI TemplatPath&nbsp;&nbsp;&nbsp;</td><td>#CGI.CF_TEMPLATE_PATH#</td></tr>
				<Tr><td>referrer&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_REFERER#</td></Tr>
				<Tr><td>UserAgent&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_USER_Agent#</td></Tr>
				<tr><td>Version/Date&nbsp;&nbsp;&nbsp;</td><td>#thisVersion# / #thisDate#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td>CGI HTTP_HOST&nbsp;&nbsp;&nbsp;</td><td>#CGI.HTTP_HOST#</td></tr>
				<tr><td>CGI QUERY_STRING&nbsp;&nbsp;&nbsp;</td><td>#CGI.QUERY_STRING#</td></tr>
				<tr><td>CGI REMOTE_ADDR&nbsp;&nbsp;&nbsp;</td><td>#CGI.REMOTE_ADDR#</td></tr>
					<tr bgcolor="blue"><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td>MailTo&nbsp;&nbsp;&nbsp;</td><td>#attributes.MailTo#</td></tr>
				<tr><td>REallowOnly&nbsp;&nbsp;&nbsp;</td><td>#attributes.REallowOnly#</td></tr>
				<tr><td>REdenyAll&nbsp;&nbsp;&nbsp;</td><td>#attributes.REdenyAll#</td></tr>
				<tr><td>checkFileContents&nbsp;&nbsp;&nbsp;</td><td>#attributes.checkFileContents#</td></tr>
				<tr><td>REbadWords&nbsp;&nbsp;&nbsp;</td><td>#attributes.REbadWords#</td></tr>
				</table>
				<p></p>


				<cfif isDefined("form")><p>form</p><cfdump var="#form#"></cfif>
				<cfif isDefined("url")><p>url</p><cfdump var="#url#"></cfif>
				<cfif isDefined("error")><p>Error<cfdump var="#error#"></cfif>
				<cfif isDefined("application")><p>application</p><cfdump var="#application#"></cfif>
				<cfif isDefined("client")><p>client</p><cfdump var="#client#"></cfif>
				<cfif isDefined("attributes")><p>attributes</p><cfdump var="#attributes#"></cfif>
				<cfif isDefined("request")><p>request</p><cfdump var="#request#"></cfif>
				<cfif isDefined("CGI")><p>cgi</p><cfdump var="#CGI#"></cfif>
				<cfif isDefined("Session")><p>session</p><cfdump var="#Session#"></cfif>
				<cfif isDefined("cookie")><p>Cookie</p><cfdump var="#cookie#"></cfif>
				<cftry>
					<cfif isDefined("server")>
						<p>server</p>
						<cfdump var="#server#">
					</cfif>
					<cfcatch type="any">
						Sorry, server information is unavailable<p>
					</cfcatch>
				</cftry>

		</cfmail>	
		</cfoutput>
		</cfif><!--- len = 0 --->
		</cfif><!--- email2mailto exists --->
	</cfif> <!--- end of attributes.email2Security neq "no" --->
		
		
<!--- redirect or pass through normally --->		
		<cfif findnocase("ignore",attributes.action) or findnocase("report",attributes.action) or findnocase("allow",attributes.action)>
			<!--- let it pass through to next normal page --->
		<cfelseif attributes.location neq "">
			<!--- divert it to a controlled page --->
			<cflocation url="#attributes.location#">  
		</cfif> <!--- ignore, report, allow --->
		
		

</cfif>


<!--- end of the ride, please procede to the exit --->
