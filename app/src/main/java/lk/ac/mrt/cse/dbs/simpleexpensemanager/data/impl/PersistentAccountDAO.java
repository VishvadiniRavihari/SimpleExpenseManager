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

    private static final String TABLE = "account";

    private static final String ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_BANKNAME = "bankName";
    private static final String ACCOUNT_HOLDERNAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";

    public PersistentAccountDAO(mydatabase mydb) {
        this.mydb = mydb;
    }

    @Override
    public List<String> getAccountNumbersList() {

        Cursor resultSet = this.mydb.getData(TABLE,new String[]{ACCOUNT_NO},null,new String[]{},
                null,null,null);
        List <String> accountNumbersList = new ArrayList<String>();
        if(resultSet.getCount() != 0) {
            while (resultSet.moveToNext()) {
                accountNumbersList.add(resultSet.getString(0));
            }
        }
        resultSet.close();
        return accountNumbersList;
    }
    @Override
    public List<Account> getAccountsList() {
        String[] columns ={"accountNo","bankName","accountHolderName","balance"};
        Cursor resultSet = this.mydb.getData(TABLE,columns,null,null,null,null,null);
        List<Account> accountsList = new ArrayList<Account>();
        if(resultSet.getCount() != 0) {
            while (resultSet.moveToNext()) {
                String accountNo = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_NO));
                String bankName = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_BANKNAME));
                String accountHolderName = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_HOLDERNAME));
                double balance = resultSet.getDouble(resultSet.getColumnIndex(ACCOUNT_BALANCE));
                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accountsList.add(account);
            }
        }
        resultSet.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String selection = "accountNo = ?";
        Cursor resultSet = this.mydb.getData(TABLE,null,selection,new String[]{accountNo},null,null,null);
        if(resultSet.getCount() == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }
        String accountNO = "";
        String bankName = "";
        String accountHolderName = "";
        double balance = 0;
        while(resultSet.moveToNext()){
            accountNO = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_NO));
            bankName = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_BANKNAME));
            accountHolderName = resultSet.getString(resultSet.getColumnIndex(ACCOUNT_HOLDERNAME));
            balance = resultSet.getDouble(resultSet.getColumnIndex(ACCOUNT_BALANCE));
        }

        resultSet.close();
        return new Account(accountNO,bankName,accountHolderName,balance);
    }

    @Override
    public void addAccount(Account account) {
        ContentValues accountContent = new ContentValues();
        accountContent.put(ACCOUNT_NO, account.getAccountNo());
        accountContent.put(ACCOUNT_BANKNAME, account.getBankName());
        accountContent.put(ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        accountContent.put(ACCOUNT_BALANCE, account.getBalance());
        this.mydb.onInsertData(TABLE,accountContent);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int result = this.mydb.deleteRow("account","accountNo = ?",new String[]{accountNo});
        if(result == 0){
            throw new InvalidAccountException("Account number is invalid");
        }

    }

    @Override
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
        boolean result = this.mydb.updateTable(TABLE,accContent,"accountNo = ? ",new String[]{accountNo});
        if(!result){
            throw new InvalidAccountException("Account number is invalid");
        }
    }


}
