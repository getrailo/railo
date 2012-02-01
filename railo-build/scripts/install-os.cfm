<!---
<cfdirectory directory="\\.psf\Projects\Railo/Deploy-Source/" action="list" name="dir" recurse="no">
<cfdump eval="dir">


<cfdirectory directory="/projects/Railo/Deploy-Source/" action="list" name="dir" recurse="no">
<cfdump eval="dir">
--->


<cffunction name="copyRec" output="false">
	<cfargument name="from">
	<cfargument name="to">
	<cfargument name="recursive" default="true" type="boolean">
	
	<cfset var dir="">
	<cfif FileExists(arguments.from)>
		<cfif not find(".DS_Store",arguments.from)> 
			<cfif FileExists(arguments.to)><cffile action="delete" file="#arguments.to#"></cfif>
			<cffile action="copy" destination="#arguments.to#" source="#arguments.from#" mode="777">
		</cfif>
	<cfelseif DirectoryExists(arguments.from)>
		<cfdirectory directory="#arguments.from#" action="list" name="dir">
		<cfif not DirectoryExists(arguments.to)>
			<cfdirectory directory="#arguments.to#" action="create" mode="777">
		</cfif>
		<cfloop query="dir">
        	<cfif recursive or fileExists(arguments.from&"/"&dir.name)>
				<cfset copyRec(arguments.from&"/"&dir.name,arguments.to&"/"&dir.name,true)>
            </cfif>
		</cfloop>
	<cfelse>
		
	</cfif>
