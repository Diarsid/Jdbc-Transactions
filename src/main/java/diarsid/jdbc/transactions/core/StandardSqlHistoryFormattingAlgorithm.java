/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import diarsid.jdbc.transactions.SqlHistoryFormattingAlgorithm;


class StandardSqlHistoryFormattingAlgorithm implements SqlHistoryFormattingAlgorithm {
    
    private static final String TAB;
    private static final String TAB_TAB;
    private static final String LINE_SEPARATOR;
    private static final String LINE_SEPARATOR_TAB; 
    private static final String LINE_SEPARATOR_TAB_TAB; 
    static {
        LINE_SEPARATOR = System.lineSeparator();
        TAB = "->";
        TAB_TAB = TAB + TAB;        
        LINE_SEPARATOR_TAB = LINE_SEPARATOR + TAB;
        LINE_SEPARATOR_TAB_TAB = LINE_SEPARATOR + TAB + TAB;
    }
    
    StandardSqlHistoryFormattingAlgorithm() {
    }

    @Override
    public String formatSql(String sqlHistory, String parametersLineTabSign) {
        return sqlHistory
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
                .replaceAll("(join|JOIN)", LINE_SEPARATOR_TAB_TAB + "JOIN")
                .replace(parametersLineTabSign + "(", LINE_SEPARATOR_TAB_TAB + "(")
                .replace(TAB, "    ");
    }
}
