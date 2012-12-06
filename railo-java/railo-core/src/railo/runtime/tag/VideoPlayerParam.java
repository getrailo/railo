package railo.runtime.tag;


import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagSupport;

/**
 * implementation of the tag Compiler
 */
public class VideoPlayerParam extends TagSupport {
	
	private VideoPlayerParamBean param=new VideoPlayerParamBean();
	
	@Override
	public void release() {
		super.release();
		param=new VideoPlayerParamBean();
	}
	
	/**
	 * @param video the video to set
	 * @throws PageException 
	 */
	public void setVideo(String video) throws PageException {
		param.setVideo(pageContext,video);
	}

	/**
	 * @param flash the flash to set
	 */
	public void setFlash(String flash) throws PageException {
		param.setFlash(pageContext,flash);
	}

	/**
	 * @param show the show to set
	 */
	public void setShow(String show) throws PageException {
		param.setShow(show);
	}

	/**
	 * @param index the index to set
	 * @throws PageException 
	 */
	public void setIndex(double dIndex) throws PageException {
		param.setIndex((int) dIndex);
	}
	/*public void setAutostart(boolean autostart) {
		param.setAutostart(autostart);
	}*/
	public void setTitle(String title) {
		param.setTitle(title);
	}

	/**
	 * @param preview the preview to set
	 * @throws PageException 
	 */
	public void setPreview(String preview) throws PageException {
		param.setImage(pageContext,preview);
	}
	public void setImage(String preview) throws PageException {
		param.setImage(pageContext,preview);
	}
	public void setLink(String link) {
		param.setLink(link);
	}
	public void setAuthor(String author) {
		param.setAuthor(author);
	}
		
	@Override
	public int doStartTag() throws PageException	{

		if(param.getFlash()==null && param.getVideo()==null)
			throw new ApplicationException("you have to define video or flash source");
		if(param.getFlash()!=null && param.getVideo()!=null)
			throw new ApplicationException("you can define only one source");
			 
		
		// get VideoPlayer Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof VideoPlayerJW)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof VideoPlayerJW) {
			VideoPlayerJW mail = (VideoPlayerJW)parent;
			mail.setParam(param);
		}
		else {
			throw new ApplicationException("Wrong Context, tag VideoPlayerParam must be inside a VideoPlayer tag");	
		}
		return SKIP_BODY;
	}
    
}