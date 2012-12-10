component {


	this.metadata.hint = "use this tag to create a backup archive";
	this.metadata.attributetype = "fixed";
	this.metadata.attributes = {

		  name: { required: true, type: "string", hint: "Name of the backup job" }
		, destination: { required: true, type: "string", hint: "Folder in which to store the backup" }
		, async: { required: false, type: "boolean", default: false, hint: "Pass true to run the backup process in a separate thread" }
		, copiesToRetain: { required: false, type: "int", default: 0, hint: "Number of copies to keep after successful backup" }
		, retainUnique: { required: false, type: "boolean", default: false, hint: "Pass true to keep unique copies of backup only" }
		, onComplete: { required: false, default: "", hint: "a function that will be called upon successful completion" }
		, onError: { required: false, default: "", hint: "a function that will be called in case of an error" }
	};

	this.suffix = ".backup.zip";
	this.railoSettingsPrefix = "__railo-backup";


	/** cfc custom tag init( boolean hasEndTag, component parent ) */
	public function init( boolean hasEndTag, component parent ) {

		return this;
	}


	/** cfc custom tag onStartTag( struct attributes, struct caller ):boolean */
	boolean function onStartTag( struct attributes, struct caller ) {

		setting requestTimeout=600;

		attributes.timestamp = "#dateFormat( now(), 'yyyymmdd' )##timeFormat( now(), 'HHmmss' )#";

		this.attributes = attributes;

		return true;
	}


	/** cfc custom tag onEndTag( struct attributes, struct caller, string generatedContent ):boolean */
	boolean function onEndTag( struct attributes, struct caller, string generatedContent ) {

		if ( this.attributes.async ) {

			thread name="backup-#this.attributes.timestamp#" {

				try {

					this.doBackup();

				} catch ( ex ) {

					onError( ex, "End" );
				}
			}
		} else {

			this.doBackup();
		}


		return false;
	}


	/** cfc custom tag onError( struct cfcatch, string source ):boolean */
	boolean function onError( struct cfcatch, string source ) {

		if ( isCustomFunction( this.attributes.onError ) ) {

			this.attributes.onError( { exception: cfcatch, source: source } );

			return false;
		}

		return true;
	}


	/** cfc custom tag onFinally():boolean */
	boolean function onFinally() {

		return true;
	}


	function doBackup() localMode=true {

		param name="this.backupParams" default=[];

		if ( this.backupParams.isEmpty() ) {

			this.backupParams.append( { type: "directory", path: expandPath( '/' ), filter="!*.log" } );
		}

		if ( "/\" NCT right( this.attributes.destination, 1 ) )
			this.attributes.destination &= '/';

		if ( !directoryExists( this.attributes.destination ) )
			directoryCreate( this.attributes.destination );
		

		var archiveMask = "#this.attributes.name#-*#this.suffix#";
		var archiveName = replace( archiveMask, "*", this.attributes.timestamp );
		var archivePath = "#this.attributes.destination##archiveName#";

		zip action="zip" file=archivePath {

			loop array=this.backupParams item="Local.ai" {

				switch( ai.type ) {

					case "directory":
						
						zipparam source=ai.path filter=ai.filter;
						break;
					
					case "railo-server":
						zipparam source=getDirectoryFromPath( expandPath( "{railo-server}" ) ) prefix="#this.railoSettingsPrefix#/railo-server" filter="!*.log, *.scpt, memory.bin";
						break;
					
					case "railo-server-config":
						zipparam source=expandPath( "{railo-server}/railo-server.xml" ) entryPath="#this.railoSettingsPrefix#/railo-server.xml";
						break;

					case "railo-web":
						zipparam source=getDirectoryFromPath( expandPath( "{railo-web}" ) ) prefix="#this.railoSettingsPrefix#/railo-web" filter="!*.log, *.scpt";
						break;

					case "railo-web-config":
						zipparam source=expandPath( "{railo-web}/railo-web.xml.cfm" ) entryPath="#this.railoSettingsPrefix#/railo-web.xml.cfm";
						break;

					default:
						
						throw ( type="InvalidArgument", message="[#ai.type#] is not a valid BackupParam type.  valid types are [directory]" );
				}
			}
		};


		var archiveInfo = getZipArchiveInfo( "#this.attributes.destination##archiveName#" );

		fileSetLastModified( archivePath, archiveInfo.lastModified );


		directory action="list" directory=this.attributes.destination name="Local.qBackups" filter="#archiveMask#" sort="name desc";

		// remove duplicate backups
		if ( this.attributes.retainUnique && qBackups.recordCount > 1 ) {

			if ( archiveInfo.size == qBackups.size[ 2 ] && archiveInfo.lastModified == qBackups.dateLastModified[ 2 ] ) {

				var archiveInfo2 = getZipArchiveInfo( "#this.attributes.destination##qBackups.name[ 2 ]#" );

				if ( archiveInfo.count == archiveInfo2.count && archiveInfo.hash == archiveInfo2.hash ) {

					fileDelete( "#this.attributes.destination##qBackups.name[ 2 ]#" );

					directory action="list" directory=this.attributes.destination name="Local.qBackups" filter="#archiveMask#" sort="name desc";
				}
			}
		}

		// remove old backups
		if ( ( this.attributes.copiesToRetain > 0 ) && ( qBackups.recordCount > this.attributes.copiesToRetain ) ) {

			loop from=this.attributes.copiesToRetain+1 to=qBackups.recordCount index="Local.ii" {

				fileDelete( this.attributes.destination & qBackups.name[ ii ] );
			}
		}
		

		if ( isCustomFunction( this.attributes.onComplete ) ) {

			var values = { directory=this.attributes.destination, filename=archiveName };

			structAppend( values, archiveInfo );

			this.attributes.onComplete( values );
		}
	}


	function areArchivesEqual( string filename1, string filename2 ) {

		var zinfo1 = getZipArchiveInfo( filename1 );
		var zinfo2 = getZipArchiveInfo( filename2 );

		return ( zinfo1.count == zinfo2.count && zinfo1.size == zinfo2.size && zinfo1.lastModified == zinfo2.lastModified && zinfo1.hash == zinfo2.hash );
	}


	function getZipArchiveInfo( string filename ) {

		zip action="list" file=filename name="Local.qZip";

		var result = {};

		result.count = qZip.recordCount;

		result.lastModified = "01/01/2000";

		var sb = createObject( 'java', 'java.lang.StringBuilder' ).init( javaCast( 'int', 1024 ) );

		loop query=qZip {

			if ( qZip.dateLastModified > result.lastModified )
				result.lastModified = qZip.dateLastModified;

			sb.append( qZip.crc ).append( qZip.name ).append( qZip.size );
		}

		result.hash = hash( sb.toString() );

		var fileInfo = getFileInfo( filename );

		result.size = fileInfo.size;

		return result;
	}
}