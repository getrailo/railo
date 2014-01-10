package railo.runtime.tag;

import java.io.IOException;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.image.ImageNew;
import railo.runtime.functions.image.ImageWrite;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class Sprite extends TagImpl {
	
	private String _id;
	private String _ids;
	private String _srcs;
	
	String src;
		

	
	@Override
	public void release()	{
		this._id=null;
		this._ids=null;
		this.src=null;
		this._srcs=null;
		super.release();
	}
	
	
	
	public void set_ids(String _ids){
		this._ids=_ids;
	}

	public void set_id(String _id){
		this._id=_id;
	}

	public void set_srcs(String _srcs){
		this._srcs=_srcs;
	}

	public void setSrc(String src){
		this.src=src;
	}
	
	@Override

	public int doStartTag() throws PageException	{
		try {
			return _doStartTag();
		} catch (Throwable e) {
			throw Caster.toPageException(e);
		}
	}
	
	
	public int _doStartTag() throws Throwable	{
		
		// write out div for single item
		pageContext.write("<div id=\""+_id+"\"></div>");
		
		
		
		
		
		
		// handle all items
		if(!StringUtil.isEmpty(_ids)) {
			String[] ids=ListUtil.listToStringArray(_ids, ',');
			String[] strSrcs=ListUtil.listToStringArray(_srcs, ',');
			Resource[] srcs=new Resource[strSrcs.length];
			Image[] images=new Image[strSrcs.length];
			for(int i=0;i<srcs.length;i++){
				srcs[i]=ResourceUtil.toResourceExisting(pageContext, strSrcs[i]);
				images[i] = new Image(srcs[i]);
			}
			
			// TODO use the same resource as for cfimage
			PageSource ps = pageContext.getCurrentTemplatePageSource();
			Resource curr = ps.getResource();
			Resource dir = curr.getParentResource();
			Resource cssDir = dir.getRealResource("css");
			Resource pathdir = cssDir;
			cssDir.mkdirs();
			
			
			//the base name for the files we are going to create as a css and image
			String baseRenderedFileName = MD5.getDigestAsString(_ids);
			Resource cssFileName = cssDir.getRealResource(baseRenderedFileName+".css");
			Resource imgFileName = pathdir.getRealResource(baseRenderedFileName+"."+ResourceUtil.getExtension(src,""));
			
			//if the files don't exist, then we create them, otherwise
			boolean bCreate = !cssFileName.isFile() || !imgFileName.isFile();
			
			
			//Are we going to create it, let's say no
			String css = "";
			if(bCreate){
				int imgMaxHeight = 0;
				int imgMaxWidth = 0;
				Image img;
				int actualWidth,actualHeight;
				//Setup the max height and width of the new image. 
				for(int i=0;i<srcs.length;i++){
					img = images[i];
					
					//set the image original height and width 
					actualWidth = img.getWidth();;
					actualHeight = img.getHeight();
									
									
					
					//Check if there is a height, 
					imgMaxHeight += actualHeight;
					if(actualWidth  > imgMaxWidth) imgMaxWidth  =  actualWidth;
				}
				
				//Create the new image (hence we needed to do two of these items)
				Image spriteImage = (Image) ImageNew.call(pageContext,"", ""+imgMaxWidth,""+imgMaxHeight, "argb");
				
				int placedHeight = 0;
				//Loop again but this time, lets do the copy and paste
				for(int i=0;i<srcs.length;i++){
					img = images[i];
					spriteImage.paste(img,1,placedHeight);
					
						css += "#"+ids[i]+" {\n\tbackground: url("+baseRenderedFileName+"."+ResourceUtil.getExtension(strSrcs[i],"")+") 0px -"+placedHeight+"px no-repeat; width:"+img.getWidth()+"px; height:"+img.getHeight()+"px;\n} \n";
						placedHeight += img.getHeight();
				}
				
				//Now Write the CSS and the Sprite Image
				
				ImageWrite.call(pageContext, spriteImage, imgFileName.getAbsolutePath());
				IOUtil.write(cssFileName, css,"UTF-8",false);
				
			}

			
			//pageContext.write("<style>"+css+"</style>");

			try {
				((PageContextImpl)pageContext).getRootOut()
					.appendHTMLHead("<link rel=\"stylesheet\" href=\"css/"+baseRenderedFileName+".css\" type=\"text/css\" media=\"screen\" title=\"no title\" charset=\"utf-8\">");
			} catch (IOException e) {
				Caster.toPageException(e);
			} 
			
		}
		
		
		
		
		return SKIP_BODY;
	}



	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}