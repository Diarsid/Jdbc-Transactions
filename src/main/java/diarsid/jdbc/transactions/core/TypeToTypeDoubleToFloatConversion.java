/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;


class TypeToTypeDoubleToFloatConversion implements TypeToTypeConversion {

    @Override
    public boolean matchBothTypes(Class sqlType, Class javaType) {
        return sqlType.equals(Double.class) && javaType.equals(Float.class);
    }

    @Override
    public Object convert(Object sqlObject) {
        return ((Double) sqlObject).floatValue();
    }
    
}
