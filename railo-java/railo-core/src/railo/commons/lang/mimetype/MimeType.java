package railo.commons.lang.mimetype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
import railo.runtime.type.UDF;

public class MimeType {
	
	private static int DEFAULT_MXB=100000;
	private static double DEFAULT_MXT=5;
	private static double DEFAULT_QUALITY=1;
	
	public static final MimeType ALL = new MimeType(null,null,null);
	public static final MimeType APPLICATION_JSON = new MimeType("application","json",null);
	public static final MimeType APPLICATION_XML = new MimeType("application","xml",null);
	public static final MimeType APPLICATION_WDDX = new MimeType("application","wddx",null);
	public static final MimeType APPLICATION_CFML = new MimeType("application","cfml",null);
	public static final MimeType APPLICATION_PLAIN = new MimeType("application","lazy",null);

	public static final MimeType IMAGE_GIF = new MimeType("image","gif",null);
	public static final MimeType IMAGE_JPG = new MimeType("image","jpeg",null);
	public static final MimeType IMAGE_PNG = new MimeType("image","png",null);
	public static final MimeType IMAGE_TIFF = new MimeType("image","tiff",null);
	public static final MimeType IMAGE_BMP = new MimeType("image","bmp",null);
	public static final MimeType IMAGE_WBMP = new MimeType("image","vnd.wap.wbmp",null);
	public static final MimeType IMAGE_FBX = new MimeType("image","fbx",null);
	public static final MimeType IMAGE_PNM = new MimeType("image","x-portable-anymap",null);
	public static final MimeType IMAGE_PGM = new MimeType("image","x-portable-graymap",null);
	public static final MimeType IMAGE_PBM = new MimeType("image","x-portable-bitmap",null);
	public static final MimeType IMAGE_ICO = new MimeType("image","ico",null);
	public static final MimeType IMAGE_PSD = new MimeType("image","psd",null);
	public static final MimeType IMAGE_ASTERIX = new MimeType("image",null,null);
	public static final MimeType APPLICATION_JAVA = new MimeType("application","java",null);
	
	private String type;
	private String subtype;
	//private double quality;
	//private int mxb;
	//private double mxt;
	private Map<String,String> properties;
	private double q=-1;

	
	private MimeType(String type, String subtype, Map<String,String> properties) {
		//if(quality<0 || quality>1)
		//	throw new RuntimeException("quality must be a number between 0 and 1, now ["+quality+"]");

		
		this.type=type;
		this.subtype=subtype;
		this.properties=properties;
		//this.quality=quality;
		//this.mxb=mxb;
		//this.mxt=mxt;
	}
	


