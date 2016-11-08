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
public interface JdbcPreparedStatementParamSetter {

    boolean applicableTo(Object o);

    void setParameter(PreparedStatement statement, int index, Object param) throws SQLException ;    
}
