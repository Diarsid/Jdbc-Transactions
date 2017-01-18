/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Diarsid
 */
public class Params {
    
    private final Object[] params;
        
    public Params(Object... params) {
        this.params = params;
    }
    
    public static Params params(Object... params) {
        return new Params(params);
    }
    
    public static Params params(List<Object> params) {
        return new Params(params.toArray());
    }
    
    public static Params params(Set<Object> params) {
        return new Params(params.toArray());
    }
    
    Object[] get() {
        return this.params;
    }
    
    int qty() {
        return this.params.length;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.deepHashCode(this.params);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Params other = ( Params ) obj;
        if ( !Arrays.deepEquals(this.params, other.params) ) {
            return false;
        }
        return true;
    }
}
