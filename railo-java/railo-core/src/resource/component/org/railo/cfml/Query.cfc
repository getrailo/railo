component output="false" extends="Base" accessors="true"{
	
	property name="name" type="String";
	property name="qArray" type="Array";
	
	/*
	 * Tag Name
	 */
	variables.tagname = "query";
	
	
	/*
	 * @hint Constructor
	 */	
	public Base function init(){
		super.init(argumentCollection=arguments);
		return this;		
	}

	/*
	 * @hint Execute the query
	 */	
	public Result function execute(String sql=""){
		if(len(arguments.sql)){
			 setSql(arguments.sql);
		}
		
		//parse the sql into an array and save it
		setQArray(parseSql());
		
		// invoke the query and return the result
		return invokeTag();
	}

	/*
	 * @hint Parse the sql string converting into an array. 
	 *       Named and positional params will populate the array too.
	 */
	private Array function parseSql(){
		var result = [];
		var sql = trim(this.getSql());
		var namedParams = getNamedParams();
		var positionalParams = getPositionalParams(); 
		var positionalCursor = 1;
		var str = "";
		var cursor = 1;
		var lastMatch = 0;
		var regex = '[\s]+:[a-zA-Z1-9]*|\?';

		var match = refindNoCase(regex,sql,cursor,true);

		//if no match there is no need to enter in the loop
		if(match.pos[1] eq 0){
			result.add({type='String',value=sql});
			return result;
		}
		
		while(cursor neq 0){
			// trace the lastmatch position to add any string after the last match if found
			lastMatch =  cursor;
			
			match = refindNoCase(regex,sql,cursor,true);

			if(match.pos[1] gt 0){
				// string from cursor to match			
				str = mid(sql,cursor,match.pos[1] - cursor);
				result.add({type='String',value=str})
				
				//add match
				str = trim(mid(sql,match.pos[1],match.len[1]));
				if(left(str,1) eq ':'){
					result.add(findNamedParam(namedParams,right(str,len(str) - 1)));
				}else{
					result.add(positionalParams[positionalCursor]);
					positionalCursor ++;				
				}
			}
			
			// point the cursor after the match
			cursor = match.pos[1] + match.len[1];	
		}
		
		// no more match check if we have string to close the statement
		if(len(sql) gt lastMatch){
			str = mid(sql,lastMatch,len(sql));
			result.add({type='String',value=str})
		}
		
		return result;	
	}
	
	/*
	 * @hint Return just the named params
	 */	
	private Array function getNamedParams(){
		var params = getParams();
		var result = [];
		
		for(item in params){
			if(structKeyExists(item,'name')){
				result.add(item);
			}
		}
		
		return result;
	}


	/*
	 * @hint Return just the positional params
	 */	
	private Array function getPositionalParams(){
		var params = getParams();
		var result = [];

		for(item in params){
			if(not structKeyExists(item,'name')){
				result.add(item);
			}
		}
		
		return result;
	}
	

	/*
	 * @hint Scan the passed array looking for a "name" param match.
	 */		
	private Struct function findNamedParam(Array params,String name){
		for(item in params){
			if(structKeyExists(item,'name') && name == item.name){
				return item;
			}
		}
	
		throw(type="org.railo.cfml.query.namedParameterNotFoundException", message="The named parameter [#name#] has not been provided");
		
	}
						
}
