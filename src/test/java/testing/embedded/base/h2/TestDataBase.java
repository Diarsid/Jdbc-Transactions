/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing.embedded.base.h2;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Diarsid
 */
public interface TestDataBase {
    
    Connection getConnection() throws SQLException ;
    
    void setupRequiredTable(String tableCreationSQLScript);
    
    int countRowsInTable(String tableName);
}
