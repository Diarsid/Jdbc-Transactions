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
public class JdbcFailureException extends RuntimeException {

    public JdbcFailureException() {
    }

    public JdbcFailureException(String msg) {
        super(msg);
    }
}
