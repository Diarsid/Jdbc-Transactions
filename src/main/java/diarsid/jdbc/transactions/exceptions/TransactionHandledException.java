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
public class TransactionHandledException extends Exception {

    /**
     * Creates a new instance of <code>TransactionHandledException</code> without detail message.
     */
    public TransactionHandledException() {
    }
    
    public TransactionHandledException(Exception e) {
    }

    /**
     * Constructs an instance of <code>TransactionHandledException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public TransactionHandledException(String msg) {
        super(msg);
    }
}