</cffunction>
<cfif isDefined("form.version")>
	
	<cfflush>
	
	
	<cfset ext="rc">
	<cfset sext="rcs">
	
	<cfset topVersion=left(form.version,3)>
    <cfset v3=replace(mid(server.railo.version,1,3),".","-")>
	<cfsetting requesttimeout="2000000">
	<cfparam name="form.create" default="">
	<cfset zipDirs=array()>
	<cfset resinName="resin-3.1.9">
	<cfset railixName="jetty-7.2.2">
    
	<cfset resinDSPName="resin">
	<cfset railixDSPName="railo-express">
	
	
    
    
	<cfset tomName="tomcat-5.5.12">
	
	<cfset isWindows=find("Windows",server.os.name) GT 0>
	
	<cfif isWindows>
		<cfset projectsFolder="\\.psf\Projects\">
	<cfelse>
		<cfset projectsFolder="/Users/mic/Projects/">
	</cfif>
	<cfset projectFolder="#projectsFolder#Railo/">
	
	<cfset source=projectFolder&"Deploy-Source/">
	<cfset railoJar=source&"railo-jar-open/railo-#form.version#.jar">
	<cfset cfmlTemplates=source&"cfml-templates/">
	<cfset deploy=projectFolder&"Deploy/#form.version#/">
	
	<cfset download=deploy&"#form.version#/">
	<cfset downloadCustom=download&"custom/">
	<cfset downloadServer=download&"server/">
	<cfset downloadRailix=download&"railix/">
	
	<cfset buildSource=projectFolder&"Deploy-Source/railo-jar-open/#form.version#.#ext#">
	<cfset buildSourceSecure=projectFolder&"Deploy-Source/railo-jar-closed/#form.version#.#sext#">
	<cfset buildTarget=download&"#form.version#.#ext#">
	<cfset buildTargetSecure=download&"#form.version#.#sext#">
	
	
	<cfset jre4_win=source&"jre/windows-x86-1.4.2_09/">
	<cfset jre5_win=source&"jre/windows-x86-1.5.0_11/">
	<cfset jre6_win32=source&"jre/windows-x86-1.6.0_23/">
	<cfset jre6_win64=source&"jre/windows-x64-1.6.0_25/">
	<cfset jre4_lin=source&"jre/linux-x86-1.4.2_09/">
	<cfset jre5_lin=source&"jre/linux-x86-1.5.0_11/">
	<cfset jre6_lin=source&"jre/linux-i586-jre1.6.0_23/">
	
	<cfset jre_win=jre6_win32>
	<cfset jre_lin=jre6_lin>
	
	
	
	<cfset jarSource="/Users/mic/Projects/Railo/Source2/railo/railo-java/libs/">
	<cfset jarSourceRemoved=source&"jars-"&v3&"-removed/">
	<cfset licence=source&"licence/License-#v3#.txt">
	<cfset installDir=source&"install-doc/">
	<cfset auxSource=source&"jars-build/">
	<cfset tmpDir=source&"tmp/">
	
	<cfif not DirectoryExists(source)><cfdirectory directory="#source#" action="create"></cfif>
	<cfif not DirectoryExists(deploy)><cfdirectory directory="#deploy#" action="create"></cfif>
	<cfif not DirectoryExists(download)><cfdirectory directory="#download#" action="create"></cfif>
	<cfif not DirectoryExists(auxSource)><cfdirectory directory="#auxSource#" action="create"></cfif>
	<cfif not DirectoryExists(tmpDir)><cfdirectory directory="#tmpDir#" action="create"></cfif>
	<cfif not FileExists(railoJar)><cfthrow message="missing #railoJar#"></cfif>
	<cfloop index="name" list="custom,server,railix">
		<cfif not DirectoryExists(download&name)><cfdirectory directory="#download##name#" action="create"></cfif>
		<cfif not DirectoryExists(download&name&"/unix")><cfdirectory directory="#download##name#/unix" action="create"></cfif>
		<cfif not DirectoryExists(download&name&"/windows")><cfdirectory directory="#download##name#/windows" action="create"></cfif>
		<cfif not DirectoryExists(download&name&"/all")><cfdirectory directory="#download##name#/all" action="create"></cfif>
		<cfif not DirectoryExists(download&name&"/macosx")><cfdirectory directory="#download##name#/macosx" action="create"></cfif>
	</cfloop>
    
   
   <!--- remove unnecessary jars --->
   <cfset _tmp=expandPath("{temp-directory}/jars/")>
   <cfif DirectoryExists(_tmp)><cfdirectory directory="#_tmp#" action="delete" recurse="yes"> </cfif>
   <cfdirectory directory="#_tmp#" action="create">
   <cfdump var="#_tmp#">
   <cfset copyRec(jarSource,_tmp,false)>
   <cfset jarSource=_tmp>
   <cfif fileExists("#_tmp#/javax.servlet.jar")><cffile action="delete" file="#_tmp#/javax.servlet.jar"></cfif>
   <cfif fileExists("#_tmp#/ojdbc14.jar")><cffile action="delete" file="#_tmp#/ojdbc14.jar"></cfif>
   <cfif fileExists("#_tmp#/org.mortbay.jetty.jar")><cffile action="delete" file="#_tmp#/org.mortbay.jetty.jar"></cfif>
   
   

	<!--- copy build --->
	<cfif fileExists(buildTarget)><cffile action="delete" file="#buildTarget#"></cfif>
	<cffile action="copy" source="#buildSource#" destination="#buildTarget#">
	
	<cfif fileExists(buildTargetSecure)><cffile action="delete" file="#buildTargetSecure#"></cfif>
	<cffile action="copy" source="#buildSourceSecure#" destination="#buildTargetSecure#">
	
	<!--- Jars --->
		<cfif ListFind(form.create,'jars')>
		<cfscript>
		jarsDeploy=deploy&"railo-"&form.version&"-jars";
		jarsDeployDownload=downloadCustom&"all/railo-"&form.version&"-jars";
		copyRec(jarSource,jarsDeploy,false);
		copyRec(railoJar,jarsDeploy&"/railo.jar");
		copyRec(licence,jarsDeploy&"/License.txt");
		compress("zip",jarsDeploy,jarsDeployDownload&".zip");
		compress("tgz",jarsDeploy,jarsDeployDownload&".tar.gz");
		
		</cfscript>
		</cfif>
	
	<!--- Caucho Resin --->
		<cfif ListFind(form.create,'resin') or ListFind(form.create,'resin_exe')>
		<h1>Create Resin</h1>
		</cfif>
		<cfif ListFind(form.create,'resin')>
		<cfset resinSource=source&"server/"&resinName&"/">
		
		<!--- No JRE --->
			<p><li>No JRE</li></p>
			<cfset resinDeployNoJRE=deploy&"railo-"&form.version&"-"&resinDSPName&"-without-jre">
			<cfset resinDeployNoJREDownload=downloadServer&"all/railo-"&form.version&"-"&resinDSPName&"-without-jre">
			
			
			
			<cfif not DirectoryExists(resinDeployNoJRE)><cfdirectory directory="#resinDeployNoJRE#" action="create"></cfif>
			<cfset copyRec(resinSource,resinDeployNoJRE)>
			<cfset copyRec(jarSource,resinDeployNoJRE&"/lib/",false)>
			<cfset copyRec(railoJar,resinDeployNoJRE&"/lib/railo.jar")>
			<cfset copyRec(licence,resinDeployNoJRE&"/License.txt")>
			<cfset copyRec(installDir&"resin.txt",resinDeployNoJRE&"/install.txt")>
			<cfset copyRec(cfmlTemplates,resinDeployNoJRE&"/webapps/ROOT/")>
			<cfset compress("zip",resinDeployNoJRE,resinDeployNoJREDownload&".zip")>
			<cfset compress("tgz",resinDeployNoJRE,resinDeployNoJREDownload&".tar.gz")>
		 
		
		<!--- JRE 1.4 win --->
			<p><li>JRE Windows</li></p>
			<cfset name="railo-"&form.version&"-"&resinDSPName&"-with-jre-windows">
			<cfset resinDeployJREWin=deploy&name>
			<cfset resinDeployJREWinDownload=downloadServer&"windows/"&name>
			<cfif not DirectoryExists(resinDeployJREWin)><cfdirectory directory="#resinDeployJREWin#" action="create"></cfif>
			
			<cfset copyRec(resinSource,resinDeployJREWin)>
			<cfset copyRec(jarSource,resinDeployJREWin&"/lib/",false)>
			<cfset copyRec(railoJar,resinDeployJREWin&"/lib/railo.jar")>
			<cfset copyRec(cfmlTemplates,resinDeployJREWin&"/webapps/ROOT/")>
			<cfset copyRec(licence,resinDeployJREWin&"/License.txt")>
			<cfset copyRec(installDir&"resin.txt",resinDeployJREWin&"/install.txt")>
			<cfset copyRec(jre_win,resinDeployJREWin&"/jre/")>
			<cfset copyRec(source&"batch-#v3#/resin-local-jre-install-service.bat",resinDeployJREWin&"/install-service.bat")>
			<cfset compress("zip",resinDeployJREWin,resinDeployJREWinDownload&".zip")>
		</cfif>
		<cfif ListFind(form.create,'resin_exe')>
			<p><li>Setup Exe</li></p>
			<!--- setup --->
			<cfset setupScript=source&"script/railo-#v3#.nsi">
			<cfset setupScriptTarget=source&"script/railo-#form.version#.nsi">
			<cffile action="read" file="#setupScript#" variable="script">

			<cfdirectory directory="#jarSourceRemoved#" action="list" name="removed">
			<cfdirectory directory="#jarSource#" action="list" name="insert">
			<cfset r="">
			<cfoutput query="removed">
				<cfif removed.name NEQ ".DS_Store">
					<cfset r=r&chr(13)&chr(10)&"Delete ""$OUTDIR\lib\#removed.name#""">
				</cfif>
			</cfoutput>
			<cfset r=r&chr(13)&chr(10)&"Delete ""$OUTDIR\lib\railo.jar""">
			
			<cfoutput query="insert">
				<cfif insert.name NEQ ".DS_Store">
					<cfset r=r&chr(13)&chr(10)&"Delete ""$OUTDIR\lib\#insert.name#""">
				</cfif>
			</cfoutput>
			<cfset script=replace(script,'{remove-jars}',r,'all')>
			
			<cfset i="">
			<cfoutput query="insert">
				<cfif insert.name NEQ ".DS_Store">
					<cfset i=i&chr(13)&chr(10)&"File /oname=\lib\#insert.name# ""#projectFolder#deploy\{railo}\railo-{railo}-{resin}-with-jre-windows\lib\#insert.name#""    ">
				</cfif>
			</cfoutput>
			<cfset i=i&chr(13)&chr(10)&"File /oname=\lib\railo.jar ""#projectFolder#deploy\{railo}\railo-{railo}-{resin}-with-jre-windows\lib\railo.jar""    ">
			<cfset script=replace(script,'{insert-jars}',i,'all')>
			
		
			<cfset script=replace(script,'{railo}',form.version,'all')>
			<cfset script=replace(script,'{railo-version-int}',replace(form.version,'.','','all'),'all')>
			<cfset script=replace(script,'{resin}',resinDSPName,'all')>
			<cfset script=replace(script,'{projects}',projectsFolder,'all')>
			<cfset script=replace(script,'{top-version}',topVersion,'all')>
			<cfset script=replace(script,'{version_}',replace(topVersion,'.','_','all'),'all')>
			
			
			
						
			<cffile action="write" file="#setupScriptTarget#" output="#script#" addnewline="yes">
			<cfdump var="""C:\Program Files\NSIS\makensis"" #setupScriptTarget#">
			
			<cfexecute name="""C:\Program Files\NSIS\makensis"" #setupScriptTarget#"></cfexecute>
			
			
		</cfif>
		<cfif ListFind(form.create,'resin')>	
		<!--- JRE 1.4 lin --->
			<p><li>JRE Linux</li></p>
			<cfset name="railo-"&form.version&"-"&resinDSPName&"-with-jre-linux">
			<cfset resinDeployJRELin=deploy&name>
			<cfset resinDeployJRELinDownload=downloadServer&"unix/"&name>
			<cfif not DirectoryExists(resinDeployJRELin)><cfdirectory directory="#resinDeployJRELin#" action="create"></cfif>
			
			<cfset copyRec(resinSource,resinDeployJRELin)>
			<cfset copyRec(jarSource,resinDeployJRELin&"/lib/",false)>
			<cfset copyRec(railoJar,resinDeployJRELin&"/lib/railo.jar")>
			<cfset copyRec(cfmlTemplates,resinDeployJRELin&"/webapps/ROOT/")>
			<cfset copyRec(licence,resinDeployJRELin&"/License.txt")>
			<cfset copyRec(installDir&"resin.txt",resinDeployJRELin&"/install.txt")>
			<cfset copyRec(jre_lin,resinDeployJRELin&"/jre/")>
			<cfset copyRec(source&"batch-#v3#/resin-local-jre-install-service.bat",resinDeployJRELin&"/install-service.bat")>
			<cfset compress("tgz",resinDeployJRELin,resinDeployJRELinDownload&".tar.gz")>
			

		
		</cfif>
		

	<!--- Jetty --->
	

	<!--- Railix --->
	<cfset extraLib='/lib/ext/'>
	<cfif ListFind(form.create,'railix')>
		<h1>Create Railix</h1>
				
		<!--- No JRE --->
		<p><li>No JRE</li></p>
		<cfset railixSource=source&"server/"&railixName&"/">
		<cfset name="railo-"&form.version&"-"&railixDSPName&"-without-jre">
		<cfset railixDeployNoJRE=deploy&name>
		<cfset railixDeployNoJREDownload=downloadRailix&"all/"&name>
		<cfif not DirectoryExists(railixDeployNoJRE)><cfdirectory 
		directory="#railixDeployNoJRE#" action="create"></cfif>
	
		<cfset copyRec(railixSource,railixDeployNoJRE)>
		<cfset copyRec(jarSource,railixDeployNoJRE&extraLib,false)>
		<cfset copyRec(railoJar,railixDeployNoJRE&extraLib&'railo.jar')>
			<cfset copyRec(licence,railixDeployNoJRE&"/License.txt")>
			<cfset copyRec(installDir&"railix.txt",railixDeployNoJRE&"/install.txt")>
		<cfset copyRec(cfmlTemplates,railixDeployNoJRE&"/webroot/")>
		<cfset compress("zip",railixDeployNoJRE,railixDeployNoJREDownload&".zip")>
		<cfset compress("tgz",railixDeployNoJRE,railixDeployNoJREDownload&".tar.gz")>
		
		
		<!--- MacOSX --->
		<p><li>No JRE</li></p>
		<cfset railixSource=source&"server/"&railixName&"/">
		<cfset name="railo-"&form.version&"-"&railixDSPName&"-macosx">
		<cfset railixDeployNoJRE=deploy&name>
		<cfset railixDeployNoJREDownload=downloadRailix&"macosx/"&name>
		<cfif not DirectoryExists(railixDeployNoJRE)><cfdirectory 
		directory="#railixDeployNoJRE#" action="create"></cfif>
	
		<cfset copyRec(railixSource,railixDeployNoJRE)>
		<cfset copyRec(jarSource,railixDeployNoJRE&extraLib,false)>
		<cfset copyRec(railoJar,railixDeployNoJRE&extraLib&'railo.jar')>
		<cfset copyRec(licence,railixDeployNoJRE&"/License.txt")>
		<cfset copyRec(installDir&"railix.txt",railixDeployNoJRE&"/install.txt")>
		<cfset copyRec(cfmlTemplates,railixDeployNoJRE&"/webroot/")>
		<cfif fileExists('#railixDeployNoJRE#/start.bat')><cffile action="delete" file="#railixDeployNoJRE#/start.bat"></cfif>
		<cfif fileExists('#railixDeployNoJRE#/stop.bat')><cffile action="delete" file="#railixDeployNoJRE#/stop.bat"></cfif>
        
		<cfset compress("zip",railixDeployNoJRE,railixDeployNoJREDownload&".zip")>
		

		<!--- With JRE Windows --->
        <cfloop list="32,64" index="arch">
		<cfoutput><p><li>JRE Windows (#arch#)</li></p></cfoutput>
		<cfset railixSource=source&"server/"&railixName&"/">
		<cfset name="railo-"&form.version&"-"&railixDSPName&"-with-jre-windows#arch#">
		<cfset railixDeployJREWin=deploy&name>
		<cfset railixDeployJREWinDownload=downloadRailix&"windows/"&name>
		<cfif not DirectoryExists(railixDeployJREWin)><cfdirectory directory="#railixDeployJREWin#" action="create"></cfif>
	
		<cfset copyRec(railixSource,railixDeployJREWin)>
		<cfset copyRec(jarSource,railixDeployJREWin&extraLib,false)>
		<cfset copyRec(railoJar,railixDeployJREWin&extraLib&"railo.jar")>
		<cfset copyRec(cfmlTemplates,railixDeployJREWin&"/webroot/")>
			<cfset copyRec(licence,railixDeployJREWin&"/License.txt")>
			<cfset copyRec(installDir&"railix.txt",railixDeployJREWin&"/install.txt")>
		<cfset copyRec(variables['jre6_win'&arch],railixDeployJREWin&"/jre/")>
			<cfif fileExists('#railixDeployJREWin#/start')><cffile action="delete" file="#railixDeployJREWin#/start"></cfif>
			<cfif fileExists('#railixDeployJREWin#/stop')><cffile action="delete" file="#railixDeployJREWin#/stop"></cfif>
			<cfif fileExists('#railixDeployJREWin#/start.bat')><cffile action="delete" file="#railixDeployJREWin#/start.bat"></cfif>
			<cfif fileExists('#railixDeployJREWin#/stop.bat')><cffile action="delete" file="#railixDeployJREWin#/stop.bat"></cfif>
			
			<cffile action="copy" source="#source&"batch-#v3#/railix-local-jre-start.bat"#" destination="#railixDeployJREWin&"/start.bat"#" mode="777">
        	<cffile action="copy" source="#source&"batch-#v3#/railix-local-jre-stop.bat"#" destination="#railixDeployJREWin&"/stop.bat"#" mode="777">
        
		<cfset compress("zip",railixDeployJREWin,railixDeployJREWinDownload&".zip")>
		</cfloop>
        
        
		<!--- With JRE Linux --->
		<p><li>JRE Linux</li></p>
		<cfset railixSource=source&"server/"&railixName&"/">
		<cfset name="railo-"&form.version&"-"&railixDSPName&"-with-jre-linux">
		<cfset railixDeployJRELin=deploy&name>
		<cfset railixDeployJRELinDownload=downloadRailix&"unix/"&name>
		<cfif not DirectoryExists(railixDeployJRELin)><cfdirectory directory="#railixDeployJRELin#" action="create"></cfif>
	
		<cfset copyRec(railixSource,railixDeployJRELin)>
		<cfset copyRec(jarSource,railixDeployJRELin&extraLib,false)>
		<cfset copyRec(railoJar,railixDeployJRELin&extraLib&"railo.jar")>
		<cfset copyRec(cfmlTemplates,railixDeployJRELin&"/webroot/")>
			<cfset copyRec(licence,railixDeployJRELin&"/License.txt")>
			<cfset copyRec(installDir&"railix.txt",railixDeployJRELin&"/install.txt")>
		<cfset copyRec(jre_lin,railixDeployJRELin&"/jre/")>
			<cfif fileExists('#railixDeployJRELin#/start')><cffile action="delete" file="#railixDeployJRELin#/start"></cfif>
			<cfif fileExists('#railixDeployJRELin#/stop')><cffile action="delete" file="#railixDeployJRELin#/stop"></cfif>
			<cfif fileExists('#railixDeployJRELin#/start.bat')><cffile action="delete" file="#railixDeployJRELin#/start.bat"></cfif>
			<cfif fileExists('#railixDeployJRELin#/stop.bat')><cffile action="delete" file="#railixDeployJRELin#/stop.bat"></cfif>
            
			<cffile action="copy" source="#source&"batch-#v3#/railix-local-jre-start.sh"#" destination="#railixDeployJRELin&"/start"#" mode="777">
        	<cffile action="copy" source="#source&"batch-#v3#/railix-local-jre-stop.sh"#" destination="#railixDeployJRELin&"/stop"#" mode="777">
            
		<cfset compress("tgz",railixDeployJRELin,railixDeployJRELinDownload&".tar.gz")>
		
	</cfif>




	<!--- War --->
	<cfif ListFind(form.create,'war')>
		<h1>Create War</h1>
				
		<cfset warSource=source&"war-"&v3&"/">
		<cfset warDeploy=deploy&"railo-"&form.version&"-war">
		<cfset warDeployZip=downloadCustom&"all/railo-"&form.version&".war">
		<cfif not DirectoryExists(warDeploy)><cfdirectory directory="#warDeploy#" action="create"></cfif>
	
		<cfset copyRec(warSource,warDeploy)>
		<cfset copyRec(jarSource,warDeploy&"/WEB-INF/lib/",false)>
		<cfset copyRec(railoJar,warDeploy&"/WEB-INF/lib/railo.jar")>
			<cfset copyRec(licence,warDeploy&"/License.txt")>
		<cfset copyRec(cfmlTemplates,warDeploy)>
		<cfset compress("zip",warDeploy,warDeployZip,false)>
		
		
	</cfif>


<pre>done</pre>

<cfelse>


<cfform action="#cgi.SCRIPT_NAME#">
<cfoutput>Version: <input type="text" name="version" value="#server.railo.version#"><br></cfoutput>

Create:<br>
	<input type="checkbox" name="create" value="jars" checked> Jars<br>
	<input type="checkbox" name="create" value="war" checked> War<br>
	<input type="checkbox" name="create" value="railix" checked> Railix<br>
	<input type="checkbox" name="create" value="resin" checked> Resin (Railo Server)<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="create" value="resin_exe" checked>  - Resin EXE<br>
	<br />
    



<input type="submit">
</cfform>


</cfif>