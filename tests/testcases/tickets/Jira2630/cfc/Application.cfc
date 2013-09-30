component {
	
	this.name = hash( getCurrentTemplatePath() );
    request.baseURL="http://#cgi.HTTP_HOST##GetDirectoryFromPath(cgi.SCRIPT_NAME)#";
	request.currentPath=GetDirectoryFromPath(getCurrentTemplatePath());
	if(isDefined('url.locale'))this.locale=url.locale;
	if(isDefined('url.timeZone'))this.timeZone=url.timeZone;
} 