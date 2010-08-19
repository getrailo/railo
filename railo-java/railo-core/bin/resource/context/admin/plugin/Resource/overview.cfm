ddd
<cfoutput>

<script>

function selectAll(field) {
	var form=field.form;
	for(var key in form.elements){
		if((""+form.elements[key].name).indexOf("path")==0){
			form.elements[key].checked=field.checked;
		}
	}
}
</script>

<h2>#lang.info#</h2>
#lang.descInfo#<br /><br />
<table class="tbl">
<tr>
    <td valign="top" class="tblHead">#lang.size#</td>
    <td valign="top" class="tblContent">#req.dspSize#</td>
</tr>
<tr>
    <td valign="top" class="tblHead">#lang.countDir#</td>
    <td valign="top" class="tblContent">#req.countDir#</td>
</tr>
<tr>
    <td valign="top" class="tblHead">#lang.countFile#</td>
    <td valign="top" class="tblContent">#req.countFile#</td>
</tr>
</table>
<br />

<h2>#lang.listing#</h2>
#lang.descListing#<br /><br />
<table class="tbl">
<cfform action="#action('delete')#" method="post">
    <tr>
        <td width="20"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)">
            </td>
        <td width="400" class="tblHead" nowrap>#lang.path#</td>
        <td width="100" class="tblHead" nowrap>#lang.size#</td>
        <td width="50" class="tblHead" nowrap>#lang.age#</td>
    </tr>
    <cfloop query="req.listing">
        <!--- and now display --->
    <tr>
        <td>
        <table border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td>
            <input type="checkbox" class="checkbox" name="path[]" value="#req.listing.path#">
            </td>
        </tr>
        </table>
        </td>
        <td class="tblContent" nowrap>#req.listing.path#</td>
        <td class="tblContent" nowrap>#req.listing.dspSize#</td>
        <td class="tblContent" nowrap>#dateFormat(req.listing.dateLastModified)# #TimeFormat(req.listing.dateLastModified)#</td>
    </tr>
    </cfloop>
    <tr>
        <td colspan="4">
         <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <td>&nbsp;</td>		
            <td><img src="resources/img/#request.adminType#-bgcolor.gif.cfm" width="1" height="20"></td>
            <td></td>
         </tr>
         <tr>
            <td></td>
            <td valign="top"><img src="resources/img/#request.adminType#-bgcolor.gif.cfm" width="1" height="14"><img 
            src="resources/img/#request.adminType#-bgcolor.gif.cfm" width="36" height="1"></td>
            <td>&nbsp;
            <input type="submit" class="submit" name="delete" value="#lang.btnDelete#">
            </td>	
        </tr>
         </table>
         </td>
    </tr>
</cfform>
</table>

</cfoutput>
