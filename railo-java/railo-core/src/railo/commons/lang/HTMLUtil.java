package railo.commons.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.CharsetUtil;
import railo.commons.net.HTTPUtil;
import railo.transformer.util.CFMLString;

/**
 * HTML Util class
 *
 */
public final class HTMLUtil {
	
	private final Tag[] tags=new Tag[]{
			new Tag("a","href"),
			new Tag("link","href"),
			new Tag("form","action"),
			new Tag("applet","code"),
			new Tag("script","src"),
			new Tag("body","background"),
			new Tag("frame","src"),
			new Tag("bgsound","src"),
			new Tag("img","src"),
			
			new Tag("embed",new String[]{"src","pluginspace"}),
			new Tag("object",new String[]{"data","classid","codebase","usemap"})
			
	};
	
	
	/**
	 * returns all urls in a html String
	 * @param html HTML String to search urls
	 * @param url Absolute URL path to set
	 * @return urls found in html String
	 */
	public List getURLS(String html, URL url) {
		
	    ArrayList urls=new ArrayList();
		CFMLString cfml=new CFMLString(html,CharsetUtil.UTF8);
		while(!cfml.isAfterLast()) {
			if(cfml.forwardIfCurrent('<')) {
				for(int i=0;i<tags.length;i++) {
					if(cfml.forwardIfCurrent(tags[i].tag+" ")) {
						getSingleUrl(urls,cfml,tags[i],url);
					}
				}
			}
			else {
				cfml.next();
			}
			
		}
		return urls;
	}
	
	/**
	 * transform a single tag
	 * @param urls all urls founded
	 * @param cfml CFMl String Object containing plain HTML
	 * @param tag current tag totransform
	 * @param url absolute URL to Set at tag attribute
	 */
	private void getSingleUrl(List urls,CFMLString cfml, Tag tag,URL url) {
		char quote=0;
		boolean inside=false;
		StringBuilder value=new StringBuilder();
		
		while(!cfml.isAfterLast()) {
			if(inside) {
				if(quote!=0 && cfml.forwardIfCurrent(quote)) {
					inside=false;
					
					add(urls,url,value.toString());
				}
				else if(quote==0 && (cfml.isCurrent(' ')||cfml.isCurrent("/>")||cfml.isCurrent('>')||cfml.isCurrent('\t')||cfml.isCurrent('\n'))) {
					inside=false;
					try {
						urls.add(new URL(url,value.toString()));
                    } catch (MalformedURLException e) {}
					cfml.next();
				} 
				else {
					value.append(cfml.getCurrent());
					cfml.next();
				}
			}
			else if(cfml.forwardIfCurrent('>')) {
				break;
			}
			else {
				
				for(int i=0;i<tag.attributes.length;i++) {
					if(cfml.forwardIfCurrent(tag.attributes[i])) {
						cfml.removeSpace();
						// =
						if(cfml.isCurrent('=')) {
							inside=true;
							cfml.next();
							cfml.removeSpace();
							
							quote=cfml.getCurrent();
							value=new StringBuilder();
							if(quote!='"' && quote!='\'')quote=0;
							else {
								cfml.next();
							}
						}
					}
				}
				if(!inside) {
					cfml.next();
				}
			}
		}
	}

    private void add(List list,URL baseURL,String value) {
		value=value.trim();
		String lcValue=value.toLowerCase();
		try {
			if(lcValue.startsWith("http://") || lcValue.startsWith("news://") || lcValue.startsWith("goopher://") || lcValue.startsWith("javascript:"))
				list.add(HTTPUtil.toURL(value,true));
			else {
				
				
				list.add(new URL(baseURL,value.toString()));
			}
		}
		catch(MalformedURLException mue) {}
		//print.err(list.get(list.size()-1));
	}

	private class Tag {
		private String tag;
		private String[] attributes;
		private Tag(String tag,String[] attributes) {
			this.tag=tag.toLowerCase();
			this.attributes=new String[attributes.length];
			for(int i=0;i<attributes.length;i++) {
				this.attributes[i]=attributes[i].toLowerCase();
			}
			
		}
		private Tag(String tag,String attribute1) {
			this.tag=tag.toLowerCase();
			this.attributes=new String[]{attribute1.toLowerCase()};
		}
	
	}
}