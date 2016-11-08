/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import diarsid.jdbc.transactions.exceptions.JdbcPreparedStatementParamsException;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 *
 * @author Diarsid
 */
public class JdbcPreparedStatementSetter {
    
    private final Set<JdbcPreparedStatementParamSetter> setters;
    
    public JdbcPreparedStatementSetter(
            JdbcPreparedStatementParamSetter... additionalSetters) {
        Set<JdbcPreparedStatementParamSetter> newSetters = new HashSet<>();
        newSetters.add(new ParamSetterString());
        newSetters.add(new ParamSetterBool());
        newSetters.add(new ParamSetterInt());
        newSetters.addAll(asList(additionalSetters));
        this.setters = unmodifiableSet(newSetters);
    }
    
    void setParameters(PreparedStatement statement, Object[] args) throws SQLException {
        int counter = 1;
        for (Object arg : args) {
            this.setParameter(statement, arg, counter);
            counter++;
        }
    }
        
    private void setParameter(PreparedStatement statement, Object arg, int counter) 
            throws SQLException {        
        this.findAppropriateSetterFor(arg)
                .setParameter(statement, counter, arg);
    }
    
    private JdbcPreparedStatementParamSetter findAppropriateSetterFor(Object obj) {
        return this.setters
                .stream()
                .filter(setter -> setter.applicableTo(obj))
                .findFirst()
                .orElseThrow(() -> 
                        new JdbcPreparedStatementParamsException(
                                "appropriate ParamsSetter not found for class: " + 
                                obj.getClass().getCanonicalName()));
    }
}
