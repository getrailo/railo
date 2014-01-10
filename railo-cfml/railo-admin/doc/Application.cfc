component {


	this.Name = "__RAILO_DOCS";


	function onApplicationStart() {

		Application.objects.utils = new DocUtils();
	}


	function onRequestStart( target ) {

		param name="cookie.railo_admin_lang" default="en";
		Session.railo_admin_lang = cookie.railo_admin_lang;

		param name="URL.item"   default="";
		param name="URL.format" default="html";
	}

}