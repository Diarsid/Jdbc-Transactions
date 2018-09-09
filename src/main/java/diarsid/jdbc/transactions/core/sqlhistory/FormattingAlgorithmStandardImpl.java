/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core.sqlhistory;

import diarsid.support.strings.replace.Replace;

import static diarsid.support.strings.replace.Replace.replace;


public class FormattingAlgorithmStandardImpl extends FormattingAlgorithm {
    
    private static final String TAB;
    private static final String TAB_TAB;
    private static final String LINE_SEPARATOR;
    private static final String LINE_SEPARATOR_TAB; 
    private static final String LINE_SEPARATOR_TAB_TAB; 
    private static final String LINE_SEPARATOR_TAB_TAB_TAB; 
    
    static {
        LINE_SEPARATOR = System.lineSeparator();
        TAB = "->";
        TAB_TAB = TAB + TAB;        
        LINE_SEPARATOR_TAB = LINE_SEPARATOR + TAB;
        LINE_SEPARATOR_TAB_TAB = LINE_SEPARATOR + TAB + TAB;
        LINE_SEPARATOR_TAB_TAB_TAB = LINE_SEPARATOR + TAB + TAB + TAB;
    }
    
    private final String parametersLineTabSign;
    private final Replace replace;
    
    public FormattingAlgorithmStandardImpl(String parametersLineTabSign) {
        super(parametersLineTabSign);
        this.parametersLineTabSign = parametersLineTabSign;
        this.replace = replace()
                .regexToString("\\s+", " ")
                .regexToString("[" + LINE_SEPARATOR + "]+", "")
                .regexToString("[\\[]+", "\n[")
                .regexToString("(all|ALL)", "ALL")
                .regexToString("(union|UNION)", LINE_SEPARATOR_TAB_TAB + "UNION")
                .regexToString("(insert|INSERT)", LINE_SEPARATOR_TAB + "INSERT")
                .regexToString("(delete|DELETE)", LINE_SEPARATOR_TAB + "DELETE")
                .regexToString("(update|UPDATE)", LINE_SEPARATOR_TAB + "UPDATE")
                .regexToString("(set|SET)", LINE_SEPARATOR_TAB + "SET")
                .regexToString("(select|SELECT)", LINE_SEPARATOR_TAB + "SELECT")
                .regexToString("(where|WHERE)", LINE_SEPARATOR_TAB + "WHERE")
                .regexToString("(from|FROM)", LINE_SEPARATOR_TAB + "FROM")
                .regexToString("(group by|GROUP BY)", LINE_SEPARATOR_TAB + "GROUP BY")
                .regexToString("(order by|ORDER BY)", LINE_SEPARATOR_TAB + "ORDER BY")
                .regexToString("(values|VALUES)", LINE_SEPARATOR_TAB + "VALUES")
                .regexToString("(having|HAVING)", LINE_SEPARATOR_TAB + "VALUES")
                .regexToString("(join|JOIN)", LINE_SEPARATOR_TAB_TAB + "JOIN")
                .regexToString("(case|CASE)", LINE_SEPARATOR_TAB_TAB + "CASE")
                .regexToString("(limit|LIMIT)", LINE_SEPARATOR_TAB + "LIMIT")
                .stringToString(this.parametersLineTabSign + "(", LINE_SEPARATOR_TAB_TAB + "(")
                .stringToString(TAB, "    ");
    }
    
    @Override
    public String formatSql(StringBuilder historyBuilder) {
        this.replace.doIn(historyBuilder);
        return historyBuilder.toString();
    }
    
}
