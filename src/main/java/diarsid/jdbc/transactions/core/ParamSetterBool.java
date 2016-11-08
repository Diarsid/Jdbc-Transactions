/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;



class ParamSetterBool implements JdbcPreparedStatementParamSetter {
    
    ParamSetterBool() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof Boolean );
    }

    @Override
    public void setParameter(PreparedStatement statement, int index, Object arg) throws SQLException {
        statement.setBoolean(index, (boolean) arg);
    }
}
