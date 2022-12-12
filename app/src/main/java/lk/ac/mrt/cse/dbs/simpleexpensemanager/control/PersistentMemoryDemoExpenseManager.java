package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.support.annotation.Nullable;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.mydatabase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentMemoryDemoExpenseManager extends ExpenseManager{
    private final mydatabase mydb;


    public PersistentMemoryDemoExpenseManager(@Nullable Context context){
        this.mydb = new mydatabase(context);
        try{
            setup();
        }catch(Exception e){
            System.out.println("Setup error at Persistent Expense Manager");
        }

    }

    @Override
    public void setup() {
        /*** Begin generating dummy data for persistent implementation ***/

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(mydb);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(mydb);
        setAccountsDAO(persistentAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

        /** End **/
    }
}