	private static MimeType getInstance(String type, String subtype, Map<String,String> properties) {
		// TODO read this from a external File
		if("text".equals(type)) {
			if("xml".equals(subtype)) return new MimeType("application", "xml", properties);
			if("x-json".equals(subtype)) return new MimeType("application", "json", properties);
			if("javascript".equals(subtype)) return new MimeType("application", "json", properties);
			if("x-javascript".equals(subtype)) return new MimeType("application", "json", properties);
			if("wddx".equals(subtype)) return new MimeType("application", "wddx", properties);
		}
		else if("application".equals(type)) {
			if("x-json".equals(subtype)) return new MimeType("application", "json", properties);
			if("javascript".equals(subtype)) return new MimeType("application", "json", properties);
			if("x-javascript".equals(subtype)) return new MimeType("application", "json", properties);
			
			if("jpg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("x-jpg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			
			if("png".equals(subtype)) return new MimeType("image", "png", properties);
			if("x-png".equals(subtype)) return new MimeType("image", "png", properties);

			if("tiff".equals(subtype)) return new MimeType("image", "tiff", properties);
			if("tif".equals(subtype)) return new MimeType("image", "tiff", properties);
			if("x-tiff".equals(subtype)) return new MimeType("image", "tiff", properties);
			if("x-tif".equals(subtype)) return new MimeType("image", "tiff", properties);

			if("fpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			if("x-fpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			if("vnd.fpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			if("vnd.netfpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			
			if("ico".equals(subtype)) return new MimeType("image", "ico", properties);
			if("x-ico".equals(subtype)) return new MimeType("image", "ico", properties);
			if("x-icon".equals(subtype)) return new MimeType("image", "ico", properties);
			
			if("psd".equals(subtype)) return new MimeType("image", "psd", properties);
			if("x-photoshop".equals(subtype)) return new MimeType("image", "psd", properties);
			if("photoshop".equals(subtype)) return new MimeType("image", "psd", properties);
		}
		else if("image".equals(type)) {
			if("gi_".equals(subtype)) return new MimeType("image", "gif", properties);
			
			if("pjpeg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("jpg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("jpe".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("vnd.swiftview-jpeg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("pipeg".equals(subtype)) return new MimeType("image", "jpeg", properties);
			if("jp_".equals(subtype)) return new MimeType("image", "jpeg", properties);

			if("x-png".equals(subtype)) return new MimeType("image", "png", properties);

			if("tif".equals(subtype)) return new MimeType("image", "tiff", properties);
			if("x-tif".equals(subtype)) return new MimeType("image", "tiff", properties);
			if("x-tiff".equals(subtype)) return new MimeType("image", "tiff", properties);

			if("x-fpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			if("vnd.fpx".equals(subtype)) return new MimeType("image", "fpx", properties);
			if("vnd.netfpx".equals(subtype)) return new MimeType("image", "fpx", properties);

			if("x-portable/graymap".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("portable graymap".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("x-pnm".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("pnm".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);

			if("x-portable/graymap".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("portable graymap".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("x-pgm".equals(subtype)) return new MimeType("image", "x-portable-anymap", properties);
			if("pgm".equals(subtype)) return new MimeType("image", "x-portable-graymap", properties);

			if("portable bitmap".equals(subtype)) return new MimeType("image", "x-portable-bitmap", properties);
			if("x-portable/bitmap".equals(subtype)) return new MimeType("image", "x-portable-bitmap", properties);
			if("x-pbm".equals(subtype)) return new MimeType("image", "x-portable-bitmap", properties);
			if("pbm".equals(subtype)) return new MimeType("image", "x-portable-bitmap", properties);

			if("x-ico".equals(subtype)) return new MimeType("image", "ico", properties);
			if("x-icon".equals(subtype)) return new MimeType("image", "ico", properties);

			if("x-photoshop".equals(subtype)) return new MimeType("image", "psd", properties);
			if("photoshop".equals(subtype)) return new MimeType("image", "psd", properties);
		}
		else if("zz-application".equals(type)) {
			if("zz-winassoc-psd".equals(subtype)) return new MimeType("image", "psd", properties);
		}
		/*
		
		if("image/x-p".equals(mt)) return "ppm";
		if("image/x-ppm".equals(mt)) return "ppm";
		if("image/ppm".equals(mt)) return "ppm";

		*/
		return new MimeType(type, subtype, properties);
	}

	
	/**
	 * returns a mimetype that match given string
	 * @param strMimeType
	 * @return
	 */
	public static MimeType getInstance(String strMimeType){
		strMimeType=strMimeType.trim();
		if("*".equals(strMimeType)) return ALL;
		
		String[] arr = List.listToStringArray(strMimeType, ';');
		
		String[] arrCT = List.listToStringArray(arr[0].trim(), '/');
		
		String type=arrCT[0].trim();
		if("*".equals(type)) type=null;
		
		if(arrCT.length==1) return getInstance(type,null,null);
		
		String subtype=arrCT[1].trim();
		if("*".equals(subtype)) subtype=null;
		
		if(arr.length==1) return getInstance(type,subtype,null);
		

		final Map<String,String> properties=new HashMap<String, String>();
		String entry;
		String[] _arr;
		for(int i=1;i<arr.length;i++){
			entry=arr[i].trim();
			_arr = List.listToStringArray(entry, '=');
			if(arr.length<2) continue;
			properties.put(_arr[0].trim().toLowerCase(), _arr[1].trim());
			//if(_arr[0].equals("q")) quality=Caster.toDoubleValue(_arr[1],1);
			//if(_arr[0].equals("mxb")) mxb=Caster.toIntValue(_arr[1],100000);
			//if(_arr[0].equals("mxt")) mxt=Caster.toDoubleValue(_arr[1],5);
		}
		return getInstance(type,subtype,properties);
	}

	public static MimeType[] getInstances(String strMimeTypes, char delimiter) {
		if(StringUtil.isEmpty(strMimeTypes,true)) return new MimeType[0];
		String[] arr = List.trimItems(List.listToStringArray(strMimeTypes, delimiter));
		MimeType[] mtes=new MimeType[arr.length];
		for(int i=0;i<arr.length;i++){
			mtes[i]=getInstance(arr[i]);
		}
		return mtes;
	}




	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}
	
	/**
	 * @return the type
	 */
	String getTypeNotNull() {
		return type==null?"*":type;
	}

	/**
	 * @return the subtype
	 */
	String getSubtypeNotNull() {
		return subtype==null?"*":subtype;
	}
	

	public double getQuality() {
		if(q==-1){
			if(properties==null) q=DEFAULT_QUALITY;
			else q= Caster.toDoubleValue(properties.get("q"),DEFAULT_QUALITY);
		}
		return q;
	}
	/*
	public int getMxb() {
		return Caster.toIntValue(properties.get("mxb"),DEFAULT_MXB);
	}

	public double getMxt() {
		return Caster.toDoubleValue(properties.get("mxt"),DEFAULT_MXT);
	}*/

	public boolean hasWildCards() {
		return type==null || subtype==null;
	}

	/**
	 * checks if given mimetype is covered by current mimetype
	 * @param other
	 * @return
	 */
	public boolean match(MimeType other){
		if(this==other) return true;
		if(type!=null && other.type!=null && !type.equals(other.type)) return false;
		if(subtype!=null && other.subtype!=null && !subtype.equals(other.subtype)) return false;
		return true;
	}
	
	public MimeType bestMatch(MimeType[] others){
		MimeType best=null;
		
		for(int i=0;i<others.length;i++){
			if(match(others[i]) && (best==null || best.getQuality()<others[i].getQuality())) {
				best=others[i];
			}
		}
		return best;
	}
	


	/**
	 * checks if other is from the same type, just type and subtype are checked, properties (q,mxb,mxt) are ignored.
	 * @param other
	 * @return
	 */
	public boolean same(MimeType other){
		if(this==other) return true;
		return getTypeNotNull().equals(other.getTypeNotNull()) && getSubtypeNotNull().equals(other.getSubtypeNotNull());
	}
	
	public boolean equals(Object obj){
		if(obj==this) return true;
		
		
		MimeType other;
		if(obj instanceof MimeType)
			other=(MimeType) obj;
		else if(obj instanceof String)
			other=MimeType.getInstance((String)obj);
		else
			return false;
		
		if(!same(other)) return false;
		
		return other.toString().equals(toString());
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append(type==null?"*":type);
		sb.append("/");
		sb.append(subtype==null?"*":subtype);
		if(properties!=null){
			String[] keys = properties.keySet().toArray(new String[properties.size()]);
			Arrays.sort(keys);
			//Iterator<Entry<String, String>> it = properties.entrySet().iterator();
			//Entry<String, String> e;
			for(int i=0;i<keys.length;i++){
				sb.append("; ");
				sb.append(keys[i]);
				sb.append("=");
				sb.append(properties.get(keys[i]));
			}
		}
		return sb.toString();
	}



	public static MimeType toMimetype(int format, MimeType defaultValue) {
		switch(format){
		case UDF.RETURN_FORMAT_JSON:return MimeType.APPLICATION_JSON;
		case UDF.RETURN_FORMAT_WDDX:return MimeType.APPLICATION_WDDX;
		case UDF.RETURN_FORMAT_SERIALIZE:return MimeType.APPLICATION_CFML;
		case UDF.RETURN_FORMAT_XML:return MimeType.APPLICATION_XML;
		case UDF.RETURN_FORMAT_PLAIN:return MimeType.APPLICATION_PLAIN;
		
		}
		return defaultValue;
	}
	
	public static int toFormat(MimeType mt, int defaultValue) {
		if(MimeType.APPLICATION_JSON.same(mt)) return  UDF.RETURN_FORMAT_JSON;
		if(MimeType.APPLICATION_WDDX.same(mt)) return  UDF.RETURN_FORMAT_WDDX;
		if(MimeType.APPLICATION_CFML.same(mt)) return  UDF.RETURN_FORMAT_SERIALIZE;
		if(MimeType.APPLICATION_XML.same(mt)) return  UDF.RETURN_FORMAT_XML;
		if(MimeType.APPLICATION_PLAIN.same(mt)) return  UDF.RETURN_FORMAT_PLAIN;
		return defaultValue;
	}
}
