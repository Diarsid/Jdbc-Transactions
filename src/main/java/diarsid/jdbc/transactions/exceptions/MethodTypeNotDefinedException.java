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
public class MethodTypeNotDefinedException extends Exception {

    /**
     * Creates a new instance of <code>MethodTypeNotDefinedException</code> without detail message.
     */
    public MethodTypeNotDefinedException() {
    }

    /**
     * Constructs an instance of <code>MethodTypeNotDefinedException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public MethodTypeNotDefinedException(String msg) {
        super(msg);
    }
}
