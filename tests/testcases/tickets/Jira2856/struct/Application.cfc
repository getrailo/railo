component {

	this.name = hash( getCurrentTemplatePath() );
    request.baseURL="http://#cgi.HTTP_HOST##GetDirectoryFromPath(cgi.SCRIPT_NAME)#";
	request.currentPath=GetDirectoryFromPath(getCurrentTemplatePath());
	
	this.componentpaths = {a:request.currentPath&"cfcs1/",b:request.currentPath&"cfcs2/"};

} 