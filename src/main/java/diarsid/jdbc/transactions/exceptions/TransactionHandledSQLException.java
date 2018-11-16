/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.exceptions;

import java.sql.SQLException;

/**
 * Wrapper exception class that is used to wrap SQLExceptions occurring
 * during a JdbcTransaction execution.
 * 
 * @author Diarsid
 */
public class TransactionHandledSQLException extends Exception {
    
    public TransactionHandledSQLException(String message) {
        super(message);
    }

    /**
     * Wrap underlying SQLException that has occurred during an execution
     * of SQL statements or other SQL related actions inside of a JdbcTransaction.
     * 
     * @param e     SQLException that has occurred during a JdbcTransaction
     *              execution.
     */
    public TransactionHandledSQLException(Exception e) {
        super(e);
    }
    
    /**
     * Returns TRUE if this exception has been caused by a statement that
     * has violated the SQL table primary key constraint.
     * 
     * @return  TRUE if this exception has been caused by a statement that
     *          has violated the SQL table primary key constraint.
     */
    public boolean causedByPrimaryKeyViolation() {
        return ((SQLException) this.getCause())
                .getSQLState()
                .startsWith("23");
    }
}
