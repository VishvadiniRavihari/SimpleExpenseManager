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

    private static final String TABLE = "account_transaction";
    private static final String TRANSACTION_DATE = "date";
    private static final String TRANSACTION_ACCOUNT_NO = "accountNo";
    private static final String TRANSACTION_EXPENSE_TYPE = "expenseType";
    private static final String TRANSACTION_AMOUNT = "amount";

    public PersistentTransactionDAO(mydatabase db) {
        this.mydb = db;
    }

    @Override
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
        this.mydb.onInsertData(TABLE, transactionContent);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor resultSet = this.mydb.getData(TABLE, null, null, null, null, null, null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (resultSet.getCount() != 0) {

            while (resultSet.moveToNext()) {
                String transDate = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_DATE));
                String accountNo = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_ACCOUNT_NO));
                String expenseType = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_EXPENSE_TYPE));
                double amount = resultSet.getDouble(resultSet.getColumnIndex(TRANSACTION_AMOUNT));
                Date date = stringToDate(transDate);

                Transaction transaction = new Transaction(date, accountNo, getExpense(expenseType), amount);
                transactions.add(transaction);
            }
        }
        resultSet.close();
        return transactions;
    }


    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor resultSet = this.mydb.getDataWithLimit(TABLE, null, null, null, null, null, null, Integer.toString(limit));
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (resultSet.getCount() != 0) {

            while (resultSet.moveToNext()) {
                String dateS = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_DATE));
                String accountNo = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_ACCOUNT_NO));
                String expenseType = resultSet.getString(resultSet.getColumnIndex(TRANSACTION_EXPENSE_TYPE));
                double amount = resultSet.getDouble(resultSet.getColumnIndex(TRANSACTION_AMOUNT));
                Date date = stringToDate(dateS);
                Transaction transaction = new Transaction(date, accountNo, getExpense(expenseType), amount);
                transactions.add(transaction);
            }
        }
        resultSet.close();
        return transactions;
    }


    private Date stringToDate(String stringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = dateFormat.parse(stringDate);
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


