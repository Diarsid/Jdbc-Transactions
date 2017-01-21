# Jdbc Transactions project
Simple API to streamline SQL operations through plain bare JDBC. 

### Usage

Simple SELECT query:
```java
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.PerRowOperation;
import diarsid.jdbc.transactions.core.JdbcTransactionFactory;
import diarsid.jdbc.transactions.exceptions.TransactionHandledException;
...
    try {
        JdbcTransaction transaction = factory.createTransaction();
        transaction.doQuery(
                "SELECT * " +
                "FROM table ", 
                doForEachRow());    // <- operation that will be performed for each row
        transaction.commit();
    } catch (TransactionHandledException e) {
        // your code here
        // there is no need to rollbackAndTerminate or close transaction 
        // manually if exception has been thrown.
    }  
        
    ...
    
    PerRowOperation doForEachRow() {
        return (row) -> {
            // any actions you want to perform with data 
            // which you can get from single row of SQL query resulting table
            System.out.println( (String) row.get("col_string") ); 
            System.out.println( (int) row.get("col_integer") );
            System.out.println( (boolean) row.get("col_boolean") );
        };
    }
        
```
Simple SELECT query with wildcards:

```java
    try {
        JdbcTransaction transaction = factory.createTransaction();
        
        transaction.doQueryVarargParams(
                "SELECT * " +
                "FROM table " +
                "WHERE ( col_1 IS ? ) AND ( col_2 IS ? ) ",  
                doForEachRow(),         
                "value_1", "value_2");  // <- wildcard (?) parameters as Object...
        
        List<Object> paramsList = getYourParams();
        transaction.doQuery(
                "SELECT * " +
                "FROM table " +
                "WHERE ( col_1 IS ? ) AND ( col_2 IS ? ) ",  
                doForEachRow(),         
                paramsList);   // <- wildcard (?) parameters as List
        
        transaction.commit();
    } catch (TransactionHandledException e) {
        // ... 
    }  
```    
Simple update query (INSERT, DELETE, UPDATE)

```java
    try {
        JdbcTransaction transaction = factory.createTransaction();
        transaction.doUpdateVarargParams(
                "INSERT INTO table (col_1, col_2, col_3) " +
                "VALUES ( ?, ?, ? )", 
                "value_1", true, 42);  // <- wildcard (?) parameters
        transaction.commit();
    } catch (TransactionHandledException e) {
        // ... 
    }  
```
Batch update queries:

```java
import ...
import diarsid.jdbc.transactions.core.Params;
import static diarsid.jdbc.transactions.core.Params.params;
...
    try {
        JdbcTransaction transaction = factory.createTransaction();

        // 1-st way
        transaction.doBatchUpdateVarargParams(
                "INSERT INTO table (col_1, col_2, col_3) " +
                "VALUES ( ?, ?, ? )", 
                params("value_1", true, 42),
                params("value_2", false, 72),
                params("value_3", true, 56),
                params("value_4", true, 32),
                params("value_5", false, 78),
                params("value_7", true, 26));  

        // 2-nd way
        Set<Params> params = youToGetParamsFrom(yourBusinessEntities());
        transaction.doBatchUpdate(
                "INSERT INTO table (col_1, col_2, col_3) " +
                "VALUES ( ?, ?, ? )", 
                params);

        transaction.commit();
    } catch (TransactionHandledException e) {
        // ... 
    } 
```
Conditional methods execution:

```java
    try {
        JdbcTransaction transaction = factory.createTransaction();

        transaction
                .ifTrue(yourCustomCondition())   // <- your boolean value here...
                .doQuery(                        // <- ...this query will be invoked
                        "SELECT * " +            //    only if yourCustomCondition()
                        "FROM table ",           //    returned TRUE.
                        doForEachRow());

        transaction                              // <- rollback if true.
                .ifTrue(someLogicalProblem())
                .rollbackAndProceed();

        transaction                              
                .ifTrue(yourCustomCondition())
                .countQueryResults(              // <- if TRUE, real query will be performed,
                        "SELECT * " +            //    otherwise 0 will be returned.
                        "FROM table");

        transaction                              
                .ifTrue(yourCustomCondition())
                .doesQueryHaveResults(           // <- if TRUE, real query will be performed,
                        "SELECT * " +            //    otherwise FALSE will be returned.
                        "FROM table");

        transaction                              
                .ifTrue(yourCustomCondition())
                .ifTrue(anotherCondition())      // <- you could stack conditions if you need.
                .doesQueryHaveResults("...");
        
        transaction                              
                .ifTrue(conditionSupposedToBeTrue())
                .ifTrue(conditionSupposedToBeFalse())  // <- if FALSE appeared at least at once,
                .ifTrue(conditionSupposedToBeTrue())   //    operation will be omitted despite of 
                .doesQueryHaveResults("...");          //    any other TRUE conditions.

        transaction.commit();
    } catch (TransactionHandledException e) {
        // ...
    }
```
Transaction flow:

