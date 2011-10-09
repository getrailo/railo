package railo.runtime.img.coder;

import java.awt.image.BufferedImage;
import java.io.IOException;

import railo.commons.io.res.Resource;

class DoubleCoder extends Coder {

	private Coder first;
	private Coder second;
	
	private String[] writerFormatNames;
	private String[] readerFormatNames;

	public DoubleCoder(Coder first, Coder second){
		this.first=first;
		this.second=second;
	}
	
	@Override
	public BufferedImage toBufferedImage(Resource res, String format) throws IOException {
		try {
			return first.toBufferedImage(res, format);
		}
		catch(Throwable t){
			return second.toBufferedImage(res, format);
		}
	}

	@Override
	public BufferedImage toBufferedImage(byte[] bytes, String format) throws IOException {
		try {
			return first.toBufferedImage(bytes, format);
		}
		catch(Throwable t){
			return second.toBufferedImage(bytes, format);
		}
	}

	@Override
	public final String[] getWriterFormatNames() {
		if(writerFormatNames==null)	{
			writerFormatNames=JRECoder.mixTogetherOrdered(first.getWriterFormatNames(),second.getWriterFormatNames());
		}
		return writerFormatNames;
	}
	
	@Override
	public final String[] getReaderFormatNames() {
		if(readerFormatNames==null){
			readerFormatNames=JRECoder.mixTogetherOrdered(first.getReaderFormatNames(),second.getReaderFormatNames());
		}
		return readerFormatNames;
	}

}
