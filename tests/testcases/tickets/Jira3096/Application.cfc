component {

	this.name = hash( getCurrentTemplatePath() );
    request.baseURL="http://#cgi.HTTP_HOST##GetDirectoryFromPath(cgi.SCRIPT_NAME)#";
	request.currentPath=GetDirectoryFromPath(getCurrentTemplatePath());

	this.javasettings={
    	LoadPaths = [request.currentPath&"jira3096.jar"], 
    	loadColdFusionClassPath = true, 
    	reloadOnChange = false
	}
} 