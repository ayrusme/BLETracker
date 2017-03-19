package teamfour.bluetoothtracker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Surya Raman on 12-03-2017.
 */

public class dbIO extends SQLiteOpenHelper
{
    /*   Declaring variables for the database*/
    public static  final String COL1 = "IMEI";
    public static  final String COL2 = "BEACONID";




    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table locationDB(CREATE TABLE locationDB(IMEI VARCHAR, BEACONID VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*LMAO DO NOTHING*/
    }

    public dbIO(Context context)
    {
        super(context, "locationDB", null, 1);
    }

    public void writeData(String imei, String bid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if (CheckIfDataIsInDB(COL1,COL2))
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL1, imei);
            contentValues.put(COL2, bid);
            long result = db.insert("locationDB", null, contentValues);
            if (result == -1) {
                Log.e("Error", "Cannot Write DB");
            }
        }
    }

    public boolean CheckIfDataIsInDB(String dbfield, String fieldValue)

    {

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from locationDB where " + dbfield + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