```java
    try {
        JdbcTransaction transaction = factory.createTransaction();

        // there are few ways to control JdbcTransaction flow:

        // 1-st
        transaction.rollbackAndProceed();   // <- transaction will only be rolled back,
                                            //    but it remains open and valid for further work

        // 2-nd                                    
        transaction.rollbackAndTerminate();   // <- transaction will be rolled back and closed.
                                              //    Exception will be thrown.

        // 3-rd, the most obvious one.
        transaction.commit();
    } catch (TransactionHandledException e) {
        // ...
    } catch (TransactionTerminationException te) {   // <- this exception will be thrown
        // here you can do actions you need                on .rollbackAndTerminate() invocation. 
        // after transaction forcible termination               
    }
```
Disposable one-method-use-only autocommitted transaction:

```java
    try {
        return factory.createDisposableTransaction()  // <- transaction for one use only.
                .ifTrue(yourCustomCondition())        //    It doesn't require to be committed 
                .doesQueryHaveResults(                //    manually, you could just return.
                        "SELECT * " +                 //    If condition is FALSE, operation will be 
                        "FROM table");                //    omitted and transaction will be closed properly.
    } catch (TransactionHandledException e) {
        // ...
    }
```
Process the first row only:

```java
    try {
        factory.createDisposableTransaction()         
                .doQueryAndProcessFirstRow(     // <- process the first row only
                        "SELECT TOP 1 * " +
                        "FROM table " +
                        "ORDER BY some_col",
                        (firstRow) -> {
                            // do anything you need with 
                            // the first sql resulting table row.
                            // If there aren't any rows at all, 
                            // this method will not be invoked.
                        });                         
    } catch (TransactionHandledException e) {       
        // ...
    }
```
Convert and return data from the first row as Optional<T>:

```java
    try {
        return factory.createDisposableTransaction()   //  <- returns Optional<MyEntity>   
                .doQueryAndConvertFirstRow( 
                        MyEntity.class,                //  <- <T> type of Optional
                        "SELECT TOP 1 * " +
                        "FROM table " +
                        "ORDER BY some_col",
                        (firstRow) -> {
                            // use the first row to get your data and 
                            // return your data inside of Optional<T>.
                            return Optional.of(
                                    new MyEntity(
                                            (String) firstRow.get("string_col"), 
                                            (int) firstRow.get("integer_col")));
                            // If first row does not exist, Optional.empty() will be returned.
                            // This operation will not be invoked at all. 
                        });                                 
    } catch (TransactionHandledException e) {        
        // ...
    }
```

JdbcTransaction also is AutoCloseable thus it can be used with Java try-with-resources:
```java
    try (JdbcTransaction transaction =              // <- if you use try-with-resources you can
                factory.createTransaction()) {      //    omit commit() call. It will be comitted
                                                    //    automatically
        transaction
                .doQueryVarargParams(
                        "SELECT * " +
                        "FROM table " +
                        "WHERE  ( col_a IS ? ) AND ( col_b IS ? )", 
                        (row) -> {
                            // process result rows...
                        }, 
                        "param_1", 42);
                                                    // <- commit() can be omitted.
    } catch (TransactionHandledException e) {        
        // ...
    } 
```

