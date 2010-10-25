package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.HTMLEntities;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * slider applet tag
 */
public final class Slider extends TagImpl {
    
    private Struct params=new StructImpl();
    private InputBean input=new InputBean();
    private int height=40;
	private int width=0;
	private int vspace=0;
	private int hspace=0;
    
    /**
     * @see railo.runtime.ext.tag.TagImpl#release()
     */
    public void release() {
        super.release();

        input=new InputBean();
        params.clear();
        
        width=0;
        height=40;
        hspace=0;
        vspace=0;
    }
    
    /**
     * sets the attribut to tag
     * @param align
     * @throws ApplicationException
     */
    public void setAlign(String align) throws ApplicationException {
        align=align.trim().toLowerCase();
        if(List.listFind("top,left,bottom,baseline,texttop,absbottom,middle,absmiddle,right",align,",")>0) {
            params.setEL("align",align);
        }
        else
            throw new ApplicationException("attribute align ["+align+"] is invalid","valid alignments are [top,left,bottom,baseline,texttop,absbottom,middle,absmiddle,right]");
    }
    
    /**
     * sets the attribut to tag
     * @param bgcolor
     */
    public void setBgcolor(String bgcolor) {
        params.setEL("bgcolor",bgcolor);
    }
    
    /**
     * sets the attribut to tag
     * @param bold
     */
    public void setBold(boolean bold) {
        params.setEL("bold",Caster.toString(bold));
    }
    
    /**
     * sets the attribut to tag
     * @param font
     */
    public void setFont(String font) {
        params.setEL("font",font);
    }
    
    /**
     * sets the attribut to tag
     * @param fontsize
     */
    public void setFontsize(double fontsize) {
        params.setEL("fontsize",Caster.toString((int)fontsize));
    }
    
    /**
     * sets the attribut to tag
     * @param italic
     */
    public void setItalic(boolean italic) {
        params.setEL("italic",Caster.toString(italic));
    }
    
    /**
     * sets the attribut to tag
     * @param label
     */
    public void setLabel(String label) {
        params.setEL("label",label);
    }
    
    /**
     * sets the attribut to tag
     * @param lookandfeel
     * @throws ApplicationException
     */
    public void setLookandfeel(String lookandfeel) throws ApplicationException {
        lookandfeel=lookandfeel.trim().toLowerCase();
        if(lookandfeel.equals("motif"))params.setEL("lookandfeel",lookandfeel);
        else if(lookandfeel.equals("windows"))params.setEL("lookandfeel",lookandfeel);
        else if(lookandfeel.equals("metal"))params.setEL("lookandfeel",lookandfeel);
        else throw new ApplicationException("value of attribute lookAndFeel ["+lookandfeel+"] is invalid","valid values are [motif,windows,metal]");
        
    }
    
    /**
     * sets the attribut to tag
     * @param range
     * @throws PageException
     */
    public void setRange(String range) throws PageException {
        String errMessage="attribute range has a invalid value ["+range+"], must be string list with numbers";
        String errDetail="Example: [number_from,number_to], [number_from], [number_from,], [,number_to]";
        
        Array arr=List.listToArray(range,',');
        
        if(arr.size()==1) {
            double from=Caster.toDoubleValue(arr.get(1,null),Double.NaN);
            if(!Decision.isValid(from))throw new ApplicationException(errMessage,errDetail);
            input.setRangeMin(from);
            input.setRangeMax(Double.NaN);
            if(from<100)params.setEL("minimum",Caster.toString(from));
        }
        else if(arr.size()==2) {
            String strFrom=arr.get(1,"").toString().trim();
            double from=Caster.toDoubleValue(strFrom,Double.NaN);
            if(!Decision.isValid(from) && strFrom.length()>0) {
                throw new ApplicationException(errMessage,errDetail);
            }
            input.setRangeMin(from);
            
            String strTo=arr.get(2,"").toString().trim();
            double to=Caster.toDoubleValue(strTo,Double.NaN);
            if(!Decision.isValid(to) && strTo.length()>0) {
                throw new ApplicationException(errMessage,errDetail);
            }
            input.setRangeMax(to);
            
            if(from<to) {
                params.setEL("minimum",Caster.toString(from));
                params.setEL("maximum",Caster.toString(to));
            }
            
        }
        else throw new ApplicationException(errMessage,errDetail);
    }
    
    /**
     * sets the attribut to tag
     * @param message
     */
    public void setMessage(String message) {
        input.setMessage(message);
    }
    
    /**
     * sets the attribut to tag
     * @param name
     */
    public void setName(String name) {
        input.setName(name);
    }
    
    /**
     * sets the attribut to tag
     * @param notsupported
     */
    public void setNotsupported(String notsupported) {
        params.setEL("notsupported",notsupported);
    }
    
    /**
     * sets the attribut to tag
     * @param onerror
     */
    public void setOnerror(String onerror) {
        input.setOnError(onerror);
    }
    
