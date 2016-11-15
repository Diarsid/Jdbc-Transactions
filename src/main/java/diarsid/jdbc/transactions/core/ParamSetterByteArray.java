/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class ParamSetterByteArray implements JdbcPreparedStatementParamSetter {
    
    ParamSetterByteArray() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof byte[]);
    }

    @Override
    public void setParameter(PreparedStatement statement, int index, Object param) 
            throws SQLException {
        Blob blob = statement.getConnection().createBlob();
        blob.setBytes(0, (byte[])param);
        statement.setBlob(index, blob);
    }
}
