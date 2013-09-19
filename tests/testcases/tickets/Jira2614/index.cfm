<cfsetting showdebugoutput="no">

<cffile action="upload"
        filefield="file"
        destination="#expandpath('downloads/')#"
        nameconflict="overwrite">
		
