component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote struct function echoStruct(struct sct) {
		return arguments.sct;
	}

	remote array function echoArray(array arr) {
		return arguments.arr;
	}
 
	remote struct[] function echoStructArray(struct[] data) {
		return arguments.data;
	} 

	remote struct[][] function echoStructStructArray(struct[][] data) {
		return arguments.data;
	}

	remote struct[][][] function echoStructStructStructArray(struct[][][] data) { 
		return arguments.data;
	}

}