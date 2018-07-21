/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core.sqlhistory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import diarsid.jdbc.transactions.core.Params;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Diarsid
 */
public class SqlHistoryRecorder {
    
    private static final String LINE_SEPARATOR;
    
    static {
        LINE_SEPARATOR = System.lineSeparator();    
    }
    
    private final StringBuilder historyBuilder;    
    private int counter;
    private final FormattingAlgorithm formattingAlgorithm;
    private final String parametersLineTabSign;
    
    SqlHistoryRecorder(FormattingAlgorithm formattingAlgorithm) {
        this.historyBuilder = new StringBuilder();  
        this.formattingAlgorithm = formattingAlgorithm;
        this.parametersLineTabSign = formattingAlgorithm.parametersLineTabSign();
        this.init();
    }
    
    private void init() {
        this.counter = 0;
        this.historyBuilder
                .append(LINE_SEPARATOR)
                .append("[SQL HISTORY]")
                .append(LINE_SEPARATOR);
    }
    
    private void addCounter() {
        this.historyBuilder
                .append(format("[%d]", this.counter))
                .append(LINE_SEPARATOR);
        this.counter++;
    }
    
    public void add(String sql) {
        this.addCounter();
        this.historyBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
    }
    
    public void add(String sql, List<? extends Object> params) {
        this.addCounter();
        this.historyBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        this.addParamsLine(params);
    }

    private void addParamsLine(List<? extends Object> params) {
        this.historyBuilder
                .append(this.parametersLineTabSign)
                .append("( ")
                .append(params.stream()
                        .map(obj -> stringify(obj))
                        .collect(joining(", ")))
                .append(" )")
                .append(LINE_SEPARATOR);
    }
    
    private static String stringify(Object obj) {
        if ( obj instanceof Enum ) {
            return ((Enum) obj).name();
        } else if ( obj instanceof byte[] || obj instanceof Byte[] ) {
            return format("bytes:%s", ((byte[]) obj).length );
        } else if ( obj instanceof Collection ) { 
            return stringifyAsCollection(obj);
        } else if ( obj.getClass().isArray() ) { 
            return stringifyAsArray(obj);
        } else {
            return obj.toString();
        }
    }
    
    private static String stringifyAsCollection(Object obj) {
        return ((Collection<Object>) obj)
                .stream()
                .map(object -> stringify(object))
                .collect(joining(", "));
    }
    
    private static String stringifyAsArray(Object obj) {
        return stream(((Object[]) obj))
                .map(object -> stringify(object))
                .collect(joining(", "));
    }
    
    public void add(String sql, Set<Params> batchParams) {
        this.addCounter();
        this.historyBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        batchParams
                .stream()
                .forEach(params -> this.addParamsLine(params.list()));
    }
    
    public String getHistory() {
        return this.formattingAlgorithm.formatSql(this.historyBuilder);
    }
    
    public void rollback() {
        this.historyBuilder
                .append("[ROLLBACK]")
                .append(LINE_SEPARATOR);
    }
    
    public void clear() {
        this.historyBuilder.delete(0, this.historyBuilder.length());
    }
}
