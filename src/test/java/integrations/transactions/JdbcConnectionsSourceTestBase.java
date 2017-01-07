/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrations.transactions;

import java.sql.Connection;
import java.sql.SQLException;

import testing.embedded.base.h2.TestDataBase;

import diarsid.jdbc.transactions.JdbcConnectionsSource;

/**
 *
 * @author Diarsid
 */
public class JdbcConnectionsSourceTestBase implements JdbcConnectionsSource {
    
    private final TestDataBase embeddedBase;
    
    public JdbcConnectionsSourceTestBase(TestDataBase testDataBase) {
        this.embeddedBase = testDataBase;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.embeddedBase.getConnection();
    }

    @Override
    public void closeSource() {
        
    }
}
