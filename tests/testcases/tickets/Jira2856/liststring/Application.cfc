component {

	this.name = hash( getCurrentTemplatePath() );
    request.baseURL="http://#cgi.HTTP_HOST##GetDirectoryFromPath(cgi.SCRIPT_NAME)#";
	request.currentPath=GetDirectoryFromPath(getCurrentTemplatePath());
	
	this.componentpaths = request.currentPath&"cfcs1/,"&request.currentPath&"cfcs2/";

} 