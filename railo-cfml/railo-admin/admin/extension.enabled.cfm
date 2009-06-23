<cfadmin 
    action="getExtensionInfo"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    returnVariable="info">
<cfdump var="#info#">