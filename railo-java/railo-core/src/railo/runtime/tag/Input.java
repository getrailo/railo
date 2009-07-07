package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.HTMLEntities;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

// FUTURE tag input 
//attr validateAt impl tag atrr
//attr validate add support for submitOnce
// Added support for generating Flash and XML controls (specified in the cfform tag).
// Added support for preventing multiple submissions.
// attr mask impl. logik dahinter umsetzen

/**
 * 
 */
public class Input extends TagImpl {
    
    public static final short TYPE_SELECT=-1;
	public static final short TYPE_TEXT=0;
    public static final short TYPE_RADIO=1;
    public static final short TYPE_CHECKBOX=2;
    public static final short TYPE_PASSWORD=3;
    public static final short TYPE_BUTTON=4;
    public static final short TYPE_FILE=5;
    public static final short TYPE_HIDDEN=6;
    public static final short TYPE_IMAGE=7;
    public static final short TYPE_RESET=8;
    public static final short TYPE_SUBMIT=9;
    public static final short TYPE_DATEFIELD=10;
        
    public static final short VALIDATE_DATE=4;
    public static final short VALIDATE_EURODATE=5;
    public static final short VALIDATE_TIME=6;
    public static final short VALIDATE_FLOAT=7;
    public static final short VALIDATE_INTEGER=8;
    public static final short VALIDATE_TELEPHONE=9;
    public static final short VALIDATE_ZIPCODE=10;
    public static final short VALIDATE_CREDITCARD=11;
    public static final short VALIDATE_SOCIAL_SECURITY_NUMBER=12;
    public static final short VALIDATE_REGULAR_EXPRESSION=13;
    public static final short VALIDATE_NONE=14;

    public static final short VALIDATE_USDATE=15;
    public static final short VALIDATE_RANGE=16;
    public static final short VALIDATE_BOOLEAN=17;
    public static final short VALIDATE_EMAIL=18;
    public static final short VALIDATE_URL=19;
    public static final short VALIDATE_UUID=20;
    public static final short VALIDATE_GUID=21;
    public static final short VALIDATE_MAXLENGTH=22;
    public static final short VALIDATE_NOBLANKS=23;
    // TODO SubmitOnce

    /**
     * @param validate The validate to set.
     * @throws ApplicationException
     */
    public void setValidate(String validate) throws ApplicationException {
        validate=validate.toLowerCase().trim();
        if(validate.equals("creditcard"))		input.setValidate(VALIDATE_CREDITCARD);
        else if(validate.equals("date"))		input.setValidate(VALIDATE_DATE);
        else if(validate.equals("usdate"))		input.setValidate(VALIDATE_USDATE);
        else if(validate.equals("eurodate"))	input.setValidate(VALIDATE_EURODATE);
        else if(validate.equals("float"))		input.setValidate(VALIDATE_FLOAT);
        else if(validate.equals("numeric"))		input.setValidate(VALIDATE_FLOAT);
        else if(validate.equals("integer"))		input.setValidate(VALIDATE_INTEGER);
        else if(validate.equals("int"))			input.setValidate(VALIDATE_INTEGER);
        else if(validate.equals("regular_expression"))		input.setValidate(VALIDATE_REGULAR_EXPRESSION);
        else if(validate.equals("regex"))		input.setValidate(VALIDATE_REGULAR_EXPRESSION);
        else if(validate.equals("social_security_number"))input.setValidate(VALIDATE_SOCIAL_SECURITY_NUMBER);
        else if(validate.equals("ssn"))			input.setValidate(VALIDATE_SOCIAL_SECURITY_NUMBER);
        else if(validate.equals("telephone"))	input.setValidate(VALIDATE_TELEPHONE);
        else if(validate.equals("phone"))		input.setValidate(VALIDATE_TELEPHONE);
        else if(validate.equals("time"))		input.setValidate(VALIDATE_TIME);
        else if(validate.equals("zipcode"))		input.setValidate(VALIDATE_ZIPCODE);
        else if(validate.equals("zip"))			input.setValidate(VALIDATE_ZIPCODE);

        else if(validate.equals("range"))		input.setValidate(VALIDATE_RANGE);
        else if(validate.equals("boolean"))		input.setValidate(VALIDATE_BOOLEAN);
        else if(validate.equals("email"))		input.setValidate(VALIDATE_EMAIL);
        else if(validate.equals("url"))			input.setValidate(VALIDATE_URL);
        else if(validate.equals("uuid"))		input.setValidate(VALIDATE_UUID);
        else if(validate.equals("guid"))		input.setValidate(VALIDATE_GUID);
        else if(validate.equals("maxlength"))	input.setValidate(VALIDATE_MAXLENGTH);
        else if(validate.equals("noblanks"))	input.setValidate(VALIDATE_NOBLANKS);
        
        else throw new ApplicationException("attribute validate has a invalid value ["+validate+"]",
                "valid values for attribute validate are [creditcard, date, eurodate, float, integer, regular, social_security_number, telephone, time, zipcode]");
        
    }
    
    
	public static final String[] DAYNAMES_DEFAULT = new String[]{"S", "M", "T", "W", "Th", "F", "S"};
	public static final String[] MONTHNAMES_DEFAULT = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	

