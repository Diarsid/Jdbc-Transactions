/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static diarsid.jdbc.transactions.core.SqlUtil.preventSqlInjection;


/**
 *
 * @author Diarsid
 */
class ParamSetterString implements JdbcPreparedStatementParamSetter {
    
    ParamSetterString() {
    }
    
    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof String );
    }
    
    @Override
    public void setParameter(PreparedStatement statement, int index, Object arg) 
            throws SQLException {
        statement.setString(index, preventSqlInjection((String) arg));
    }
}
