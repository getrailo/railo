component {

	this.name = hash( getCurrentTemplatePath() );
    request.baseURL="http://#cgi.HTTP_HOST##GetDirectoryFromPath(cgi.SCRIPT_NAME)#";
	request.currentPath=GetDirectoryFromPath(getCurrentTemplatePath());

	this.sameFormFieldsAsArray=isDefined("url.sameFormFieldsAsArray") && url.sameFormFieldsAsArray;
	this.sameURLFieldsAsArray=isDefined("url.sameURLFieldsAsArray") && url.sameURLFieldsAsArray;
} 