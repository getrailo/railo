package railo.runtime.video;

import railo.commons.io.res.Resource;

public interface VideoOutput {
	


	/**
	 * limit size of the output file
	 * @param size the size to set
	 */
	public void limitFileSizeTo(int size);

	/**
	 * set time offset of the output file based on input file in seconds
	 * @param offset
	 */
	public void setOffset(double offset);
	
	/**
	 * sets a comment to the output video
	 * @param comment
	 */
	public void setComment(String comment);
	
	/**
	 * sets a title to the output video
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * sets a author to the output video
	 * @param author
	 */
	public void setAuthor(String author);
	
	/**
	 * sets a copyright to the output video
	 * @param copyright
	 */
	public void setCopyright(String copyright);
	


	/**
	 * @param maxFrames the maxFrames to set
	 */
	public void setMaxFrames(long maxFrames);

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource);
	

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format);

	/**
	 * @param fileLimitation the fileLimitation to set
	 */
	public void setFileLimitation(int fileLimitation);


	/**
	 * @return the res
	 */
	public Resource getResource();


	/**
	 * @return the offset
	 */
	public double getOffset();

	/** 
	 * @return the comment
	 */
	public String getComment();


	/**
	 * @return the title
	 */
	public String getTitle();


	/**
	 * @return the author
	 */
	public String getAuthor();


	/**
	 * @return the copyright
	 */
	public String getCopyright();

	/**
	 * @return the fileLimitation
	 */
	public int getFileLimitation();
	
	/**
	 * @return the maxFrames
	 */
	public long getMaxFrames();

	/**
	 * @return the format
	 */
	public String getFormat();

	public void setFrameRate(int framerate);
	public int getFrameRate();


}
