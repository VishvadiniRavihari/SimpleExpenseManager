package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.mydatabase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final mydatabase mydb;
    private static final String ACCOUNT_TABLE = "account";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_BANKNAME = "bankName";
    private static final String ACCOUNT_HOLDERNAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";

    public PersistentAccountDAO(mydatabase mydb) {
        this.mydb = mydb;
    }

    @Override
    // To get a list of account numbers
    public List<String> getAccountNumbersList() {
        Cursor result = this.mydb.getData(ACCOUNT_TABLE,new String[]{ACCOUNT_NO},null,new String[]{}, null,null,null);
        List <String> accountNumbersList = new ArrayList<String>();

        if(result.getCount() != 0) {
            while (result.moveToNext()) {
                accountNumbersList.add(result.getString(0));
            }
        }
        result.close();

        return accountNumbersList;
    }
    @Override
    //To get a list of accounts
    public List<Account> getAccountsList() {

        String[] columns ={"accountNo","bankName","accountHolderName","balance"};
        Cursor result = this.mydb.getData(ACCOUNT_TABLE,columns,null,null,null,null,null);
        List<Account> accountsList = new ArrayList<Account>();

        if(result.getCount() != 0) {
            while (result.moveToNext()) {
                String accountNo = result.getString(result.getColumnIndex(ACCOUNT_NO));
                String bankName = result.getString(result.getColumnIndex(ACCOUNT_BANKNAME));
                String accountHolderName = result.getString(result.getColumnIndex(ACCOUNT_HOLDERNAME));
                double balance = result.getDouble(result.getColumnIndex(ACCOUNT_BALANCE));
                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accountsList.add(account);
            }
        }
        result.close();

        return accountsList;
    }

    @Override
    //To get an account
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String selection = "accountNo = ?";
        Cursor result = this.mydb.getData(ACCOUNT_TABLE,null,selection,new String[]{accountNo},null,null,null);
        if(result.getCount() == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }
        String accountNO = "";
        String bankName = "";
        String accountHolderName = "";
        double balance = 0;

        while(result.moveToNext()){
            accountNO = result.getString(result.getColumnIndex(ACCOUNT_NO));
            bankName = result.getString(result.getColumnIndex(ACCOUNT_BANKNAME));
            accountHolderName = result.getString(result.getColumnIndex(ACCOUNT_HOLDERNAME));
            balance = result.getDouble(result.getColumnIndex(ACCOUNT_BALANCE));
        }
        result.close();

        Account account = new Account(accountNO,bankName,accountHolderName,balance);
        return account;
    }

    @Override
    //To add an account
    public void addAccount(Account account) {
        ContentValues accContent = new ContentValues();
        accContent.put(ACCOUNT_NO, account.getAccountNo());
        accContent.put(ACCOUNT_BANKNAME, account.getBankName());
        accContent.put(ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        accContent.put(ACCOUNT_BALANCE, account.getBalance());
        this.mydb.onInsertData(ACCOUNT_TABLE,accContent);


    }

    @Override
    //To remove an account
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int result = this.mydb.deleteRow("account","accountNo = ?",new String[]{accountNo});
        if(result == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }

    }

    @Override
    //To update the balance
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance=0;
        double total = 0;
        try{
            Account account = getAccount(accountNo);
            balance = account.getBalance();
        }catch(Exception e){
            throw new InvalidAccountException("Invalid Account Number");
        }

        if (expenseType == ExpenseType.EXPENSE){
            if(balance < amount){
                throw new InvalidAccountException("Insufficient Account Balance");
            }
            total = balance-amount;
        }else{
            total = amount +balance;
        }
        ContentValues accContent = new ContentValues();
        accContent.put(ACCOUNT_BALANCE, total);
        boolean result = this.mydb.updateTable(ACCOUNT_TABLE,accContent,"accountNo = ? ",new String[]{accountNo});
        if(!result){
            throw new InvalidAccountException("Invalid Account Number");
        }
    }
}
