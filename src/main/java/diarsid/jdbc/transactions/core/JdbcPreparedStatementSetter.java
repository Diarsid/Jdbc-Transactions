/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import diarsid.jdbc.transactions.exceptions.JdbcPreparedStatementParamsException;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Diarsid
 */
class JdbcPreparedStatementSetter {
    
    private final Set<JdbcPreparedStatementParamSetter> setters;
    
    JdbcPreparedStatementSetter(
            JdbcPreparedStatementParamSetter... additionalSetters) {
        Set<JdbcPreparedStatementParamSetter> defaultSetters = new HashSet<>();
        defaultSetters.add(new ParamSetterNull());
        defaultSetters.add(new ParamSetterString());
        defaultSetters.add(new ParamSetterBool());
        defaultSetters.add(new ParamSetterInt());
        defaultSetters.add(new ParamSetterLong());
        defaultSetters.add(new ParamSetterByteArray());
        defaultSetters.add(new ParamSetterBinaryStream());
        defaultSetters.add(new ParamSetterLocalDateTime());
        defaultSetters.add(new ParamSetterEnum());
        defaultSetters.add(new ParamSetterUUID());
        defaultSetters.add(new ParamSetterFloat());
        defaultSetters.add(new ParamSetterDouble());
        if ( nonNull(additionalSetters) && additionalSetters.length > 0 ) {
            defaultSetters.addAll(asList(additionalSetters));
        }
        this.setters = unmodifiableSet(defaultSetters);
    }
    
    void setParameters(PreparedStatement statement, Stream<? extends Object> params) throws SQLException {
        int paramIndex = 1;
        for (Object param : this.unwrap(params).collect(toList())) {
            this.findAppropriateSetterFor(param)
                    .setParameterInto(statement, paramIndex, param);
            paramIndex++;
        }
    }
    
    private Stream<Object> unwrap(Stream<? extends Object> stream) {
        return stream.flatMap(obj -> {
            if ( obj instanceof Collection ) {
                return ( (Collection) obj ).stream();
            } else if ( obj.getClass().isArray() 
                    && ! (obj instanceof byte[] || obj instanceof Byte[]) ) {
                return stream( (Object[]) obj );
            } else {
                return Stream.of(obj);
            }
        });
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
