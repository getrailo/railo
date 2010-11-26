<cffunction name="addZero">
	<cfargument name="str">
 <!---   <while len(str) LT 2>
    	<cfset str="0"&str>
    </while>--->
    <cfreturn str>
</cffunction>
<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.submit#">
        	<cfset custom=struct()>
        
        
			<!--- custom --->
            <cfloop collection="#form#" item="key">
                <cfif left(key,13) EQ "custompart_d_">
                	<cfset name=mid(key,14,10000)>
                    <cfset custom[name]=(form["custompart_d_"&name]*86400)+(form["custompart_h_"&name]*3600)+(form["custompart_m_"&name]*60)+form["custompart_s_"&name]>
                </cfif>
            </cfloop>       
            <cfloop collection="#form#" item="key">
                <cfif left(key,7) EQ "custom_">
                    <cfset custom[mid(key,8,10000)]=form[key]>
                </cfif>
            </cfloop>
            
            <cfadmin 
                action="updateCacheConnection"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                
                
                name="#trim(form.name)#" class="#trim(form.class)#" 
                default="#StructKeyExists(form,'default')?form.default:""#" custom="#custom#"
                
                remoteClients="#request.getRemoteClients()#">
                    
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq "none">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<cfset isNew=false>
<cfif StructKeyExists(url,'name')>
<cfloop query="connections" >
	<cfif hash(connections.name) EQ url.name>
    	<cfset connection=querySlice(connections,connections.currentrow,1)>
        <cfset driver=drivers[connections.class]>
    </cfif> 
</cfloop>
<cfelse>
	<cfset isNew=true>
	<cfset connection=struct()>
	<cfset connection.class=form.class>
	<cfset connection.default=false>
	<cfset connection.name=form._name>
	<cfset connection.custom=struct()>
	<cfset driver=drivers[form.class]>
</cfif>
<cfoutput>



<!--- 
Error Output --->
<cfset printError(error)>

<h2>#driver.getLabel()# (#connection.class#)</h2>
#driver.getDescription()#

<table class="tbl" width="650">

<cfform action="#request.self#?action=#url.action#&action2=create#iif(isDefined('url.name'),de('&name=##url.name##'),de(''))#" method="post">
<cfinput type="hidden" name="class" value="#connection.class#">
<cfinput type="hidden" name="name" value="#connection.name#" >
<cfinput type="hidden" name="_name" value="#connection.name#" >
	<tr>
		<td width="150" class="tblHead" nowrap>#stText.Settings.cache.Name#</td>
		<td width="450" class="tblContent" nowrap>#connection.name#</td>
	</tr>
    
	<tr>
		<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="4"></td>
	</tr>
    
    <cfset custom=connection.custom>
    <cfloop array="#driver.getCustomFields()#" index="field">
    	<cfif isInstanceOf(field,"Group")>
        <tr>
            <td colspan="2"><cfmodule template="tp.cfm"  width="1" height="4"><h#field.getLevel()#>#field.getDisplayName()#</h#field.getLevel()#>#field.getDescription()#
            	</td>
        </tr>
		<cfcontinue>
		</cfif>
		
		
		<cfset doBR=true>
        <cfif StructKeyExists(custom,field.getName())>
            <cfset default=custom[field.getName()]>
        <cfelseif isNew>
            <cfset default=field.getDefaultValue()>
        <cfelse>
            <cfset default="">
        </cfif>
        <cfset type=field.getType()>
        <tr>
            <td class="tblHead" width="150">#field.getDisplayName()#</td>
            <td class="tblContent" width="300">
<cfif type EQ "text" or type EQ "password">
            <cfinput type="#type#" 
                name="custom_#field.getName()#" 
                value="#default#" style="width:300px" required="#field.getRequired()#" 
                message="Missing value for field #field.getDisplayName()#">
            
			
			
<cfelseif type EQ "textarea">
			<textarea style="width:450px;height:100px;" name="custom_#field.getName()#">#default#</textarea>
            
