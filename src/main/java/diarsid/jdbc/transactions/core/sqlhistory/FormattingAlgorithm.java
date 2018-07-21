/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core.sqlhistory;

/**
 *
 * @author Diarsid
 */
public abstract class FormattingAlgorithm {
    
    private final String parametersLineTabSign;
    
    public FormattingAlgorithm(String parametersLineTabSign) {
        this.parametersLineTabSign = parametersLineTabSign;
    }
    
    final String parametersLineTabSign() {
        return this.parametersLineTabSign;
    }
    
    public abstract String formatSql(StringBuilder historyBuilder);
    
}
