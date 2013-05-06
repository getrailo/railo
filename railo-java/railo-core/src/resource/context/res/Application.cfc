component {


	this.name = "__RAILO_ADMIN_STATIC_RESOURCES";
	

	variables.isDebug = false;		// ATTN: set to false for production!


	function onApplicationStart() {

		Application.objects.missingTemplateHandler = new StaticResourceProvider();
	}


	function onMissingTemplate( target ) {

		if ( variables.isDebug )	onApplicationStart();		// disable cache for debug/develop

		Application.objects.missingTemplateHandler.onMissingTemplate( target );
	}

}