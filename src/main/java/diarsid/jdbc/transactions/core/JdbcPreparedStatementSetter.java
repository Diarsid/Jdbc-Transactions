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
        Set<JdbcPreparedStatementParamSetter> defaultSetters = new HashSet<>();
        defaultSetters.add(new ParamSetterString());
        defaultSetters.add(new ParamSetterBool());
        defaultSetters.add(new ParamSetterInt());
        defaultSetters.add(new ParamSetterByteArray());
        defaultSetters.add(new ParamSetterBinaryStream());
        defaultSetters.addAll(asList(additionalSetters));
        this.setters = unmodifiableSet(defaultSetters);
    }
    
    void setParameters(PreparedStatement statement, Object[] params) throws SQLException {
        int paramIndex = 1;
        for (Object param : params) {
            this.findAppropriateSetterFor(param)
                    .setParameterInto(statement, paramIndex, param);
            paramIndex++;
        }
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
