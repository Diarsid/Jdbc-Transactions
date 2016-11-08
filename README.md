# Jdbc Transactions project
Simple API to streamline SQL operations through plain bare JDBC. 

### Example

Simple SELECT query:
```java
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
                "value_1", "value_2");  // <- wildcard (?) parameters
        transaction.commit();
    } catch (TransactionHandledException e) {
        // ... 
    }  
```    
