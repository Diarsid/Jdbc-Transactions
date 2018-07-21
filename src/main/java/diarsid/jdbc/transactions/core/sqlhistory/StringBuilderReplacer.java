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
interface StringBuilderReplacer {
    
    static StringBuilderReplacer regexReplacer(String regextToReplace, String replacement) {
        return new StringBuilderRegexReplacer(regextToReplace, replacement);
    }
    
    static StringBuilderReplacer stringReplacer(String stringToReplace, String replacement) {
        return new StringBuilderStringReplacer(stringToReplace, replacement);
    }
    
    void replaceIn(StringBuilder stringBuilder);
}