    /**
     * sets the attribut to tag
     * @param onvalidate
     */
    public void setOnvalidate(String onvalidate) {
        input.setOnValidate(onvalidate);
    }

    /**
     * sets the attribut to tag
     * @param refreshlabel
     */
    public void setRefreshlabel(boolean refreshlabel) {
        params.setEL("refreshlabel",Caster.toString(refreshlabel));
    }
    
    /**
     * sets the attribut to tag
     * @param scale
     */
    public void setScale(double scale) {
        params.setEL("scale",Caster.toString((int)scale));
    }
    
    /**
     * sets the attribut to tag
     * @param textcolor
     */
    public void setTextcolor(String textcolor) {
        params.setEL("textcolor",textcolor);
    }
    
    /**
     * sets the attribut to tag
     * @param tickmarkimages
     */
    public void setTickmarkimages(String tickmarkimages) {
        params.setEL("tickmarkimages",tickmarkimages);
    }
    
    /**
     * sets the attribut to tag
     * @param tickmarklabels
     */
    public void setTickmarklabels(String tickmarklabels) {
        params.setEL("tickmarklabels",tickmarklabels);
    }
    
    /**
     * sets the attribut to tag
     * @param tickmarkmajor
     */
    public void setTickmarkmajor(boolean tickmarkmajor) {
        params.setEL("tickmarkmajor",Caster.toString(tickmarkmajor));
    }
    
    /**
     * sets the attribut to tag
     * @param tickmarkminor
     */
    public void setTickmarkminor(boolean tickmarkminor) {
        params.setEL("tickmarkminor",Caster.toString(tickmarkminor));
    }
    
    /**
     * sets the attribut to tag
     * @param value
     */
    public void setValue(double value) {
        params.setEL("value",Caster.toString((int)value));
    }
    
    /**
     * sets the attribut to tag
     * @param vertical
     */
    public void setVertical(boolean vertical) {
        params.setEL("vertical",Caster.toString(vertical));
        
    }

    /**
     * sets the attribut to tag
     * @param height
     */
    public void setHeight(double height) {
        this.height=(int)height;
    }
    
    /**
     * sets the attribut to tag
     * @param hspace
     */
    public void setHspace(double hspace) {
        this.hspace = (int)hspace;
    }
    
    /**
     * sets the attribut to tag
     * @param vspace
     */
    public void setVspace(double vspace) {
        this.vspace = (int) vspace;
    }
    
    /**
     * sets the attribut to tag
     * @param width
     */
    public void setWidth(double width) {
        this.width = (int) width;
    }

    /**
     * @see railo.runtime.ext.tag.TagImpl#doStartTag()
     */
    public int doStartTag() throws PageException {
        try {
            _doStartTag();
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
        return SKIP_BODY;
    }

    private void _doStartTag() throws PageException, IOException  {
        Tag parent = getParent();
        while(parent!=null && !(parent instanceof Form)){
			parent=parent.getParent();
		}
		
		if(!(parent instanceof Form)) {
		  throw new ApplicationException("Tag slider must be inside a form tag");
		}
		Form form = (Form)parent;
	    form.setInput(input);
	    String codebase;
        
        pageContext.write("<input type=\"hidden\" name=\""+enc(input.getName())+"\" value=\"\">");
        pageContext.write("<applet MAYSCRIPT code=\"thinlet.AppletLauncher\"");
        pageContext.write(" archive=\""+form.getArchive()+"?version=101\"");
        pageContext.write(" width=\""+width+"\"");
        if(!StringUtil.isEmpty(codebase=form.getCodebase()))
        	pageContext.write(" codebase=\""+codebase+"\"");
        if(height>0)pageContext.write(" height=\""+height+"\"");
        if(hspace>0)pageContext.write(" hspace=\""+hspace+"\"");
        if(vspace>0)pageContext.write(" vspace=\""+vspace+"\"");
        Object align = params.get("align",null);
        if(align!=null)pageContext.write(" align=\""+align+"\"");
        pageContext.write(">\n");
        pageContext.write("<param name=\"class\" value=\"railo.applet.SliderThinlet\"></param>\n");
        pageContext.write("<param name=\"form\" value=\""+enc(form.getName())+"\"></param>\n");
        pageContext.write("<param name=\"element\" value=\""+enc(input.getName())+"\"></param>\n");
        
        railo.runtime.type.Collection.Key[] keys = params.keys();
        railo.runtime.type.Collection.Key key;
        for(int i=0;i<keys.length;i++) {
            key = keys[i];
            pageContext.write("<param name=\"");
            pageContext.write(key.getString());
            pageContext.write("\" value=\"");
            pageContext.write(enc(Caster.toString(params.get(key,""))));
            pageContext.write("\"></param>\n");
        }
        pageContext.write("</applet>");
    }

    /**
     * html encode a string
     * @param str string to encode
     * @return encoded string
     */
    private String enc(String str) {
        return HTMLEntities.escapeHTML(str,HTMLEntities.HTMLV20);
    }
}