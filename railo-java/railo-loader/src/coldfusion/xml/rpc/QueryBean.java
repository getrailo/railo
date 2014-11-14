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
package coldfusion.xml.rpc;

import java.io.Serializable;

/**
 * Extends the Query with a Bean initalizer for WebService deserializer
 */
public final class QueryBean implements Serializable {

    private String columnList[];
    private Object data[][];

    
    public QueryBean() {}
    

	/**
     * @return Returns the columnList.
     */
    public String[] getColumnList() {
        return columnList;
    }

    /**
     * @param columnList The columnList to set.
     */
    public void setColumnList(String[] columnList) {
        this.columnList = columnList;
    }

    /**
     * @return Returns the data.
     */
    public Object[][] getData() {
        return data;
    }

    /**
     * @param data The data to set.
     */
    public void setData(Object[][] data) {
        this.data = data;
    }
}