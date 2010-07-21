package railo.runtime.flash;

import java.io.InputStream;

import railo.runtime.Info;

public class FlashEngineDummy implements FlashEngine {

	/**
	 * @see railo.runtime.flash.FlashEngine#createFlash(java.lang.String)
	 */
	public InputStream createFlash(String input) {
		return new Info().getClass().getResourceAsStream("/resource/media/flash/railo.swf");
	}

}
