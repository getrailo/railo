/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.dump;

import java.util.Set;

public class DumpProperties {
	public static final int DEFAULT_MAX_LEVEL=9999;

	public final static DumpProperties DEFAULT = new DumpProperties(DumpProperties.DEFAULT_MAX_LEVEL,null,null,9999,true,true);
	private final Set show;
	private final Set hide;
	private final int maxlevel;
	private final int keys;
	private final boolean metainfo;
	private final boolean showUDFs;
	
	
	
	
	public DumpProperties(int maxlevel, Set show, Set hide, int keys,boolean metainfo, boolean showUDFs) {
		this.show = show;
		this.hide = hide;
		this.maxlevel=maxlevel;
		this.keys=keys;
		this.metainfo=metainfo;
		this.showUDFs=showUDFs;
	}
	/**
	 * @return the metainfo
	 */
	public boolean getMetainfo() {
		return metainfo;
	}
	/**
	 * @return the show
	 */
	public Set getShow() {
		return show;
	}
	/**
	 * @return the hide
	 */
	public Set getHide() {
		return hide;
	}
	public int getMaxlevel() {
		return maxlevel;
	}
	/**
	 * @return the keys
	 */
	public int getMaxKeys() {
		return keys;
	}
	/**
	 * @return the showUDFs
	 */
	public boolean getShowUDFs() {
		return showUDFs;
	}
}
