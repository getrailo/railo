
<cffunction name="deleteExtension" returntype="void">
	<cfargument name="file">
    <cftry>
    	<cffile action="delete" file="#file#">
    	<cfcatch></cfcatch>
    </cftry>
</cffunction>

<cftry>

	<!---- load ExtensionManager ---->
    <cfset manager=createObject('component','extension.ExtensionManager')>
    
    <cfset display=true>
    <cfset detail=getDetail(url.provider,url.app)>
	<cfset isUpdate=StructKeyExists(detail,'installed')>
    <cfparam name="config" default="#manager.createConfig()#">
    
    <cfparam name="url.step" default="1">
    
    <cfif StructKeyExists(form,'previous')>
        <cfset url.step-->
    <cfelseif StructKeyExists(form,'next')>
        <cfset url.step++>
    </cfif>
    
    
    
    <!--- create app folder --->
    <cfset dest=manager.createAppFolder(url.provider,detail.app.name)>
    
    <!--- copy railo extension package to destination directory --->
    <cfset destFile=manager.copyAppFile(detail.app,dest).destFile>
    
    
    <!---- load xml ---->
    <cfset zip="zip://"&destFile&"!/">
    <cfset configFile=zip&"config.xml">
    <cfif not FileExists(configFile)>
        <cfset deleteExtension(destFile)>
        <cfthrow message="missing config file in extension package">
    </cfif>
    
    <!--- loadcfc --->
	<cfset install=manager.loadInstallCFC(zip)>
    
    
    


	<cfcatch>
    	<cfset display=false>
        <cfset deleteExtension(destFile)>
    	<cfset printError(cfcatch,true)>
    </cfcatch>
</cftry>
        

<!--- validate --->
<cfset valid=true>
<cfif StructKeyExists(form,"step") and not StructKeyExists(form,"previous")>
    <cftry>
    	<cfset rst=struct(fields:struct(),common:'')>
        <cfset install.validate(rst,zip,config,form.step)>
        
        <cfif len(rst.common) or structCount(rst.fields)>
        	<cfset valid=false>
            <cfset url.step=form.step>
            
            <cfif structCount(rst.fields)>
            	<cfset err=rst.fields>
            </cfif>
            <cfif len(rst.common)>
                <cfset printError(struct(message:rst.common))>
            </cfif>
        </cfif>
        
        <cfcatch>
			<cfset valid=false>
            <cfset url.step=form.step>
            <cfset printError(cfcatch,true)>
        </cfcatch>
    </cftry>
</cfif>


<!--- load XML --->
<cftry>
    <cfset xmlConfig=XMLParse(configFile,false).config>
    <cfset extForm=manager.translateXML(install,config,xmlConfig)>
	<cfset steps=extForm.getSteps()>

	<cfcatch>
    	<cfset display=false>
        <cfset deleteExtension(destFile)>
    	<cfset printError(cfcatch,true)>
    </cfcatch>
</cftry>



<!--- install --->
<cfset done=true>
<cfif (valid and StructKeyExists(form,"install")) or ( StructKeyExists(variables,"steps") and arrayLen(steps) EQ 0)>
    <cftry>
    	
		<cfset rst=struct(fields:struct(),common:'')>
    	<cfif isUpdate>
			<cfset message=install.update(rst,zip,config,detail.installed.config)>
        <cfelse>
            <cfset message=install.install(rst,zip,config)>
        </cfif>
        
        <cfif not IsDefined('message') or not len(message)>
        	<cfset message=stText.ext.installDone>
        </cfif>
        <cfif len(rst.common) or structCount(rst.fields)>
        	<cfset done=false>
            
            <cfif structCount(rst.fields)>
            	<cfset err=rst.fields>
            </cfif>
            <cfif len(rst.common)>
                <cfset printError(struct(message:rst.common))>
            </cfif>
        </cfif>
        
        <cfif done>
            <cfadmin 
                action="updateExtension"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                
                config="#config#"
                provider="#detail.url#"
                
                id="#detail.app.id#"
                version="#detail.app.version#"
                name="#detail.app.name#"
                label="#detail.app.label#"
                description="#detail.app.description#"	
                category="#detail.app.category#"	
                image="#detail.app.image#"	
                
                author="#detail.app.author#"	
                codename ="#detail.app.codename#"	
                video="#detail.app.video#"	
                support="#detail.app.support#"	
                documentation="#detail.app.documentation#"	
                forum="#detail.app.forum#"	
                mailinglist="#detail.app.mailinglist#"	
                network="#detail.app.network#"	
                _type="#detail.app.type#"
                created="#detail.app.created#"
                >
            <cfset session.confirm.text=message>    
            <cfset session.confirm.success=true>    
            <cflocation url="#request.self#?action=#url.action#&action2=confirm">
		</cfif>
        
        
        <cfcatch>
        	<cfset done=false>
            <cfset printError(cfcatch,true)>
        </cfcatch>
    </cftry>
