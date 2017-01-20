/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;

/**
 *
 * @author Diarsid
 */
class SqlUtil {    
    
    private static final List<String> SQL_DDL_COMMANDS;    
    static {
        SQL_DDL_COMMANDS = unmodifiableList(asList(
                "UPDATE ", "INSERT INTO", "DELETE ", ";", "VALUES "));
    }
    
    SqlUtil() {
    }
    
    static String lower(String target) {
        return target.toLowerCase(ENGLISH);
    }
    
    static boolean containsIgnoreCase(String whereToSearch, String searched) {
        if ( isNull(searched) || isNull(whereToSearch) ) {
            return false;
        } else {
            return lower(whereToSearch).contains(lower(searched));
        }        
    }
    
    static boolean containsIgnoreCaseAnyFragment(
            String whereToSearch, Collection<String> searchedSnippets) {
        return searchedSnippets
                .stream()
                .filter(snippet -> containsIgnoreCase(whereToSearch, snippet))
                .findFirst()
                .isPresent();
    }
    
    static String preventSqlInjection(String s) throws SQLException {
        if ( containsIgnoreCaseAnyFragment(s, SQL_DDL_COMMANDS) ) {
            throw new SQLException("SQL injection found in: " + s);
        } else {
            return s;
        }
    }
}
