/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ParamSetterEnum implements JdbcPreparedStatementParamSetter {
    
    public ParamSetterEnum() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return ( o instanceof Enum );
    }

    @Override
    public void setParameterInto(PreparedStatement statement, int index, Object param) throws SQLException {
        statement.setString(index, ((Enum) param).name());
    }
}
