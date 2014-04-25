package railo.runtime.extension;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;

public class ExtensionImpl implements Extension {

	private String provider;
	private String id;
	private String strConfig;
	private Struct config;
	private String version;
	private String name;
	private String label;
	private String description;
	private String category;
	private String image;
	private String author;
	private String codename;
	private String video;
	private String support;
	private String documentation;
	private String forum;
	private String mailinglist;
	private String network;
	private DateTime created;
	private String type;
	
	
	public ExtensionImpl(String strConfig, String id, String provider, String version, 
			String name, String label, String description, String category, String image,
			String author,String codename,String video,String support,String documentation,
			String forum,String mailinglist,String network,DateTime created,String type) {
		this.strConfig=strConfig;
		this.id = id;
		this.provider = provider;
		this.version = version;
		this.name = name;
		this.label = label;
		this.description = description;
		this.category = category;
		this.image = image;

		this.author = author;
		this.codename = codename;
		this.video = video;
		this.support = support;
		this.documentation = documentation;
		this.forum = forum;
		this.mailinglist = mailinglist;
		this.network = network;
		this.created = created;

		if("server".equalsIgnoreCase(type))this.type="server";
		else if("all".equalsIgnoreCase(type))this.type="all";
		else this.type="web";
		
	}
	
	public ExtensionImpl(Struct config, String id, String provider, String version, 
			String name, String label, String description, String category, String image,
			String author,String codename,String video,String support,String documentation,
			String forum,String mailinglist,String network,DateTime created,String type) {
		if(config==null)	this.config=new StructImpl();
		else 				this.config = config;
		this.id = id;
		this.provider = provider;
		this.version = version;
		this.name = name;
		this.label = label;
		this.description = description;
		this.category = category;
		this.image = image;

		this.author = author;
		this.codename = codename;
		this.video = video;
		this.support = support;
		this.documentation = documentation;
		this.forum = forum;
		this.mailinglist = mailinglist;
		this.network = network;
		this.created = created;
		if("server".equalsIgnoreCase(type))this.type="server";
		else if("all".equalsIgnoreCase(type))this.type="all";
		else this.type="web";
	}
	
	
	
	@Override
	public String getAuthor() {
		return author;
	}



	@Override
	public String getCodename() {
		return codename;
	}



	@Override
	public String getVideo() {
		return video;
	}



	@Override
	public String getSupport() {
		return support;
	}



	@Override
	public String getDocumentation() {
		return documentation;
	}



	@Override
	public String getForum() {
		return forum;
	}



	@Override
	public String getMailinglist() {
		return mailinglist;
	}



	@Override
	public String getNetwork() {
		return network;
	}



	@Override
	public DateTime getCreated() {
		return created;
	}



	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getProvider() {
		return provider;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public Struct getConfig(PageContext pc) {
		if(config==null) {
			if(StringUtil.isEmpty(strConfig)) config= new StructImpl();
			else  {
				try {
					config= (Struct)pc.evaluate(strConfig);
				} catch (PageException e) {
					return new StructImpl();
				}
			}
		}
		return config;
	}
	
	@Override
	public String getStrConfig()  {
		if(config!=null && strConfig==null) {
			try {
				strConfig=new ScriptConverter().serialize(config);
			} catch (ConverterException e) {
				return "";
			}
		}
		return strConfig;
	}

	@Override
	public String getType() {
		return type;
	}
}
