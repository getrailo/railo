component {


	this.metadata.hint = "use this tag as a child of the Backup tag to specify which resources should be backed up";
	this.metadata.attributetype = "dynamic";
	this.metadata.attributes = {

		  type: { required: false, default: "directory", hint="possible values: [directory], website, railo-web, railo-web-config, railo-server, railo-server-config" }
		, path: { required: false, default: "", hint="required if type is directory" }
		, filter: { required: false, default: "", hint="if passed, only files that pass the filter will be backed up. prefix the filter with a ! to negate, so for example !*.log will accept all files that do not end with the suffix .log" }
	};


	/** cfc custom tag init( boolean hasEndTag, component parent ) */
	function init( boolean hasEndTag, component parent ) {

		return this;
	}


	/** cfc custom tag onStartTag( struct attributes, struct caller ):boolean */
	boolean function onStartTag( struct attributes, struct caller ) {

		param name="attributes.path" default="";
		param name="attributes.filter" default="";

		if ( !isDefined( "attributes.type" ) || !len( attributes.type ) )
			attributes.type = "directory";

		if ( attributes.type == "directory" && !len( attributes.path ) )
			throw( type="InvalidArgument", message="attribute path must be passed when type is set to directory" );

		associate basetag="backup" datacollection="backupParams";

		return false;
	}


}