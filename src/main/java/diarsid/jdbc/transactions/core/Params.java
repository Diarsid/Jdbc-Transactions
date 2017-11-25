/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Diarsid
 */
public class Params {
    
    private final List<Object> params;
        
    public Params(List<Object> params) {
        this.params = params;
    }
    
    public static Params params(Object... params) {
        return new Params(asList(params));
    }
    
    public static Params params(List<Object> params) {
        return new Params(params);
    }
    
    public static Params params(Set<Object> params) {
        return new Params(params.stream().collect(toList()));
    }
    
    List<Object> list() {
        return this.params;
    }
    
    Stream<Object> stream() {
        return this.params.stream();
    }
    
    int qty() {
        return this.params.size();
    }
    
    @Override
    public String toString() {
        return format(
                "PARAMS[%s]", this.params
                        .stream()
                        .map(obj -> String.valueOf(obj))
                        .collect(joining(", ")));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.params);
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
        if ( !Objects.equals(this.params, other.params) ) {
            return false;
        }
        return true;
    }
    
    
}
