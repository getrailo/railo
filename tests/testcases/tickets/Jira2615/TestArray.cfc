component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote struct function echoArray(array sct) {
		return arguments.sct;
	}

	remote array[] function echoArrayArray(array[] data) {
		return arguments.data;
	}
	remote array[][][] function echoArrayArrayArrayArray(array[][][] data) {
		return arguments.data;
	}

}