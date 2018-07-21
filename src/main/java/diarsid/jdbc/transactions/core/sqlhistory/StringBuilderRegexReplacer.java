/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core.sqlhistory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 *
 * @author Diarsid
 */
class StringBuilderRegexReplacer implements StringBuilderReplacer {
    
    private static final StringBuilder EMPTY_STRING_BUILDER = new StringBuilder();
    
    private final Matcher matcher;
    private final String replacement;
    private final int replacementLength;

    StringBuilderRegexReplacer(String regexToReplace, String replacement) {
        Pattern pattern = compile(regexToReplace);
        this.matcher = pattern.matcher(EMPTY_STRING_BUILDER);
        this.replacement = replacement;
        this.replacementLength = this.replacement.length();
    }
    
    @Override
    public void replaceIn(StringBuilder stringBuilder) {
        this.matcher.reset(stringBuilder);
        
        int start = 0;
        int end;
        while ( this.matcher.find(start) ) {
            start = this.matcher.start();
            end = this.matcher.end();
            stringBuilder.replace(start, end, this.replacement);
            start = start + this.replacementLength;
            this.matcher.reset();
        }
        
        this.matcher.reset(EMPTY_STRING_BUILDER);
    }
    
}
