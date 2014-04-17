component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote string function echoString(string sct) {
		return arguments.sct;
	}

	remote array function echoArray(array arr) {
		return arguments.arr;
	}
 
	remote string[] function echoStringArray(string[] data) {
		return arguments.data;
	} 

	remote string[][] function echoStringStringArray(string[][] data) {
		return arguments.data;
	}

	remote string[][][] function echoStringStringStringArray(string[][][] data) { 
		return arguments.data;
	}

}