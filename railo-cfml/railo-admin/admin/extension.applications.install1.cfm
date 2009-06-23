
    <cfsilent>
    
        <!---- load ExtensionManager ---->
        <cfset manager=createObject('component','extension.ExtensionManager')>
    
    <cfset detail=getDetail(url.provider,url.app)>
    
    
    <cfadmin 
        action="getExtensionInfo"
        type="#request.adminType#"
        password="#session["password"&request.adminType]#"
        returnVariable="info">
    <cfset data.directory=info.directory>
    
    <cfset errMsg="">
    
    <!--- create app folder --->
    <cfset dest=manager.createAppFolder(url.provider,detail.app.name)>
    
    <!--- copy railo extension package to destination directory --->
    <cfset app=manager.copyAppFile(detail.app,dest)>
    </cfsilent>


	<cfif (not isDefined('app.url') or not len(app.url)) and not FileExists(app.destFile)>
    	<cfif app.error>
        	<cfset printError(app.message)>
        <cfelse>
        	<cfoutput>#app.message#</cfoutput>
        </cfif>
        
		<cfif len(cgi.http_referer)>
        <cfform action="#cgi.http_referer#" method="post">
            <cfoutput><input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#"></cfoutput>
        </cfform>
        </cfif>
        
    <cfelse>
        <!---- load license ---->
        <cfset zip="zip://"&app.destFile&"!/">
        <cfset licenseFile=zip&"license.txt">
        <cfif not FileExists(licenseFile)>
            <cflocation url="#request.self#?action=#url.action#&action2=install2&provider=#url.provider#&app=#url.app#" addtoken="no">
        </cfif>
        <cffile action="read" file="#licenseFile#" variable="license">
        
        
        
        <cfoutput>
        <cfform action="#request.self#?action=#url.action#&action2=install2&provider=#url.provider#&app=#url.app#" method="post">
        
        
        <h2>#stText.ext.LicenseAgreement#</h2>
        #stText.ext.LicenseAgreementDesc#
            <table class="tbl">
            
                <tr>
                    <td class="tblContent" width="400">
                    <textarea readonly="readonly" style="width:600px;height:200px;font-family:Courier New;font-size : 8pt;color:##595F73;border-style:solid;border-width:1px;border-color:##666666;">#license#</textarea>
                    </td>
                </tr>
            </table>
            <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.agree#">
        </cfform>
        
        </cfoutput>
    </cfif>

