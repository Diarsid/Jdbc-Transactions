# Jdbc Transactions project
Simple API to streamline SQL operations through plain bare JDBC. 

### Example

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
        
        transaction.doQuery(
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
        transaction.doUpdate(
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
        transaction.doBatchUpdate(
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
                .ifTrue(anotherCondition())      // <- UnsupportedOperationException!
                .doesQueryHaveResults("...");

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
