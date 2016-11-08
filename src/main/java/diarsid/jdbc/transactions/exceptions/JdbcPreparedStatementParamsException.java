/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.exceptions;

/**
 *
 * @author Diarsid
 */
public class JdbcPreparedStatementParamsException extends RuntimeException {

    /**
     * Constructs an instance of <code>ArgSetterTransactException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public JdbcPreparedStatementParamsException(String msg) {
        super(msg);
    }
}
