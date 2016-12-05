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
public class ForbiddenTransactionOperation extends TransactionHandledException {

    /**
     * Constructs an instance of <code>ProhibitedTransactionOperation</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ForbiddenTransactionOperation(String msg) {
        super(msg);
    }
}
