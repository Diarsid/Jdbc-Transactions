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
    private static final String TAB;
    private static final String TAB_TAB;
    private static final String LINE_SEPARATOR_TAB; 
    private static final String LINE_SEPARATOR_TAB_TAB; 
    
    static {
        TAB = "->";
        TAB_TAB = TAB + TAB;
        LINE_SEPARATOR = System.lineSeparator();
        LINE_SEPARATOR_TAB = LINE_SEPARATOR + TAB;
        LINE_SEPARATOR_TAB_TAB = LINE_SEPARATOR + TAB + TAB;
    }
    
    private final StringBuilder stringBuilder;    
    private int counter;
    
    JdbcTransactionSqlHistoryRecorder() {
        this.stringBuilder = new StringBuilder();    
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
    
    void add(String sql, Object... params) {
        this.addCounter();
        this.stringBuilder
                .append(sql)
                .append(LINE_SEPARATOR);
        this.addParamsLine(params);
    }

    private void addParamsLine(Object[] params) {
        this.stringBuilder
                .append(TAB_TAB)
                .append("( ")
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
                .replaceAll("\\s+", " ")
                .replaceAll("[" + LINE_SEPARATOR + "]+", "")
                .replaceAll("[\\[]+", "\n[")
                .replaceAll("(all|ALL)", "ALL")
                .replaceAll("(union|UNION)", LINE_SEPARATOR_TAB_TAB + "UNION")
                .replaceAll("(insert|INSERT)", LINE_SEPARATOR_TAB + "INSERT")
                .replaceAll("(delete|DELETE)", LINE_SEPARATOR_TAB + "DELETE")
                .replaceAll("(update|UPDATE)", LINE_SEPARATOR_TAB + "UPDATE")
                .replaceAll("(set|SET)", LINE_SEPARATOR_TAB + "SET")
                .replaceAll("(select|SELECT)", LINE_SEPARATOR_TAB + "SELECT")
                .replaceAll("(where|WHERE)", LINE_SEPARATOR_TAB + "WHERE")
                .replaceAll("(from|FROM)", LINE_SEPARATOR_TAB + "FROM")
                .replaceAll("(group by|GROUP BY)", LINE_SEPARATOR_TAB + "GROUP BY")
                .replaceAll("(order by|ORDER BY)", LINE_SEPARATOR_TAB + "ORDER BY")
                .replaceAll("(values|VALUES)", LINE_SEPARATOR_TAB + "VALUES")
                .replaceAll("(having|HAVING)", LINE_SEPARATOR_TAB + "VALUES")
                .replace(TAB_TAB + "(", LINE_SEPARATOR + TAB_TAB + "(")
                .replace(TAB, "    ");
    }
    
    void rollback() {
        this.clear();
        this.init();        
    }
    
    void clear() {
        this.stringBuilder.delete(0, this.stringBuilder.length());
    }
}
