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
public class TransactionTerminationException extends TransactionHandledException {

    /**
     * Creates a new instance of <code>TransactionTerminationException</code> without detail
     * message.
     */
    public TransactionTerminationException() {
    }

    /**
     * Constructs an instance of <code>TransactionTerminationException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public TransactionTerminationException(String msg) {
        super(msg);
    }
}