</cfif>



<!--- generate form --->
<cfset fields="">

<cfif IsDefined('err._message')>
	<cfset printError(cfcatch,true)>
</cfif>

<cfif display and arrayLen(steps) GT 0>
<cfoutput>




<cfform action="#request.self#?action=#url.action#&action2=install2&provider=#url.provider#&app=#url.app#&step=#url.step#" method="post" enctype="multipart/form-data">

<cfif url.step GT arrayLen(steps)><cfset url.step=arrayLen(steps)></cfif>
<cfif url.step LT 1><cfset url.step=1></cfif>


<cfset stepLen=arrayLen(steps)>
<cfset isFirst=url.step EQ 1>
<cfset isLast=url.step EQ arrayLen(steps)>
<cfset formPrefix="dyn#url.step#_">


<cfset groups=steps[url.step].getGroups()>
<table class="tbl" border="0">
	<cfif stepLen GT 1>
		<cfset stepOf=replace(stText.ext.stepOf,'{current}',url.step)>
		<cfset stepOf=replace(stepOf,'{total}',arrayLen(steps))>
	<tr>
    	<td colspan="3" align="right" class="comment">#stepOf#</td>
    </tr>
	</cfif>
	<cfif len(trim(steps[url.step].getLabel())&trim(steps[url.step].getDescription()))>
    <tr>
    <td colspan="3"> <cfif len(trim(steps[url.step].getLabel()))><h2>#steps[url.step].getLabel()#</h2></cfif>#steps[url.step].getDescription()#<br /></td>
    </tr>
