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

package railo.runtime.sql.old;


// Referenced classes of package Zql:
//            ZStatement, ZExp

public final class ZDelete
    implements ZStatement
{

    public ZDelete(String s)
    {
        where_ = null;
        table_ = new String(s);
    }

    public void addWhere(ZExp zexp)
    {
        where_ = zexp;
    }

    public String getTable()
    {
        return table_;
    }

    public ZExp getWhere()
    {
        return where_;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("delete ");
        if(where_ != null)
            stringbuffer.append("from ");
        stringbuffer.append(table_);
        if(where_ != null)
            stringbuffer.append(" where " + where_.toString());
        return stringbuffer.toString();
    }

    String table_;
    ZExp where_;
}