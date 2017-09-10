/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.List;
import java.util.Set;

import diarsid.jdbc.transactions.SqlHistoryFormattingAlgorithm;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Diarsid
 */
class JdbcTransactionSqlHistoryRecorder {
    
    private static final String LINE_SEPARATOR;
    private static final String PARAMETERS_LINE_TAB;
    
    static {
        LINE_SEPARATOR = System.lineSeparator();        
        PARAMETERS_LINE_TAB = "{p}";
    }
    
    private final StringBuilder stringBuilder;    
    private int counter;
    private final SqlHistoryFormattingAlgorithm formattingAlgorithm;
    
    JdbcTransactionSqlHistoryRecorder(SqlHistoryFormattingAlgorithm formattingAlgorithm) {
        this.stringBuilder = new StringBuilder();  
        this.formattingAlgorithm = formattingAlgorithm;
        this.init();
    }
    
    private void init() {
        this.counter = 0;
        this.stringBuilder
                .append(LINE_SEPARATOR)
                .append("[SQL HISTORY]")
                .append(LINE_SEPARATOR);
    }
    
    private void addCounter() {
        this.stringBuilder
                .append(format("[%d]", this.counter))
                .append(LINE_SEPARATOR);
        this.counter++;
    }
    
    void add(String sql) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
    }
    
    void add(String sql, List<? extends Object> params) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        this.addParamsLine(params);
    }

    private void addParamsLine(List<? extends Object> params) {
        this.stringBuilder
                .append(PARAMETERS_LINE_TAB)
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
        } else {
            return obj.toString();
        }
    }
    
    void add(String sql, Set<Params> batchParams) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        batchParams
                .stream()
                .forEach(params -> this.addParamsLine(params.list()));
    }
    
    String getHistory() {
        return this.formattingAlgorithm.formatSql(
                this.stringBuilder.toString(), PARAMETERS_LINE_TAB);
    }
    
    void rollback() {
        this.clear();
        this.init();        
    }
    
    void clear() {
        this.stringBuilder.delete(0, this.stringBuilder.length());
    }
}
