/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core.sqlhistory;

import java.util.function.Function;

/**
 *
 * @author Diarsid
 */
public class SqlHistory {
    
    private SqlHistory() {
        //
    }
    
    public static SqlHistoryRecorder getSqlHistoryRecorder(
            Function<String, FormattingAlgorithm> formattingAlgorithmProducer) {
        String parametersLineTabSign = "{p}";
        FormattingAlgorithm formattingAlgorithm = formattingAlgorithmProducer
                .apply(parametersLineTabSign);
        SqlHistoryRecorder sqlHistoryRecorder = new SqlHistoryRecorder(formattingAlgorithm);
        return sqlHistoryRecorder;
    }
}
