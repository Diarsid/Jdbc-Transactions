/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrations.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import testing.embedded.base.h2.H2TestDataBase;
import testing.embedded.base.h2.TestDataBase;

import diarsid.jdbc.transactions.JdbcConnectionsSource;
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.core.JdbcPreparedStatementSetter;
import diarsid.jdbc.transactions.core.JdbcTransactionFactory;
import diarsid.jdbc.transactions.core.JdbcTransactionGuard;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;

import static java.lang.String.format;
import static java.lang.Thread.sleep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static diarsid.jdbc.transactions.core.Params.params;


/**
 *
 * @author Diarsid
 */
public class JdbcTransactionTest {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionTest.class);
    
    private static JdbcTransactionFactory TRANSACTION_FACTORY;
    private static TestDataBase TEST_BASE;
    private static final String TABLE_1_CREATE = 
            "CREATE TABLE table_1 (" +
            "id     INTEGER         NOT NULL PRIMARY KEY," +
            "label  VARCHAR(100)    NOT NULL," +
            "index  INTEGER         NOT NULL," +
            "active BOOLEAN         NOT NULL)";
    private static final String TABLE_1_INSERT = 
            "INSERT INTO table_1 (id, label, index, active) " +
            "VALUES (?, ?, ?, ?)";
     
    private static final int row_1_id = 1;
    private static final int row_2_id = 2;
    private static final int row_3_id = 3;
    
    private static final String row_1_label = "name_1";
    private static final String row_2_label = "name_2";
    private static final String row_3_label = "name_3";
    
    private static final int row_1_index = 10;
    private static final int row_2_index = 20;
    private static final int row_3_index = 30;
    
    private static final boolean row_1_active = true;
    private static final boolean row_2_active = false;
    private static final boolean row_3_active = true;
    
    public JdbcTransactionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        setupTestBase();
        setupTransactionsFactory();
        setupRequiredTables();    
    }
    
    @Before
    public void setUpCase() {
        setupTestData();
    }
    
    @After
    public void clearCase() {
        clearData();
    }
    
    private static void clearData() {
        try (Connection connection = TEST_BASE.getConnection();
                Statement st = connection.createStatement();) {
            st.executeUpdate("DELETE FROM table_1");
        } catch (SQLException e) {
            logger.error("test base data cleaning: ", e);
        }
    }

    private static void setupTestData() {
        try (Connection connection = TEST_BASE.getConnection();
                PreparedStatement st = connection.prepareStatement(TABLE_1_INSERT)) {
            st.setInt(1, row_1_id);
            st.setString(2, row_1_label);
            st.setInt(3, row_1_index);
            st.setBoolean(4, row_1_active);
            st.addBatch();
            st.setInt(1, row_2_id);
            st.setString(2, row_2_label);
            st.setInt(3, row_2_index);
            st.setBoolean(4, row_2_active);
            st.addBatch();
            st.setInt(1, row_3_id);
            st.setString(2, row_3_label);
            st.setInt(3, row_3_index);
            st.setBoolean(4, row_3_active);
            st.addBatch();
            
            int[] update = st.executeBatch();
        } catch (SQLException e) {
            logger.error("test base data prepopulation: ", e);
        }
    }

    private static TestDataBase setupTestBase() {
        TestDataBase testBase = new H2TestDataBase("transactions.test");
        TEST_BASE = testBase;
        return testBase;
    }
    
    private static void setupRequiredTables() {
        TEST_BASE.setupRequiredTable(TABLE_1_CREATE);
    }

    private static void setupTransactionsFactory() {
        JdbcConnectionsSource source = new JdbcConnectionsSourceTestBase(TEST_BASE);        
        JdbcTransactionGuard transactionGuard = new JdbcTransactionGuard(1);
        JdbcPreparedStatementSetter paramsSetter = new JdbcPreparedStatementSetter();
        TRANSACTION_FACTORY = new JdbcTransactionFactory(
                source, transactionGuard, paramsSetter);
    }
    
    static JdbcTransaction createTransaction() throws TransactionHandledSQLException {
        return TRANSACTION_FACTORY.createTransaction();
    }

    /**
     * Test of rollback method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testRollback() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        
        int update = transaction.doUpdate(
                "DELETE FROM table_1 WHERE label IS ? ", 
                "name_2");
        
        assertEquals(1, update);
        
        transaction.rollback();
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfter);
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_3args_1() throws Exception {
        JdbcTransaction transaction = createTransaction();
        transaction.doQuery("SELECT * FROM table_1", (row) -> {
            logger.info(
                    format("id: %d, label: %s, index: %d, active: %s", 
                           (Integer)row.get("id"),
                           (String)row.get("label"),
                           (Integer)row.get("index"),
                           (Boolean)row.get("active")));
        });
        transaction.commit();
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_3args_2() throws Exception {
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_3args_3() throws Exception {
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_String_PerRowOperation() throws Exception {
    }

    /**
     * Test of doUpdate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoUpdate_String() throws Exception {
    }

    /**
     * Test of doUpdate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoUpdate_String_ObjectArr() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doUpdate(
                TABLE_1_INSERT, 
                4, "name_4", 40, false);        
        transaction.commit();
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(4, qtyAfter);
    }
    
    @Test(timeout = 3000)
    public void testDoUpdate_String_ObjectArr_not_commited_should_be_teared_down_by_Guard() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doUpdate(
                TABLE_1_INSERT, 
                4, "name_4", 40, false); 
        sleep(2000); 
        // transaction has not been committed or rolled back properly.
        // it will be rolled back, restored and closed by JdbcTransactionGuard.
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfter);
    }

    /**
     * Test of doUpdate method, of class JdbcTransactionWrapper.
     */
    @Test(expected = TransactionHandledSQLException.class)
    public void testDoUpdate_String_Params() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doBatchUpdate(
                TABLE_1_INSERT, 
                params(4, "name_4", 40, false),
                params(5, "name_5", 50, false),
                params(6, "name_6", 60, false)); 
        
        transaction.doBatchUpdate(
                TABLE_1_INSERT, 
                params(7, "name_7", 740, false),
                params(8, "name_8", 70, false));
        
        transaction.doUpdate(
                TABLE_1_INSERT, 
                8, "name_7", 70, false); // <- SQLException should rise due to primary key violation
        
        fail();        
    }

    /**
     * Test of doUpdate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoUpdate_String_List() throws Exception {
    }

    /**
     * Test of doBatchUpdate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoBatchUpdate_String_Set() throws Exception {
    }

    /**
     * Test of doBatchUpdate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoBatchUpdate_String_ParamsArr() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doBatchUpdate(
                TABLE_1_INSERT, 
                params(4, "name_4", 40, false),
                params(5, "name_5", 50, false),
                params(6, "name_6", 60, false));        
        transaction.commit();
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfter);
    }

    /**
     * Test of commit method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testCommit() {
    }

}