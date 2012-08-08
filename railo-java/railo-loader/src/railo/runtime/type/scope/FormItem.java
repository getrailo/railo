package railo.runtime.type.scope;

import railo.commons.io.res.Resource;

public interface FormItem {
		
		/**
		 * @return the resource
		 */
		public Resource getResource();
		
		/**
		 * @return the contentType
		 */
		public String getContentType();
		
		/**
		 * @return the name
		 */
		public String getName();
		
		/**
		 * @return the fieldName
		 */
		public String getFieldName();
	}