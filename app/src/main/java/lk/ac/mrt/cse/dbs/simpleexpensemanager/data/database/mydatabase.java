package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.database.Cursor;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class mydatabase extends SQLiteOpenHelper {
    private static final String DATABASE = "200332X.db";
    private static final String ACCOUNT_TABLE = "account";
    private static final String TRANSACTION_TABLE = "account_transaction";

    public mydatabase(@Nullable Context context) {
        super(context, DATABASE, null, 1);
    }


    @Override
    // To create tables
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String acc_table_creation = "CREATE TABLE " + ACCOUNT_TABLE + "( accountNo TEXT PRIMARY KEY ," +
                "bankName TEXT  ," +
                "accountHolderName TEXT, " +
                "balance REAL"
                + ")";
        sqLiteDatabase.execSQL(acc_table_creation);
        String tran_table_creation = "CREATE TABLE " + TRANSACTION_TABLE +
                " (transaction_no INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "accountNo TEXT," +
                "date TEXT, " +
                "expenseType TEXT ," +
                "amount REAL"
                + ")";
        sqLiteDatabase.execSQL(tran_table_creation);
    }

    @Override
    // if tables are already exists, delete them first and create again
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //delete the existing tables in database
        String drop_create_acc_table = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;
        sqLiteDatabase.execSQL(drop_create_acc_table);
        String drop_create_tran_table = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;
        sqLiteDatabase.execSQL(drop_create_tran_table);

        // create the database again
        onCreate(sqLiteDatabase);
    }

    //To insert data
    public boolean onInsertData(String table_name, ContentValues contentvalues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long feedback;
        try {
            feedback = sqLiteDatabase.insertOrThrow(table_name, null, contentvalues);
        } catch (Exception e) {
            feedback = -1;
            System.out.print("Error occurred in insertion");
        }
        return feedback != -1;
    }

    //To query data
    public Cursor getDataWithLimit(String table_name, String[] columns, String selection, String[] selectionArgs, String groupBy,
                                   String having, String orderBy, String limit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        return sqLiteDatabase.query(table_name, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor getData(String table_name, String[] columns, String selection, String[] selectionArgs, String groupBy,
                          String having, String orderBy) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        return sqLiteDatabase.query(table_name, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public boolean updateTable(String table_name, ContentValues contentvalues, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long feedback;
        try {
            feedback = sqLiteDatabase.update(table_name, contentvalues, whereClause, whereArgs);
        } catch (Exception e) {
            feedback = -1;
        }
        return feedback != -1;
    }

    //DELETE ROW
    public int deleteRow(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(table, whereClause, whereArgs);
    }
}