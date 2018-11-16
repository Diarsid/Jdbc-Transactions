/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

import java.util.Optional;
import java.util.Set;

import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author Diarsid
 */
class SqlTypeToJavaTypeConverter {
    
    private final Set<TypeToTypeConversion> conversions;
    
    SqlTypeToJavaTypeConverter(TypeToTypeConversion... conversions) {
        this.conversions = stream(conversions).collect(toSet());
        this.conversions.add(new TypeToTypeDoubleToFloatConversion());        
    }
    
    <T> T convert(Object obj, Class<T> type) throws TransactionHandledSQLException {
        Optional<TypeToTypeConversion> foundConversion = this.conversions
                .stream()
                .filter(conversion -> conversion.matchBothTypes(obj.getClass(), type))
                .findFirst();
        
        if ( foundConversion.isPresent() ) {
            return (T) foundConversion.get().convert(obj);
        } else {
            String message = format(
                    "Can not convert SQL type %s to Java type %s", 
                    obj.getClass().getCanonicalName(), 
                    type.getCanonicalName());
            throw new TransactionHandledSQLException(message);
        }
    }
    
}
