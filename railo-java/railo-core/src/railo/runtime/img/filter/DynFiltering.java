package railo.runtime.img.filter;import java.awt.image.BufferedImage;

import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;

public interface DynFiltering {
	public BufferedImage filter( BufferedImage src,Struct args) throws PageException;
}
