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
package railo.runtime.search;


public final class IndexResultImpl implements IndexResult {
	
	private int countDeleted;
	private int countInserted;
	private int countUpdated;
	
	public static IndexResult EMPTY=new IndexResultImpl(0,0,0);

	public IndexResultImpl(int countDeleted,int countInserted,int countUpdated) {
		this.countDeleted=countDeleted;
		this.countInserted=countInserted;
		this.countUpdated=countUpdated;
	}
	public IndexResultImpl() {
	}

	@Override
	public int getCountDeleted() {
		return countDeleted;
	}

	@Override
	public int getCountInserted() {
		return countInserted;
	}

	@Override
	public int getCountUpdated() {
		return countUpdated;
	}

	/**
	 * @param countDeleted the countDeleted to set
	 */
	public void setCountDeleted(int countDeleted) {
		this.countDeleted = countDeleted;
	}

	/**
	 * @param countInserted the countInserted to set
	 */
	public void setCountInserted(int countInserted) {
		this.countInserted = countInserted;
	}

	/**
	 * @param countUpdated the countUpdated to set
	 */
	public void setCountUpdated(int countUpdated) {
		this.countUpdated = countUpdated;
	}
	/**
	 * @param countDeleted the countDeleted to set
	 */
	public void incCountDeleted() {
		this.countDeleted++;
	}

	/**
	 * @param countInserted the countInserted to set
	 */
	public void incCountInserted() {
		this.countInserted++;
	}

	/**
	 * @param countUpdated the countUpdated to set
	 */
	public void incCountUpdated() {
		this.countUpdated++;
	}
	
}
