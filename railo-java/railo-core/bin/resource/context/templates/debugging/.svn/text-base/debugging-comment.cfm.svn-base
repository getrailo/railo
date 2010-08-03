<cfadmin 
	action="getDebugData"
	returnVariable="debugging"><!--

<cfscript>
 function print(string title,array labels, query data) {
 	
	// get maxlength of columns
	var lengths=array();
	var i=1;
	var y=1;
	var tmp=0;
	var total=1;
	var collen=arrayLen(labels);
	for(;i LTE collen;i=i+1) {
		lengths[i]=len(labels[i]);
		for(y=1;y LTE data.recordcount;y=y+1) {
		
			data[labels[i]][y]=trim(rereplace(data[labels[i]][y],"[[:space:]]+"," ","all"));
		
			tmp=len(data[labels[i]][y]);
			if(tmp GT lengths[i])lengths[i]=tmp;
		}
		lengths[i]=lengths[i]+3;
		total=total+lengths[i];
	}
	
	// now wrie out
	writeOutput(chr(13));
	writeOutput(RepeatString("=",total)&chr(13));
	writeOutput(ljustify(" "&ucase(title)&" " ,total));
	writeOutput(chr(13));
	writeOutput(RepeatString("=",total)&chr(13));
	for(y=1;y LTE collen;y=y+1) {
		writeOutput(ljustify("| "&uCase(labels[y])&" " ,lengths[y]));
	}
	writeOutput("|"&chr(13));
	
	for(i=1;i LTE data.recordcount;i=i+1) {
		writeOutput(RepeatString("-",total)&chr(13));
		for(y=1;y LTE collen;y=y+1) {
			writeOutput(ljustify("| "&data[labels[y]][i]&" " ,lengths[y]));
		}
		writeOutput("|"&chr(13));
	}
	writeOutput(RepeatString("=",total)&chr(13)&chr(13));
	
	
	
 }
 
 writeOutput("RAILO DEBUGGING OUTPUT"&chr(13));
 
 print("Pages",array('src','count','load','query','app','total'),debugging.pages);
 print("Queries",array('src','datasource','name','sql','time','count'),debugging.queries);
 print("Timers",array('template','label','time'),debugging.timers);
 print("Traces",array('template','type','category','text','line','varname','varvalue','time'),debugging.traces);
 </cfscript>-->