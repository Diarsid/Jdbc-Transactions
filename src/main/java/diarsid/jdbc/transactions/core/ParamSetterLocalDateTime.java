/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static java.sql.Timestamp.valueOf;


public class ParamSetterLocalDateTime implements JdbcPreparedStatementParamSetter {
    
    public ParamSetterLocalDateTime() {
    }

    @Override
    public boolean applicableTo(Object o) {
        return (o instanceof LocalDateTime);
    }

    @Override
    public void setParameterInto(PreparedStatement statement, int index, Object param) throws SQLException {
        statement.setTimestamp(index, valueOf((LocalDateTime) param));
    }
}