Query methods for Java 8 Streams:
```java
    try (JdbcTransaction transaction = factory.createTransaction()) {      
                                                    
        Stream<MyEntity> stream = transaction
                .doQueryAndStreamVarargParams(  // <- query method returning Stream<T>
                        MyEntity.class,         // <- T type of Stream<T>. It have to be specified as explicit
                        "SELECT * " +           //    method argument due to Java Generics limitation.
                        "FROM table " +
                        "WHERE ( col_a IS ? ) AND ( col_b IS ? )", 
                        (row) -> {                                  // <- row-to-object conversion.
                            return new MyEntity(                    //    Each object, created here,
                                (String) row.get("col_string"),     //    will be returned in Stream
                                (int) row.get("col_int"));
                        },      
                        "param_1", 42);     
                                                    
    } catch (TransactionHandledException e) {        
        // ...
    }
```
Direct access to JDBC through the java.sql.Connection:
```java
    try (JdbcTransaction transaction =              
                factory.createTransaction()) {     
        
        transaction.useJdbcDirectly(
                (connection) -> {
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM table");
                    while ( rs.next() ) {
                        // do anything
                    }                       // <- there is no need to close anything manually
                });
        
        // Don't do these things:
        transaction.useJdbcDirectly(
                (connection) -> {       
                    Statement st = connection.createStatement();
                    Connection conn = st.getConnection();  //  <- this is NOT transaction-managed connection
                    conn.prepareStatement("...");          //     and any resources opened using it will
                                                           //     not be managed and closed automatically
                                                           
                    connection.setAutoCommit(anyBoolean());  // these methods are forbidden because they
                    connection.rollback();                   // they are altering JdbcTransaction behavior.
                    connection.commit();                     // Any attempt to invoke them will throw an exception.
                                                             
                    connection.close();   // <- .close() and .abort(...) methods 
                });                       //    invocations will be ignored.
                    
    } catch (TransactionHandledException e) {        
        // ...
    }
```
JdbcTransaction records your SQL statements. If exceptions occures, SQL history 
of this particular transaction will be logged. Here is a logged SQL history 
example of a failed batch update operations:

```
[Jdbc.Transactions] 21 22:03:57.823 ERROR [diarsid.jdbc.transactions.core.JdbcTransactionWrapper] -  
[SQL HISTORY] 
[0] 
    INSERT INTO table_1 (id, label, index, active) 
    VALUES (?, ?, ?, ?) 
        ( 6, name_6, 60, false ) 
        ( 4, name_4, 40, false ) 
        ( 5, name_5, 50, false ) 
[1] 
    INSERT INTO table_1 (id, label, index, active) 
    VALUES (?, ?, ?, ?) 
        ( 8, name_8, 70, false ) 
        ( 7, name_7, 740, false ) 
[2] 
    INSERT INTO table_1 (id, label, index, active) 
    VALUES (?, ?, ?, ?) 
        ( 8, name_7, 70, false ) 
```
It is possible to force JdbcTransaction to log SQL history after closing anyway:

```java
    try (JdbcTransaction transaction = factory.createTransaction()) { 
        
        // any operations before
        
        transaction
                .logHistoryAfterCommit()    //  <- SQL history will be logged anyway
                .doBatchUpdateVarargParams(
                        "INSERT INTO table (col_1, col_2, col_3) " +
                        "VALUES ( ?, ?, ? )", 
                        params("value_1", true, 42),
                        params("value_2", false, 72),
                        params("value_3", true, 56),
                        params("value_4", true, 32),
                        params("value_5", false, 78),
                        params("value_7", true, 26)); 
        
        // any operations after        
        // it doesn't matter when exactly JdbcTransaction.logHistoryAfterCommit() 
        // would be invoked - SQL history is being recorded for the whole transaction.
    } catch (TransactionHandledSQLException e) {
        // ...
    }
```
SQL history can be also obtained from transaction object directly as String by .getSqlHistory();
Note that it should be invoked before transaction would be commited.

If you don't like standard SQL history format, you can create your own formatter as shown below.
Then you can inject it into JdbcTransactionFactory instance.

```java

import diarsid.jdbc.transactions.SqlHistoryFormattingAlgorithm;

public class MySqlFormat implements SqlHistoryFormattingAlgorithm {

    @Override
    public String formatSql(String sqlHistory, String parametersLineTabSign) {
        // String 'parametersLineTabSign' is used to distinguish real SQL lines 
        // and parameters inserted used during each statement execution.
    }
}
```