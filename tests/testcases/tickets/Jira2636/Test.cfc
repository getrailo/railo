<cfscript>
component {
	remote function base(required boolean b,numeric n) {
		return arguments;
	}

 	remote function object(obj) {
		return obj;
	}
	
 	remote boolean function boolean(boolean b) {
		return b;
	}
	
 	remote numeric function number(numeric n) {
		return n;
	}
	
 	remote array function array(array a) {
		return a;
	}
	
 	remote struct function struct(struct s) {
		return s;
	}
	
 	remote query function query(query q) {
		return q;
	}
	
 	remote component function component(component c) {
		return c;
	}
 	remote string function string(string s) {
		return s;
	}
	
} 
</cfscript>