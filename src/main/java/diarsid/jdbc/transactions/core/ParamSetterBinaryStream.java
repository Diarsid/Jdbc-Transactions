/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;


class ParamSetterBinaryStream implements JdbcPreparedStatementParamSetter {
    
    ParamSetterBinaryStream() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof InputStream );
    }

    @Override
    public void setParameter(PreparedStatement statement, int index, Object param) 
            throws SQLException {
        statement.setBinaryStream(index, ((InputStream) param));
    }
}