</cfif>
<cfloop array="#groups#" index="group">
	
   
    <tr>
    <td colspan="3">&nbsp;</td>
    </tr>
    <tr>
    <td width="1">&nbsp;</td>
    <td colspan="2"> <cfif len(trim(group.getLabel()))><b>#group.getLabel()#</b><br /></cfif>#group.getDescription()#</td>
    </tr>
	<cfset items=group.getItems()>
    <cfloop array="#items#" index="item">
        <cfset doBR=true>
        
        
        <!--- value --->
        <cfif StructKeyExists(form,formPrefix&item.getName())>
            <cfset value=form[formPrefix&item.getName()]>	
        	<cfset fromForm=value>
        <cfelseif StructKeyExists(detail,"installed") and StructKeyExists(detail.installed.config,item.getName())>
        	<!--- TODO direkt geht nicht !---><cfset tmp=detail.installed.config>
            <cfset value=tmp[item.getName()]>	
        <cfelse>
            <cfset value=item.getValue()>
        </cfif>
        
        
        <cfset fields&=","&formPrefix&item.getName()>
        <cfset isError=isDefined('err.'&item.getName())><!--- @todo wenn das feld message heisst gibt es konflikt --->
        <cfif item.getType() EQ "hidden">
        	 <input type="hidden" name="#formPrefix##item.getName()#" value="#HTMLEditFormat(item.getValue())#">
        <cfelse>
        <tr>
    		<td>&nbsp;</td>
            <cfif len(trim(item.getLabel()))><td class="tblHead" width="100">#item.getLabel()#</td></cfif>
            <td <cfif len(trim(item.getLabel())) EQ 0>colspan="2" width="500"<cfelse> width="400"</cfif> class="#iif(isError,de('tblContentRed'),de('tblContent'))#">
            <cfif isError><span class="CheckError">#err[item.getName()]#</span><br /></cfif>
                
            <!--- select --->
                <cfif item.getType() EQ "select">
                    <cfset options=item.getOptions()>
                    
                    <cfif arrayLen(options)>
                    <select name="#formPrefix##item.getName()#">
                    <cfloop array="#options#" index="option">
                        <cfif isDefined('fromForm')>
							<cfset selected=fromForm EQ option.getValue()>
                        <cfelse>
                            <cfset selected=option.getSelected()>
                        </cfif>
                        <option value="#HTMLEditFormat(option.getValue())#" <cfif selected>selected="selected"</cfif>>#option.getLabel()#</option>
                    </cfloop>
                    </select>
                    <cfelse>
                        <input type="text" name="#formPrefix##item.getName()#" value="#HTMLEditFormat(item.getValue())#"  <cfif item.getSelected()> checked="checked"</cfif>/>
                    </cfif>
            <!--- radio/checkbox --->
                <cfelseif item.getType() EQ "radio" or item.getType() EQ "checkbox">
                    <cfset options=item.getOptions()>
                    <cfif arrayLen(options)>
                    <cfset doBR=false>
                    <table class="tbl" cellpadding="0" cellspacing="0" border="0">
                    <cfloop array="#options#" index="option">
                        <cfif isDefined('fromForm')>
							<cfset selected=fromForm EQ option.getValue()>
                        <cfelse>
                            <cfset selected=option.getSelected()>
                        </cfif>
                        <tr>
                            <td valign="top"><input type="#item.getType()#" name="#formPrefix##item.getName()#" value="#HTMLEditFormat(option.getValue())#" <cfif selected> checked="checked"</cfif> /></td>
                            <td valign="top">
                            	<table border="0" cellpadding="5" cellspacing="0" class="tbl">
                                <tr>
                                	<td>#option.getLabel()#<cfif len(trim(option.getDescription()))><br /><span class="comment">#option.getDescription()#</span></cfif></td>
                                </tr>
                                
                                </table>
                            </td>
                        </tr>
                    </cfloop>
                    </table>
                    <cfelse>
						<cfif isDefined('fromForm')>
                            <cfset selected=fromForm EQ item.getValue()>
                        <cfelse>
                            <cfset selected=item.getSelected()>
                        </cfif>
                       <input type="#item.getType()#" name="#formPrefix##item.getName()#" value="#HTMLEditFormat(value)#"  <cfif selected> checked="checked"</cfif>/>
                    </cfif>
            <!--- text --->
                <cfelse>
                    <input type="#item.getType()#" name="#formPrefix##item.getName()#" value="#HTMLEditFormat(value)#" style="width:400px">
                </cfif>
                
                <cfif len(trim(item.getDescription()))>
                    <cfif doBR><br /></cfif><span class="comment">#item.getDescription()#</span>
                </cfif>
            </td>
        </tr>
        </cfif>
    </cfloop>
</cfloop>
	
    <tr>
    	
    <td>&nbsp;</td>
    <td colspan="2">
    <input type="hidden" name="step" value="#url.step#">
    <input type="hidden" name="repPath" value="#zip#">
    <input type="hidden" name="fields" value="#ListCompact(fields)#">
    
    <cfloop collection="#form#" item="key">
    	<cfif len(key) gt 3 and left(key,3) EQ "dyn">
        	<cfset stp=mid(key,4,find('_',key)-4)>
    		<cfif stp NEQ url.step><input type="hidden" name="#key#" value="#HTMLEditFormat( form[key])#"></cfif>
    	</cfif>
    </cfloop>
    
    
    
    
    <cfif stepLen EQ 1>
    	<input type="submit" class="submit" name="install" value="#stText.Buttons[iif(not StructKeyExists(detail,"installed"),de('install'),de('update'))]#">
    <cfelseif isFirst>
    	<input type="submit" class="submit" name="next" value="#stText.Buttons.next#">
    <cfelseif isLast>
    	<input type="submit" class="submit" name="previous" value="#stText.Buttons.previous#">
    	<input type="submit" class="submit" name="install" value="#stText.Buttons[iif(not StructKeyExists(detail,"installed"),de('install'),de('update'))]#">
    <cfelse>
    	<input type="submit" class="submit" name="previous" value="#stText.Buttons.previous#">
    	<input type="submit" class="submit" name="next" value="#stText.Buttons.next#">
    	
    </cfif>
    </td>
    </tr>
</cfform>
</table>
</cfoutput>
</cfif>


<!---
<cfadmin 
	action="getDatasources"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="datasources">
--->
