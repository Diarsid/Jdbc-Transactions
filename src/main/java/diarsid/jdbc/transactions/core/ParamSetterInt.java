/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;


class ParamSetterInt implements JdbcPreparedStatementParamSetter {
    
    ParamSetterInt() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof Integer );
    }

    @Override
    public void setParameterInto(PreparedStatement statement, int index, Object arg) throws SQLException {
        statement.setInt(index, (int) arg);
    }
}
