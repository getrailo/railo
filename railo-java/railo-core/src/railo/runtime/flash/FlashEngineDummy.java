package railo.runtime.flash;

import java.io.InputStream;

import railo.runtime.engine.InfoImpl;

public class FlashEngineDummy implements FlashEngine {

	@Override
	public InputStream createFlash(String input) {
		return InfoImpl.class.getResourceAsStream("/resource/media/flash/railo.swf");
	}

}
