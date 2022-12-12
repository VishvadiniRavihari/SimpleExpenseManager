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

    public static final int DATABASE_VERSION = 1;

    private static final String ACCOUNT_TABLE = "account";

    private static final String TRANSACTION_TABLE = "account_transaction";


    public mydatabase(@Nullable Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }


    @Override
    // CREATE TABLES
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE " + ACCOUNT_TABLE + "( accountNo TEXT PRIMARY KEY ," +
                "bankName TEXT  ," +
                "accountHolderName TEXT, " +
                "balance REAL"
                + ")";
        String query2 = "CREATE TABLE " + TRANSACTION_TABLE +
                " (transaction_no INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "accountNo TEXT," +
                "date TEXT, " +
                "expenseType TEXT ," +
                "amount REAL"
                + ")";

        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //deletes existing database and create the database again
        String query1 = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;
        String query2 = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
        onCreate(sqLiteDatabase);


    }

    //INSERT INTO TABLES
    public boolean onInsertData(String table, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result;
        try {
            result = sqLiteDatabase.insertOrThrow(table, null, contentValues);
        } catch (Exception e) {
            result = -1;
            System.out.print("Data insertion error");
        }
        return result != -1;
    }

    //GET DATA  - SELECT
    public Cursor getDataWithLimit(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                                   String having, String orderBy, String limit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor getData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                          String having, String orderBy) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public boolean updateTable(String table, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result;
        try {
            result = sqLiteDatabase.update(table, contentValues, whereClause, whereArgs);
        } catch (Exception e) {
            result = -1;
        }
        return result != -1;
    }

    //DELETE ROW
    public int deleteRow(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(table, whereClause, whereArgs);
    }

    //DELETE TABLE CONTENT
    public void deleteTableContent(String table_name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + table_name);
    }
}