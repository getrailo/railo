component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote Query function echoQuery(Query sct) {
		return arguments.sct;
	}

	remote array function echoArray(array arr) {
		return arguments.arr;
	}
 
	remote Query[] function echoQueryArray(Query[] data) {
		return arguments.data;
	} 

	remote Query[][] function echoQueryQueryArray(Query[][] data) {
		return arguments.data;
	}

	remote Query[][][] function echoQueryQueryQueryArray(Query[][][] data) { 
		return arguments.data;
	}

}