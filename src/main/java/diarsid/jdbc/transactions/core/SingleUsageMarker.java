/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

/**
 *
 * @author Diarsid
 */
public class SingleUsageMarker {
    
    private boolean used;
    
    public SingleUsageMarker() {
        used = false;
    }
    
    boolean isMarked() {
        return this.used;
    }
    
    void markUsed() {
        this.used = true;
    }
}
