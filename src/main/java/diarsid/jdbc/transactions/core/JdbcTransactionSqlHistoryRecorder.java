/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Diarsid
 */
class JdbcTransactionSqlHistoryRecorder {
    
    private static final String LINE_SEPARATOR;
    
    static {
        LINE_SEPARATOR = System.lineSeparator();
    }
    
    private final StringBuilder stringBuilder;    
    private int counter;
    
    JdbcTransactionSqlHistoryRecorder() {
        this.stringBuilder = new StringBuilder();        
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
    
    void add(String sql, Object... params) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        this.addParamsLine(params);
    }

    private void addParamsLine(Object[] params) {
        this.stringBuilder
                .append("   ( ")
                .append(stream(params)
                        .map(obj -> obj.toString())
                        .collect(joining(", ")))
                .append(" )")
                .append(LINE_SEPARATOR);
    }
    
    void add(String sql, Set<Params> batchParams) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        batchParams
                .stream()
                .forEach(params -> this.addParamsLine(params.get()));
    }
    
    String getHistory() {
        return this.formattedSqlHistory();
    }
    
    private String formattedSqlHistory() {
        return this.stringBuilder.toString()
                .replaceAll("(where|WHERE)", LINE_SEPARATOR + "WHERE")
                .replaceAll("(from|FROM)", LINE_SEPARATOR + "FROM")
                .replaceAll("(group by|GROUP BY)", LINE_SEPARATOR + "GROUP BY")
                .replaceAll("(values|VALUES)", LINE_SEPARATOR + "VALUES");
    }
    
    void clear() {
        this.stringBuilder.delete(0, this.stringBuilder.length());
    }
}
