/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Diarsid
 */
class ParamSetterLong implements JdbcPreparedStatementParamSetter {
    
    ParamSetterLong() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof Long );
    }

    @Override
    public void setParameterInto(PreparedStatement statement, int index, Object arg) 
            throws SQLException {
        statement.setLong(index, (long) arg);
    }
}
