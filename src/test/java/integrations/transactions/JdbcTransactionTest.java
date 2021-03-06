/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrations.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import diarsid.jdbc.transactions.Row;
import diarsid.jdbc.transactions.core.JdbcTransactionFactory;
import diarsid.jdbc.transactions.exceptions.TransactionHandledException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static diarsid.jdbc.transactions.core.JdbcTransactionFactoryBuilder.buildTransactionFactoryWith;
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
        TRANSACTION_FACTORY = buildTransactionFactoryWith(source)
                .withGuardWaitingOnSeconds(1)
                .done();
    }
    
    static JdbcTransaction createTransaction() throws TransactionHandledSQLException {
        return TRANSACTION_FACTORY.createTransaction().logHistoryAfterCommit();
    }
    
    static JdbcTransaction createDisposableTransaction() throws TransactionHandledSQLException {
        return TRANSACTION_FACTORY.createDisposableTransaction().logHistoryAfterCommit();
    }

    /**
     * Test of rollbackAndTerminate method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testRollbackAndTerminate() throws Exception {
        try {
            int qtyBefore = TEST_BASE.countRowsInTable("table_1");
            assertEquals(3, qtyBefore);
            
            JdbcTransaction transaction = createTransaction();
            
            int update = transaction.doUpdateVarargParams(
                "DELETE FROM table_1 WHERE label IS ? ",
                "name_2");
            
            assertEquals(1, update);
            
            transaction.rollbackAndTerminate();
            
            fail();
            
        } catch (TransactionTerminationException transactionTerminationException) {
            assertTrue(true);
        }
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfter);
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_3args_1() throws Exception {
        JdbcTransaction transaction = createTransaction();
        transaction.doQuery(
                (row) -> {
                    this.printDataFromRow(row, "multiple rows processing:");
                },
                "SELECT * FROM table_1");
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }

    /**
     * Test of doQuery method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testDoQuery_3args_2() throws Exception {
        JdbcTransaction transaction = createTransaction();
        transaction.doQueryVarargParams( 
                (row) -> {
                    this.printDataFromRow(row, "multiple rows processing:");
                },
                "SELECT * FROM table_1 WHERE label LIKE ? ",
                "ame_1");
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }

    private void printDataFromRow(Row row, String comment) throws TransactionHandledSQLException {
        logger.info(
                format("[%s] id: %d, label: %s, index: %d, active: %s",
                       comment,
                       (int) row.get("id"),
                       (String) row.get("label"),
                       (int) row.get("index"),
                       (boolean) row.get("active")));
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
        transaction.doUpdateVarargParams(
                TABLE_1_INSERT, 
                4, "name_4", 40, false);        
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(4, qtyAfter);
    }
    
    @Test(timeout = 3000)    
    public void testDoUpdate_String_ObjectArr_not_commited_should_be_teared_down_by_Guard() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doUpdateVarargParams(
                TABLE_1_INSERT, 
                4, "name_4", 40, false); 
        sleep(2000); 
        // transaction has not been committed or rolled back properly.
        // it will be rolled back, restored and closed by JdbcTransactionGuardReal.
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfter);
    }

    /**
     * Test of doUpdate method, of class JdbcTransactionWrapper.
     */
    // fix!!!1
    @Test()
    public void testDoUpdate_String_Params() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        try {
            JdbcTransaction transaction = createTransaction();
            transaction.doBatchUpdateVarargParams(
                    TABLE_1_INSERT,
                    params(4, "name_4", 40, false),
                    params(5, "name_5", 50, false),
                    params(6, "name_6", 60, false));            
            
            transaction.doBatchUpdateVarargParams(
                    TABLE_1_INSERT,
                    params(7, "name_7", 740, false),
                    params(8, "name_8", 70, false));
            
            transaction.doUpdateVarargParams(
                    TABLE_1_INSERT,
                    8, "name_7", 70, false); // <- SQLException should rise due to primary key violation
            
            fail();
        } catch (TransactionHandledSQLException transactionHandledSQLException) {
            assertTrue(TEST_BASE.ifAllConnectionsReleased());
            int qtyAfter = TEST_BASE.countRowsInTable("table_1");
            assertEquals(3, qtyAfter);
        }
    }
    
    @Test()
    public void testDoUpdate_String_Params_success() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doBatchUpdateVarargParams(
                TABLE_1_INSERT,
                params(4, "name_4", 40, false),
                params(5, "name_5", 50, false),
                params(6, "name_6", 60, false));            

        transaction.doBatchUpdateVarargParams(
                TABLE_1_INSERT,
                params(7, "name_7", 740, false),
                params(8, "name_8", 70, false));

        transaction.doUpdateVarargParams(
                TABLE_1_INSERT,
                9, "name_7", 70, false); 
        
        transaction.commit();
            
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(9, qtyAfter);
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
        transaction.doBatchUpdateVarargParams(
                TABLE_1_INSERT, 
                params(4, "name_4", 40, false),
                params(5, "name_5", 50, false),
                params(6, "name_6", 60, false));        
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfter);
    }

    /**
     * Test of commit method, of class JdbcTransactionWrapper.
     */
    @Test
    public void testRollbackAndProceed() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction.doBatchUpdateVarargParams(
                TABLE_1_INSERT, 
                params(4, "name_4", 40, false),
                params(5, "name_5", 50, false),
                params(6, "name_6", 60, false));
        
        transaction.rollbackAndProceed();
        
        int qtyAfterRollback = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfterRollback);
        
        transaction.doBatchUpdateVarargParams(
                TABLE_1_INSERT, 
                params(4, "name_4", 40, false));
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfter = TEST_BASE.countRowsInTable("table_1");
        assertEquals(4, qtyAfter);        
    }
    
    @Test
    public void testConditionals() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
        transaction
                .ifTrue(false)
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false));
                
        transaction
                .ifTrue(true)
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }
    
    @Test
    public void testConditionals_ifTrue_stacked() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
                
        transaction
                .ifTrue(true)
                .ifTrue(true)
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }
    
    @Test
    public void testConditionals_ifTrue_stacked_with_false() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        JdbcTransaction transaction = createTransaction();
                
        transaction
                .ifTrue(true)
                .ifTrue(false)
                .ifTrue(true)
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        transaction.commit();
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfterTrueCondition);
    }

    @Test
    public void testAutoCloseableTransaction() {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        try (JdbcTransaction transaction = createTransaction()) {
            
            transaction
                    .doBatchUpdateVarargParams(
                            TABLE_1_INSERT,
                            params(4, "name_4", 40, false),
                            params(5, "name_5", 50, false),
                            params(6, "name_6", 60, false));
            
            int count = transaction
                    .countQueryResults(
                            "SELECT * FROM table_1");
            
            assertEquals(6, count);
            
            //transaction.commit();   <- explicit commit() call is omitted!
        } catch (TransactionHandledSQLException | TransactionHandledException e) {
            fail();
        } 
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }    
    
    @Test
    public void disposableTransactionTest() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        createDisposableTransaction()
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }
    
    @Test
    public void disposableConditionalTransactionTest_true() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        createDisposableTransaction()
                .ifTrue( qtyBefore > 0 )
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }
    
    @Test
    public void disposableConditionalTransactionTest_ifTrue_stacked_true() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        createDisposableTransaction()
                .ifTrue( qtyBefore > 0 )
                .ifTrue( true )
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(6, qtyAfterTrueCondition);
    }
    
    @Test
    public void disposableConditionalTransactionTest_false() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        createDisposableTransaction()
                .ifTrue( false )
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfterTrueCondition);
    }
    
    @Test
    public void streamedQueryTest() throws Exception {
        
        int qty = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qty);
        
        List<String> list = createDisposableTransaction()
                .doQueryAndStream(
                        (row) -> {
                            return (int) row.get("index");
                        },
                        "SELECT * " +
                        "FROM table_1")
                .filter(i -> i > 0)
                .map(i -> String.valueOf(i) + ": index")
                .collect(toList());
        
        assertEquals(3, list.size());
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }
    
    @Test
    public void streamedQueryTest_varargWrappedParams() throws Exception {
        
        int qty = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qty);
        
        List<String> labelPatterns = asList("%na%", "%ame%", "%1%");
        
        List<String> list = createDisposableTransaction()
                .doQueryAndStreamVarargParams(
                        (row) -> {
                            return (int) row.get("index");
                        },
                        "SELECT * " +
                        "FROM table_1 " +
                        "WHERE  ( id IS ? ) AND " +
                        "       ( label LIKE ? ) AND ( label LIKE ? ) AND ( label LIKE ? ) " +
                        "       AND ( active IS ? ) ",                        
                        1, labelPatterns, true)
                .map(i -> String.valueOf(i) + ": index")
                .collect(toList());
        
        assertEquals(1, list.size());
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }
    
    @Test
    public void streamedQueryTest_vararg() throws Exception {
        
        int qty = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qty);
        
        List<String> list = createDisposableTransaction()
                .doQueryAndStreamVarargParams(
                        (row) -> {
                            return (int) row.get("index");
                        },
                        "SELECT * " +
                        "FROM table_1 " +
                        "WHERE ( label LIKE ? ) AND ( label LIKE ? )", 
                        "%m%", "%na%")
                .map(i -> String.valueOf(i) + ": index")
                .collect(toList());
        
        assertEquals(3, list.size());
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }
    
    @Test
    public void disposableConditionalTransactionTest_ifTrue_stacked_with_false() throws Exception {
        int qtyBefore = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyBefore);
        
        createDisposableTransaction()
                .ifTrue( true )
                .ifTrue( false )
                .ifTrue( true )
                .doBatchUpdateVarargParams(
                        TABLE_1_INSERT, 
                        params(4, "name_4", 40, false),
                        params(5, "name_5", 50, false),
                        params(6, "name_6", 60, false));
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        int qtyAfterTrueCondition = TEST_BASE.countRowsInTable("table_1");
        assertEquals(3, qtyAfterTrueCondition);
    }
    
    @Test
    public void firstRowProcessTest() throws Exception {
        createDisposableTransaction()
                .doQueryAndProcessFirstRow(
                        (firstRow) -> {
                            this.printDataFromRow(firstRow, "first row processing");
                        },
                        "SELECT TOP 1 * " +
                        "FROM table_1 " +
                        "ORDER BY index ");
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }
    
    @Test
    public void firstRowConvertTest() throws Exception {
        String s = createDisposableTransaction().doQueryAndConvertFirstRow(
                (firstRow) -> { 
                    return (String) firstRow.get("label");
                },
                "SELECT TOP 1 * " +
                "FROM table_1 " +
                "ORDER BY index "
        ).get();        
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        assertEquals(row_1_label, s);
    }
    
    // fix
    @Test
    public void firstRowConvertTest_empty() throws Exception {
        clearData();
        Optional<String> s = Optional.empty();
        try (JdbcTransaction transact = createDisposableTransaction()) {
            s = transact
                    .doQueryAndConvertFirstRow(
                            (firstRow) -> { 
                                return String.valueOf(( int ) firstRow.get("index"));
                            },
                            "SELECT MIN(index) AS index " +
                            "FROM table_1 " +
                            "ORDER BY index ");
            fail();
        } catch (TransactionHandledSQLException | 
                TransactionHandledException transactionHandledSQLException) {
            
        }
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
        
        assertFalse(s.isPresent());
    }
    
    @Test
    public void directJdbcUsage() throws Exception {
        List<String> results = new ArrayList<>();
        createDisposableTransaction()
                .useJdbcDirectly(
                        (connection) -> {
                            PreparedStatement ps = connection.prepareStatement(
                                    "SELECT * FROM table_1 " +
                                    "WHERE ( label LIKE ? ) AND ( id IS ? ) ");
                            ps.setString(1, "%ame%");
                            ps.setInt(2, 2);
                            
                            ResultSet rs = ps.executeQuery();
                            while ( rs.next() ) {
                                results.add(rs.getString("label"));
                            }
                        });
        
        assertEquals(1, results.size());
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }
    
    @Test
    public void directJdbcUsage_use_forbidden_methods() throws Exception {
        List<String> results = new ArrayList<>();
        
        try (JdbcTransaction transact = createDisposableTransaction()) {
            transact
                    .useJdbcDirectly(
                            (connection) -> {
                        connection.setAutoCommit(true); // here
                        fail();
                        PreparedStatement ps = connection.prepareStatement(
                                "SELECT * FROM table_1 " +
                                "WHERE ( label LIKE ? ) AND ( id IS ? ) ");
                        ps.setString(1, "%ame%");
                        ps.setInt(2, 2);
                        
                        ResultSet rs = ps.executeQuery();
                        while ( rs.next() ) {
                            results.add(rs.getString("label"));
                        }
                    });
        } catch (TransactionHandledSQLException | 
                TransactionHandledException transactionHandledSQLException) {
        }
        
        assertEquals(0, results.size());
        
        assertTrue(TEST_BASE.ifAllConnectionsReleased());
    }

}