    Struct attributes=new StructImpl();
    InputBean input=new InputBean();
    String passthrough;
	
    String[] daynames=DAYNAMES_DEFAULT;
    String[] monthnames=MONTHNAMES_DEFAULT;
    
    boolean enabled=true;
    boolean visible=true;
    String label;
    String tooltip;
    String validateAt;
	double firstDayOfWeek=0;
	String mask;
	
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        input=new InputBean();
        attributes.clear();
        passthrough=null;

        daynames=DAYNAMES_DEFAULT;
        monthnames=MONTHNAMES_DEFAULT;
        enabled=true;
        visible=true;
        label=null;
        tooltip=null;
        validateAt=null;
        firstDayOfWeek=0;
        mask=null;    
    }
    
    /**
     * @param cssclass The cssclass to set.
     */
    public void setClass(String cssclass) {
        attributes.setEL("class",cssclass);
    }
    /**
     * @param cssstyle The cssstyle to set.
     */
    public void setStyle(String cssstyle) {
        attributes.setEL("style",cssstyle);
    }
   /**
     * @param id The id to set.
     */
    public void setId(String id) {
        attributes.setEL("id",id);
    }
    
    public void setAccept(String accept) {
        attributes.setEL("accept",accept);
    }
    
    public void setAccesskey(String accesskey) {
        attributes.setEL("accesskey",accesskey);
    }
    
    public void setAlign(String align) {
    	attributes.setEL("align",align);
    }
    
    public void setAlt(String alt) {
        attributes.setEL("alt",alt);
    }

	public void setBorder(String border) {
        attributes.setEL("border",border);
    }
    
    public void setDatafld(String datafld) {
        attributes.setEL("datafld",datafld);
    }
    
    public void setDatasrc(String datasrc) {
        attributes.setEL("datasrc",datasrc);
    }
    
    public void setLang(String lang) {
        attributes.setEL("lang",lang);
    }
    
    public void setDir(String dir) {
    	//dir=dir.trim();
    	//String lcDir=dir.toLowerCase();
    	//if( "ltr".equals(lcDir) || "rtl".equals(lcDir)) 
    		attributes.setEL("dir",dir);
    	
    	//else throw new ApplicationException("attribute dir for tag input has a invalid value ["+dir+"], valid values are [ltr, rtl]");
    }
    
    public void setDataformatas(String dataformatas) {
    	dataformatas=dataformatas.trim();
    	//String lcDataformatas=dataformatas.toLowerCase();
    	//if( "plaintext".equals(lcDataformatas) || "html".equals(lcDataformatas)) 
    		attributes.setEL("dataformatas",dataformatas);
    	
    	//else throw new ApplicationException("attribute dataformatas for tag input has a invalid value ["+dataformatas+"], valid values are [plaintext, html");
    }

    public void setDisabled(String disabled) {
    	// alles ausser false ist true
    	//if(Caster.toBooleanValue(disabled,true)) 
    		attributes.setEL("disabled",disabled);
    }

    public void setEnabled(String enabled) {
    	// alles ausser false ist true
    	//setDisabled(Caster.toString(!Caster.toBooleanValue(enabled,true))); 
		attributes.setEL("enabled",enabled);
    }
    
    
    
    
    public void setIsmap(String ismap) {
    	// alles ausser false ist true
    	//if(Caster.toBooleanValue(ismap,true)) attributes.setEL("ismap","ismap");
    	attributes.setEL("ismap",ismap);
    }
    
    public void setReadonly(String readonly) {
    	// alles ausser false ist true
    	//if(Caster.toBooleanValue(readonly,true)) attributes.setEL("readonly","readonly");
    	attributes.setEL("readonly",readonly);
    }
    
    public void setUsemap(String usemap) {
        attributes.setEL("usemap",usemap);
    }

    /**
     * @param onBlur The onBlur to set.
     */
    public void setOnblur(String onBlur) {
        attributes.setEL("onblur",onBlur);
    }
    /**
     * @param onChange The onChange to set.
     */
    public void setOnchange(String onChange) {
        attributes.setEL("onchange",onChange);
    }
    /**
     * @param onClick The onClick to set.
     */
    public void setOnclick(String onClick) {
        attributes.setEL("onclick",onClick);
    }
    /**
     * @param onDblclick The onDblclick to set.
     */
    public void setOndblclick(String onDblclick) {
        attributes.setEL("ondblclick",onDblclick);
    }
    /**
     * @param onFocus The onFocus to set.
     */
    public void setOnfocus(String onFocus) {
        attributes.setEL("onfocus",onFocus);
    }
    /**
     * @param onKeyDown The onKeyDown to set.
     */
    public void setOnkeydown(String onKeyDown) {
        attributes.setEL("onkeydown",onKeyDown);
    }
    /**
     * @param onKeyPress The onKeyPress to set.
     */
    public void setOnkeypress(String onKeyPress) {
        attributes.setEL("onkeypress",onKeyPress);
    }
    /**
     * @param onKeyUp The onKeyUp to set.
     */
    public void setOnkeyup(String onKeyUp) {
        attributes.setEL("onKeyUp",onKeyUp);
    }
    /**
     * @param onMouseDown The onMouseDown to set.
     */
    public void setOnmousedown(String onMouseDown) {
        attributes.setEL("onMouseDown",onMouseDown);
    }
    /**
     * @param onMouseMove The onMouseMove to set.
     */
    public void setOnmousemove(String onMouseMove) {
        attributes.setEL("onMouseMove",onMouseMove);
    }
    /**
     * @param onMouseUp The onMouseUp to set.
     */
    public void setOnmouseup(String onMouseUp) {
        attributes.setEL("onMouseUp",onMouseUp);
    }
    /**
     * @param onMouseUp The onMouseUp to set.
     */
    public void setOnselect(String onselect) {
        attributes.setEL("onselect",onselect);
    }
    /**
     * @param onMouseOut The onMouseOut to set.
     */
    public void setOnmouseout(String onMouseOut) {
        attributes.setEL("onMouseOut",onMouseOut);
    }
    /**
     * @param onMouseOver The onKeyPress to set.
     */
    public void setOnmouseover(String onMouseOver) {
        attributes.setEL("onMouseOver",onMouseOver);
    }
    /**
     * @param tabIndex The tabIndex to set.
     */
    public void setTabindex(String tabIndex) {
        attributes.setEL("tabindex",tabIndex);
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        attributes.setEL("title",title);
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        attributes.setEL("value",value);
    }
    /**
     * @param size The size to set.
     */
    public void setSize(String size) {
        attributes.setEL("size",size);
    }
    /**
     * @param maxLength The maxLength to set.
     */
    public void setMaxlength(double maxLength) {
        input.setMaxLength((int)maxLength);
        attributes.setEL("maxLength",Caster.toString(maxLength));
    }
    /**
     * @param checked The checked to set.
     */
    public void setChecked(String checked) {
    	// alles ausser false ist true
    	if(Caster.toBooleanValue(checked,true)) attributes.setEL("checked","checked");
    } 
    /**
     * @param daynames The daynames to set.
     * @throws ApplicationException 
     */
    public void setDaynames(String listDaynames) throws ApplicationException {
    	String[] arr = List.listToStringArray(listDaynames, ',');
    	if(arr.length==7)
    		throw new ApplicationException("value of attribute [daynames] must contain a string list with 7 values, now there are "+arr.length+" values");
    	this.daynames=arr;
    }
    /**
     * @param daynames The daynames to set.
     * @throws ApplicationException 
     */
    public void setFirstdayofweek(double firstDayOfWeek) throws ApplicationException {
    	if(firstDayOfWeek<0 || firstDayOfWeek>6)
    		throw new ApplicationException("value of attribute [firstDayOfWeek] must conatin a numeric value between 0-6");
    	this.firstDayOfWeek=firstDayOfWeek;
    }
    /**
     * @param daynames The daynames to set.
     * @throws ApplicationException 
     */
    public void setMonthnames(String listMonthNames) throws ApplicationException {
    	String[] arr = List.listToStringArray(listMonthNames, ',');
    	if(arr.length==12)
    		throw new ApplicationException("value of attribute [MonthNames] must contain a string list with 12 values, now there are "+arr.length+" values");
    	this.monthnames=arr;
    }
    
    /**
     * @param daynames The daynames to set.
     */
    public void setLabel(String label) {
    	this.label=label;
    }
    /**
     * @param daynames The daynames to set.
     */
    public void setMask(String mask) {
    	this.mask=mask;
    }
    /**
     * @param daynames The daynames to set.
     */
    public void setNotab(String notab) {
    	attributes.setEL("notab",notab);
    }
    /**
     * @param daynames The daynames to set.
     */
    public void setHspace(String hspace) {
    	attributes.setEL("hspace",hspace);
    }
    
    /**
     * @param type The type to set.
     * @throws ApplicationException
     */
    public void setType(String type) throws ApplicationException {    	
    	type=type.toLowerCase().trim();
        if(		"checkbox".equals(type))	input.setType(TYPE_CHECKBOX);
        else if("password".equals(type))	input.setType(TYPE_PASSWORD);
        else if("text".equals(type))		input.setType(TYPE_TEXT);
        else if("radio".equals(type))		input.setType(TYPE_RADIO);
        else if("button".equals(type))		input.setType(TYPE_BUTTON);
        else if("file".equals(type))		input.setType(TYPE_FILE);
        else if("hidden".equals(type))		input.setType(TYPE_HIDDEN);
        else if("image".equals(type))		input.setType(TYPE_IMAGE);
        else if("reset".equals(type))		input.setType(TYPE_RESET);
        else if("submit".equals(type))		input.setType(TYPE_SUBMIT);
        else if("datefield".equals(type))	input.setType(TYPE_DATEFIELD);
        
        else throw new ApplicationException("attribute type has a invalid value ["+type+"]","valid values for attribute type are " +
        		"[checkbox, password, text, radio, button, file, hidden, image, reset, submit, datefield]");

        attributes.setEL("type",type);
    }
    
    /**
     * @param onError The onError to set.
     */
    public void setOnerror(String onError) {
        input.setOnError(onError);
    }
    /**
     * @param onValidate The onValidate to set.
     */
    public void setOnvalidate(String onValidate) {
        input.setOnValidate(onValidate);
    }
    /**
     * @param passthrough The passThrough to set.
     * @throws PageException
     */
    public void setPassthrough(Object passthrough) throws PageException {
        if(passthrough instanceof Struct) {
            Struct sct = (Struct) passthrough;
            railo.runtime.type.Collection.Key[] keys=sct.keys();
            railo.runtime.type.Collection.Key key;
            for(int i=0;i<keys.length;i++) {
                key=keys[i];
                attributes.setEL(key,sct.get(key,null));
            }
        }
        else this.passthrough = Caster.toString(passthrough);
        
        //input.setPassThrough(passThrough);
    }
    /**
     * @param pattern The pattern to set.
     * @throws ExpressionException 
     */
    public void setPattern(String pattern) throws ExpressionException {
        input.setPattern(pattern);
    }
    /**
     * @param range The range to set.
     * @throws PageException
     */
    public void setRange(String range) throws PageException {
        String errMessage="attribute range has a invalid value ["+range+"], must be string list with numbers";
        String errDetail="Example: [number_from,number_to], [number_from], [number_from,], [,number_to]";
        
        Array arr=List.listToArray(range,',');
        
        if(arr.size()==1) {
            double from=Caster.toDoubleValue(arr.get(1,null),Double.NaN);
            if(Double.isNaN(from))throw new ApplicationException(errMessage,errDetail);
            input.setRangeMin(from);
            input.setRangeMax(Double.NaN);
        }
        else if(arr.size()==2) {
            String strFrom=arr.get(1,"").toString().trim();
            double from=Caster.toDoubleValue(strFrom,Double.NaN);
            if(Double.isNaN(from) && strFrom.length()>0) {
                throw new ApplicationException(errMessage,errDetail);
            }
            input.setRangeMin(from);
            
            String strTo=arr.get(2,"").toString().trim();
            double to=Caster.toDoubleValue(strTo,Double.NaN);
            if(Double.isNaN(to) && strTo.length()>0) {
                throw new ApplicationException(errMessage,errDetail);
            }
            input.setRangeMax(to);
            
        }
        else throw new ApplicationException(errMessage,errDetail);
    }
    /**
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
        input.setRequired(required);
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        attributes.setEL("name",name);
        input.setName(name);
    }
    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        if(!StringUtil.isEmpty(message))input.setMessage(message);
    }

    /**
	 *
	 * @see railo.runtime.ext.tag.TagImpl#doEndTag()
	 */
	public int doEndTag() throws PageException {
		try {
            _doEndTag();
        }
		catch (IOException e) {
           throw Caster.toPageException(e);
        }
        return EVAL_PAGE;
	}

	private void _doEndTag() throws PageException, IOException {
        // check attributes
    	if(input.getValidate()==VALIDATE_REGULAR_EXPRESSION && input.getPattern()==null) {
            throw new ApplicationException("when validation type regular_expression is seleted, the pattern attribute is required");
        }

    	Tag parent = getParent();
        while(parent!=null && !(parent instanceof Form)){
			parent=parent.getParent();
		}
        if(parent instanceof Form) {
		    Form form = (Form)parent;
		    form.setInput(input);
		    if(input.getType()==TYPE_DATEFIELD && form.getFormat()!=Form.FORMAT_FLASH)
		    	throw new ApplicationException("type [datefield] is only allowed if form format is flash");
		}
		else { 
		    throw new ApplicationException("Tag must be inside a form tag");
		}
        draw();
    }

    void draw() throws IOException, PageException {

        // start output
        pageContext.write("<input");
        
        railo.runtime.type.Collection.Key[] keys = attributes.keys();
        railo.runtime.type.Collection.Key key;
        for(int i=0;i<keys.length;i++) {
            key = keys[i];
            pageContext.write(" ");
            pageContext.write(key.getString());
            pageContext.write("=\"");
            pageContext.write(enc(Caster.toString(attributes.get(key,null))));
            pageContext.write("\"");
           
        }
        
        if(passthrough!=null) {
            pageContext.write(" ");
            pageContext.write(passthrough);
        }
        pageContext.write(">");
	}

	/**
     * html encode a string
     * @param str string to encode
     * @return encoded string
     */
    String enc(String str) {
        return HTMLEntities.escapeHTML(str,HTMLEntities.HTMLV20);
    }

	/**
	 * @return the monthnames
	 */
	public String[] getMonthnames() {
		return monthnames;
	}

	/**
	 * @param monthnames the monthnames to set
	 */
	public void setMonthnames(String[] monthnames) {
		this.monthnames = monthnames;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		attributes.setEL("height",height);
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(InputBean input) {
		this.input = input;
	}

	/**
	 * @param passthrough the passthrough to set
	 */
	public void setPassthrough(String passthrough) {
		this.passthrough = passthrough;
	}

	/**
	 * @param tooltip the tooltip to set
	 * @throws ApplicationException 
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @param validateAt the validateAt to set
	 * @throws ApplicationException 
	 */
	public void setValidateat(String validateAt) throws ApplicationException {
		this.validateAt = validateAt;
		throw new ApplicationException("attribute validateAt is not supportrd for tag input ");

	}

	/**
	 * @param visible the visible to set
	 * @throws ApplicationException 
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @param width the width to set
	 * @throws ApplicationException 
	 */
	public void setWidth(String width) {
		attributes.setEL("width", width);
	}
	
	
    private ExpressionException notSupported(String label) {
		return new ExpressionException("attribute ["+label+"] is not supported");
	}
    
    


    public void setAutosuggest(String autosuggest) throws ExpressionException {
    	throw notSupported("autosuggest");
    	//attributes.setEL("bind",bind);
    }
    public void setAutosuggestbinddelay(double autosuggestBindDelay) throws ExpressionException {
    	throw notSupported("autosuggestBindDelay");
    	//attributes.setEL("bind",bind);
    }
    public void setAutosuggestminlength(double autosuggestMinLength) throws ExpressionException {
    	throw notSupported("autosuggestMinLength");
    	//attributes.setEL("bind",bind);
    }

    public void setBind(String bind) throws ExpressionException {
    	throw notSupported("bind");
    	//attributes.setEL("bind",bind);
    }
    
    public void setBindattribute(String bindAttribute) throws ExpressionException {
    	throw notSupported("bindAttribute");
    	//attributes.setEL("bind",bind);
    }
    
    public void setBindonload(boolean bindOnLoad) throws ExpressionException {
    	throw notSupported("bindOnLoad");
    	//attributes.setEL("bind",bind);
    }

    public void setDelimiter(String delimiter) throws ExpressionException {
    	throw notSupported("delimiter");
    	//attributes.setEL("bind",bind);
    }
    public void setMaxresultsdisplayed(double maxResultsDisplayed) throws ExpressionException {
    	throw notSupported("maxResultsDisplayed");
    	//attributes.setEL("bind",bind);
    }
    public void setOnbinderror(String onBindError) throws ExpressionException {
    	throw notSupported("onBindError");
    	//attributes.setEL("bind",bind);
    }
    public void setShowautosuggestloadingicon(boolean showAutosuggestLoadingIcon) throws ExpressionException {
    	throw notSupported("showAutosuggestLoadingIcon");
    	//attributes.setEL("bind",bind);
    }
    public void setSourcefortooltip(String sourceForTooltip) throws ExpressionException {
    	throw notSupported("sourceForTooltip");
    	//attributes.setEL("bind",bind);
    }
    public void setSrc(String src) throws ExpressionException {
    	throw notSupported("src");
    	//attributes.setEL("src",src);
    }
    public void setTypeahead(boolean typeahead) throws ExpressionException {
    	throw notSupported("typeahead");
    	//attributes.setEL("src",src);
    }
    
    
    
}