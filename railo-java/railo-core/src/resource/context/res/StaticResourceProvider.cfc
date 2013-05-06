component {


	/**
	* if running from a mapped folder, pass virtualpath as the mapping virtual path, e.g. /railo-context
	*/
	function init( settings={} ) {

		param name="settings.virtualpath"	default="";

		var virtualpath = settings.virtualpath;		

		this.resources = {};

		this.mimeTypes = {

			  CSS : "text/css"
			, JS  : "text/js"
			, GIF : "image/gif"
			, JPG : "image/jpeg"
			, PNG : "image/png"
		};

		var basepath = getDirectoryFromPath( getCurrentTemplatePath() );

		this.isArchive = ( left( basepath, 6 ) == "zip://" );

		if ( this.isArchive ) {

			if ( basepath CT '!' )
				basepath = left( basepath, find( '!', basepath ) );

			/*  isArchive is true and no virtualpath was passed, try to guess it
			    this will only work if the mapping does not have slashes other than
 			    the 1st leading slash 	//*/
			if ( !len( virtualpath ) ) {

				var pos = len( CGI.CONTEXT_PATH ) ? 2 : 1;

				virtualpath = '/' & listGetAt( mid( CGI.SCRIPT_NAME, 2 ), pos, '/' );
			}
		}

		this.basepath = basepath;

		if ( len( virtualpath ) && ( "\/" CT right( virtualpath, 1 ) ) )
			virtualpath = left( virtualpath, len( virtualpath ) -1 );

		this.virtualpath = virtualpath;
		this.vpathlen = len( this.virtualpath );

		return this;
	}


	function onMissingTemplate( target ) {

		var filename = right( target, 4 ) == ".cfm" ? left( target, len( target ) - 4 ) : target;

		var resInfo = getResInfo( filename );

		if ( resInfo.exists ) {

			header name='Expires'       value='#getHttpTimeString( now() + 10 )#';
			header name='Cache-Control' value='max-age=#86400 * 10#';
			header name='ETag'          value=resInfo.etag;

			if ( CGI.HTTP_IF_NONE_MATCH == resInfo.etag ) {

				header statuscode='304' statustext='Not Modified';
				content reset=true type=resInfo.mimeType;
			} else {

				content reset=true type=resInfo.mimeType file=resInfo.path;
			}
		} else {
		
			header statuscode='404' statustext='Not Found';
		//	header statuscode='404' statustext='Not Found @ #resInfo.path#';

			systemOutput( "static resource #target# was not found @ #resInfo.path#", true, true );
		}

		return resInfo.exists;
	}


	private function getResInfo( filename ) {

		if ( structKeyExists( this.resources, filename ) )
			return this.resources[ filename ];

		var path = resolvePath( filename );

		if ( !fileExists( path ) )
			return { exists: false, path: path };

		var ext = listLast( filename, '.' );

		var mimeType = structKeyExists( this.mimeTypes, ext ) ? this.mimeTypes[ ext ] : "";

		var isText = left( mimeType, 4 ) == "text";

		var contents = isText ? fileRead( path ) : fileReadBinary( path );

		var result = { exists: true, etag: hash( contents ), isText: isText, mimeType: mimeType, path: path };

		this.resources[ filename ] = result;

		return result;
	}


	private function resolvePath( filename ) {

		if ( !this.isArchive )
			return expandPath( filename );

		if ( this.vpathlen && ( left( filename, this.vpathlen ) == this.virtualpath ) ) {

			filename = mid( filename, this.vpathlen + 1 );
		}

		return this.basepath & filename;
	}

}