<cfelseif type EQ "time">
			<cfsilent>
            <cfset doBR=false>
            <cfif len(default) EQ 0>
            	<cfset default=0>
            <cfelse>
            	<cfset default=default+0>
            </cfif> 
			
            <cfset s=default>
            <cfset m=0>
            <cfset h=0>
            <cfset d=0>
            
			<cfif s GT 0>
				<cfset m=int(s/60)>
                <cfset s-=m*60>
            </cfif>
			<cfif m GT 0>
				<cfset h=int(m/60)>
                <cfset m-=h*60>
            </cfif>
            <cfif h GT 0>
				<cfset d=int(h/24)>
                <cfset h-=d*24>
            </cfif>
            </cfsilent>
            
            
            <table class="tbl">
		<tr>
			<td class="tblHead">#stText.General.Days#</td>
			<td class="tblHead">#stText.General.Hours#</td>
			<td class="tblHead">#stText.General.Minutes#</td>
			<td class="tblHead">#stText.General.Seconds#</td>
		</tr>
		
		<tr>
			<td class="tblContent"><cfinput type="text" 
                name="custompart_d_#field.getName()#" 
                value="#addZero(d)#" style="width:40px" required="#field.getRequired()#"   validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
			<td class="tblContent"><cfinput type="text" 
                name="custompart_h_#field.getName()#" 
                value="#addZero(h)#" style="width:40px" required="#field.getRequired()#"  maxlength="2"  validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
			<td class="tblContent"><cfinput type="text" 
                name="custompart_m_#field.getName()#" 
                value="#addZero(m)#" style="width:40px" required="#field.getRequired()#"  maxlength="2" validate="integer" 
                message="Missing value for field #field.getDisplayName()#"></td>
			<td class="tblContent"><cfinput type="text" 
                name="custompart_s_#field.getName()#" 
                value="#addZero(s)#" style="width:40px" required="#field.getRequired()#"  maxlength="2"  validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
		</tr>
		
		</table>
            
            
            
            
            
            
            
            
                
                
                
            <cfelseif type EQ "select">
                <cfoutput><br />
                <cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
                <select name="custom_#field.getName()#">
                    <cfif not field.getRequired()><option value=""> ---------- </option></cfif>
                    <cfif len(trim(default))>
                    <cfloop index="item" list="#field.getDefaultValue()#">
                    <option <cfif item EQ default>selected="selected"</cfif> >#item#</option>
                    </cfloop>
                    </cfif>
                </select>
                </cfoutput>
            <cfelseif type EQ "radio" or type EQ "checkbox">
            	<cfset desc=field.getDescription()>
            	<cfoutput>
                <cfif isStruct(desc) and StructKeyExists(desc,'_top')><div class="comment" style="padding-bottom:4px">#desc._top#</div></cfif>
                <cfloop index="item" list="#field.getValues()#">
                	<cfif listLen(field.getValues()) GT 1>
                    <cfset doBR=false>
                    <table cellpadding="0" cellspacing="2">
                    <tr>
                    	<td valign="top"><cfinput type="#type#" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">&nbsp;</td>
                        <td>
							#item#
                            <cfif isStruct(desc) and StructKeyExists(desc,item)><div class="comment" style="padding-bottom:4px">#desc[item]#</div></cfif>
                        </td>
                    </tr>
                    </table>
                    <cfelse>
                    	<cfinput type="#type#" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
                    </cfif>
                </cfloop>
                <cfif isStruct(desc) and StructKeyExists(desc,'_bottom')><div class="comment" style="padding-top:4px">#desc._bottom#</div></cfif>
                </cfoutput>
            </cfif>
			<cfif isSimpleValue(field.getDescription()) and len(trim(field.getDescription()))><cfif doBr><br /></cfif><span class="comment">#field.getDescription()#</span></cfif>
            </td>
        </tr>
        </cfloop>
    
    
	<tr>
		<td width="150" colspan="2"><br />
        	<table cellpadding="0" cellspacing="4">
            <tr>
            	<td><b>#stText.Settings.cache.default#</b><br />
                <select name="default">
                	<option value="">------</option>
                    <cfloop index="type" list="object,template,query,resource">
                	<option <cfif connection.default EQ type>selected="selected"</cfif> value="#type#">#stText.Settings.cache['defaultType'& type]#</option>
                    </cfloop>
                	</select><br />
                <span class="comment">#stText.Settings.cache.defaultDesc#</span></td>
            </tr>
            </table>
        
        </td>
		<td width="450"></td>
	</tr>
    
    <tr>
	<td colspan="2">
	<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.submit#"></td>
</tr>



</cfform>
</table>
<br><br>
</cfoutput>



    
    

    
    
<!---
<cfoutput>
<form action="#action('update')#" method="post">
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="##FFCC00"
		style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:1px;padding:10px;">
	<tr>
		<td valign="top" >
			<textarea style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:0px;" name="note" cols="40" rows="10">#req.note#</textarea>
		</td>
	</tr>
	</table>
	<br />
	<input class="submit" type="submit" name="submit" value="#lang.btnSubmit#" />
</form>
	
</cfoutput>
--->