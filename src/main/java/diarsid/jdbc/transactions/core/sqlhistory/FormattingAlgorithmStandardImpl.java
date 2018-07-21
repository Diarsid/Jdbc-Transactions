/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core.sqlhistory;

import java.util.ArrayList;
import java.util.List;

import static diarsid.jdbc.transactions.core.sqlhistory.StringBuilderReplacer.regexReplacer;
import static diarsid.jdbc.transactions.core.sqlhistory.StringBuilderReplacer.stringReplacer;


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
    private final List<StringBuilderReplacer> replacers;
    
    public FormattingAlgorithmStandardImpl(String parametersLineTabSign) {
        super(parametersLineTabSign);
        this.parametersLineTabSign = parametersLineTabSign;
        this.replacers = new ArrayList<>();
        this.replacers.add(regexReplacer("\\s+", " "));
        this.replacers.add(regexReplacer("[" + LINE_SEPARATOR + "]+", ""));
        this.replacers.add(regexReplacer("[\\[]+", "\n["));
        this.replacers.add(regexReplacer("(all|ALL)", "ALL"));
        this.replacers.add(regexReplacer("(union|UNION)", LINE_SEPARATOR_TAB_TAB + "UNION"));
        this.replacers.add(regexReplacer("(insert|INSERT)", LINE_SEPARATOR_TAB + "INSERT"));
        this.replacers.add(regexReplacer("(delete|DELETE)", LINE_SEPARATOR_TAB + "DELETE"));
        this.replacers.add(regexReplacer("(update|UPDATE)", LINE_SEPARATOR_TAB + "UPDATE"));
        this.replacers.add(regexReplacer("(set|SET)", LINE_SEPARATOR_TAB + "SET"));
        this.replacers.add(regexReplacer("(select|SELECT)", LINE_SEPARATOR_TAB + "SELECT"));
        this.replacers.add(regexReplacer("(where|WHERE)", LINE_SEPARATOR_TAB + "WHERE"));
        this.replacers.add(regexReplacer("(from|FROM)", LINE_SEPARATOR_TAB + "FROM"));
        this.replacers.add(regexReplacer("(group by|GROUP BY)", LINE_SEPARATOR_TAB + "GROUP BY"));
        this.replacers.add(regexReplacer("(order by|ORDER BY)", LINE_SEPARATOR_TAB + "ORDER BY"));
        this.replacers.add(regexReplacer("(values|VALUES)", LINE_SEPARATOR_TAB + "VALUES"));
        this.replacers.add(regexReplacer("(having|HAVING)", LINE_SEPARATOR_TAB + "VALUES"));
        this.replacers.add(regexReplacer("(join|JOIN)", LINE_SEPARATOR_TAB_TAB + "JOIN"));
        this.replacers.add(regexReplacer("(case|CASE)", LINE_SEPARATOR_TAB_TAB + "CASE"));
        this.replacers.add(regexReplacer("(limit|LIMIT)", LINE_SEPARATOR_TAB + "LIMIT"));
        this.replacers.add(stringReplacer(this.parametersLineTabSign + "(", LINE_SEPARATOR_TAB_TAB + "("));
        this.replacers.add(stringReplacer(TAB, "    "));
    }
    
    @Override
    public String formatSql(StringBuilder historyBuilder) {
        for (int i = 0; i < this.replacers.size(); i++) {
            replacers.get(i).replaceIn(historyBuilder);
        }
        return historyBuilder.toString();
    }
    
}
