package coldfusion.server;

import java.awt.Image;
import java.io.File;
import java.util.Map;
import java.util.Properties;

public interface DocumentService extends Service {

	public abstract boolean registerFontFile(String arg0);

	public abstract boolean registerFontDirectory(String arg0);

	public abstract void FontDiscovery();

	public abstract boolean isFontPathRegistered(String arg0);

	public abstract boolean isFontPathRegisteredAsUserFont(String arg0);

	public abstract Map getAvailableFontsForPDF();

	public abstract Map getAvailableFontsForJDK();

	public abstract Map getAvailableFontFamiles();

	public abstract Map getConfigMap();

	public abstract Map getUserConfigMap();

	public abstract Map getFontInfoFromFile(String arg0);

	public abstract boolean isCommonFont(String arg0);

	public abstract Properties getAwtFontMapper();

	public abstract Properties getAwtFontMapperBak();

	public abstract File getWmimagefile();

	public abstract Image getWmimage();

}