package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.mydatabase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private mydatabase mydb;

    private static final String TRANSACTION_TABLE = "account_transaction";
    private static final String TRANSACTION_DATE = "date";
    private static final String TRANSACTION_ACCOUNT_NO = "accountNo";
    private static final String TRANSACTION_EXPENSE_TYPE = "expenseType";
    private static final String TRANSACTION_AMOUNT = "amount";

    public PersistentTransactionDAO(mydatabase db) {
        this.mydb = db;
    }

    @Override
    //To log each transaction
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if (expenseType == ExpenseType.EXPENSE) {
            PersistentAccountDAO dao = new PersistentAccountDAO(this.mydb);
            try {
                Account userAccount = dao.getAccount(accountNo);
                if (userAccount.getBalance() < amount) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid Account");
            }
        }
        String transDate = date.toString();
        ContentValues transactionContent = new ContentValues();
        transactionContent.put(TRANSACTION_ACCOUNT_NO, accountNo);
        transactionContent.put(TRANSACTION_DATE, transDate);
        transactionContent.put(TRANSACTION_EXPENSE_TYPE, toStringExpense(expenseType));
        transactionContent.put(TRANSACTION_AMOUNT, amount);
        this.mydb.onInsertData(TRANSACTION_TABLE, transactionContent);
    }

    @Override
    //To return all transaction logs
    public List<Transaction> getAllTransactionLogs() {
        Cursor result = this.mydb.getData(TRANSACTION_TABLE, null, null, null, null, null, null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (result.getCount() != 0) {

            while (result.moveToNext()) {
                String transDate = result.getString(result.getColumnIndex(TRANSACTION_DATE));
                String accountNo = result.getString(result.getColumnIndex(TRANSACTION_ACCOUNT_NO));
                String expenseType = result.getString(result.getColumnIndex(TRANSACTION_EXPENSE_TYPE));
                double amount = result.getDouble(result.getColumnIndex(TRANSACTION_AMOUNT));
                Date date = stringToDate(transDate);

                Transaction transaction = new Transaction(date, accountNo, getExpense(expenseType), amount);
                transactions.add(transaction);
            }
        }
        result.close();
        return transactions;
    }


    @Override
    //To get paginated logs
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor result = this.mydb.getDataWithLimit(TRANSACTION_TABLE, null, null, null, null, null, null, Integer.toString(limit));
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (result.getCount() != 0) {

            while (result.moveToNext()) {
                String dateS = result.getString(result.getColumnIndex(TRANSACTION_DATE));
                String accountNo = result.getString(result.getColumnIndex(TRANSACTION_ACCOUNT_NO));
                String expenseType = result.getString(result.getColumnIndex(TRANSACTION_EXPENSE_TYPE));
                double amount = result.getDouble(result.getColumnIndex(TRANSACTION_AMOUNT));
                Date date = stringToDate(dateS);
                Transaction transaction = new Transaction(date, accountNo, getExpense(expenseType), amount);
                transactions.add(transaction);
            }
        }
        result.close();
        return transactions;
    }

    /*Three methods are added below for the conversion purposes*/
    private Date stringToDate(String string_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = dateFormat.parse(string_date);
        } catch (Exception e) {
            System.out.println(e);
        }
        return date;

    }

    private String toStringExpense(ExpenseType expenseType) {
        if (expenseType == ExpenseType.EXPENSE) {
            return "Expense";
        }
        return "Income";

    }

    private ExpenseType getExpense(String expenseType) {
        if (expenseType.equals("Expense")) {
            return ExpenseType.EXPENSE;
        } else {
            return ExpenseType.INCOME;
        }

    }
}


