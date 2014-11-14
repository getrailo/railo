/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
component {


	function init() {

		this.resources = {};

		this.mimeTypes = {

			  CSS : "text/css"
			, JS  : "text/js"
			, GIF : "image/gif"
			, JPG : "image/jpeg"
			, PNG : "image/png"
		};

		return this;
	}


	function onMissingTemplate( target ) {

		var filename = right( target, 4 ) == ".cfm" ? left( target, len( target ) - 4 ) : target;

		var resInfo = getResInfo( filename );

//		systemOutput( "\_/--> response.isCommitted: " & getPageContext().getResponse().isCommitted(), true );

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

		var result = { path: expandPath( filename ) };

		result.exists = fileExists( result.path );

		if ( !result.exists )
			return result;

		var ext = listLast( filename, '.' );

		result.mimeType = this.mimeTypes.keyExists( ext ) ? this.mimeTypes[ ext ] : "";

		result.isText = left( result.mimeType, 4 ) == "text";

		result.contents = result.isText ? fileRead( result.path ) : fileReadBinary( result.path );

		result.etag = hash( result.contents );

		this.resources[ filename ] = result;

		return result;
	}

}