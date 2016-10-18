package com.globalpaysolutions.yovendorecarga.customs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalpaysolutions.yovendorecarga.model.LocalNotification;
import com.globalpaysolutions.yovendorecarga.model.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josué Chávez on 01/10/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "YoVendoRecarga";

    // Operators table name
    private static final String TABLE_OPERATORS = "Operators";
    // Notifications table name
    private static final String NOTIFICATIONS_TABLE = "Notifications";

    // Operators Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_OPERATOR_NAME = "operator_name";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_LOGO = "logo";
    private static final String KEY_MNC = "mnc";
    private static final String KEY_LOGO_URL = "logo_url";
    private static final String KEY_STATE = "state";
    private static final String KEY_LOGO_VERSION = "logo_version";
    private static final String KEY_COUNTRY_ID = "country_id";
    private static final String KEY_LOGO_IMAGE = "logo_image";

    //Notifications table Columns names
    private static final String KEY_NOTIF_ID = "notif_id";
    private static final String KEY_AZME_ID = "azme_notif_id";
    private static final String KEY_TITLE = "notif_title";
    private static final String KEY_MESSAGE = "notif_message";
    private static final String KEY_SEEN = "notif_seen";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //OPERADORES
        String CREATE_OPERATORS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_OPERATORS + "("
                + KEY_ID + " INTEGER, " + KEY_OPERATOR_NAME + " TEXT,"
                + KEY_BRAND + " TEXT," + KEY_LOGO + " TEXT," + KEY_MNC + " TEXT,"
                + KEY_LOGO_URL + " TEXT," + KEY_STATE + " INTEGER," + KEY_LOGO_VERSION + " INTEGER,"
                + KEY_COUNTRY_ID + " INTEGER," + KEY_LOGO_IMAGE + " BLOB" + ")";

        db.execSQL(CREATE_OPERATORS_TABLE);

        //NOTIFICACIONES
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTIFICATIONS_TABLE + "("
                + KEY_NOTIF_ID + " INTEGER, " + KEY_AZME_ID + " INTEGER, " + KEY_TITLE + " TEXT, "
                + KEY_MESSAGE + " TEXT" + ")";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATORS);
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE);

        // Create tables again
        onCreate(db);
    }

    //region OPERADORES
    // Adding new contact
    public void addOperator(Operator operator)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, operator.getID());
        values.put(KEY_OPERATOR_NAME, operator.getOperatorName());
        values.put(KEY_BRAND, operator.getBrand());
        values.put(KEY_LOGO, operator.getLogo());
        values.put(KEY_MNC, operator.getMNC());
        values.put(KEY_LOGO_URL, operator.getLogoURL());
        values.put(KEY_STATE, operator.getState());
        values.put(KEY_LOGO_VERSION, operator.getLogoVersion());
        values.put(KEY_COUNTRY_ID, operator.getCountryID());
        values.put(KEY_LOGO_IMAGE, operator.getLogoImage());

        // Inserting Row
        db.insert(TABLE_OPERATORS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Operator getOperator(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPERATORS, new String[] { KEY_ID,
                        KEY_OPERATOR_NAME, KEY_BRAND, KEY_LOGO, KEY_MNC, KEY_LOGO_URL,
                        KEY_STATE, KEY_LOGO_VERSION, KEY_COUNTRY_ID, KEY_LOGO_IMAGE  }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Operator operator = new Operator();
        operator.setID(cursor.getInt(0));
        operator.setOperatorName(cursor.getString(1));
        operator.setBrand(cursor.getString(2));
        operator.setLogo(cursor.getString(3));
        operator.setMNC(cursor.getString(4));
        operator.setLogoURL(cursor.getString(5));
        operator.setState(cursor.getInt(6));
        operator.setLogoVersion(cursor.getInt(7));
        operator.setCountryID(cursor.getInt(8));
        operator.setLogoImage(cursor.getBlob(9));

        // return contact
        return operator;
    }

    // Getting All Contacts
    public List<Operator> getUserOperators(int pCountryID)
    {
        List<Operator> operatorsList = new ArrayList<Operator>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPERATORS, new String[] { KEY_ID,
                        KEY_OPERATOR_NAME, KEY_BRAND, KEY_LOGO, KEY_MNC, KEY_LOGO_URL,
                        KEY_STATE, KEY_LOGO_VERSION, KEY_COUNTRY_ID, KEY_LOGO_IMAGE  }, KEY_COUNTRY_ID + "=?",
                new String[] { String.valueOf(pCountryID) }, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Operator operator = new Operator();
                operator.setID(cursor.getInt(0));
                operator.setOperatorName(cursor.getString(1));
                operator.setBrand(cursor.getString(2));
                operator.setLogo(cursor.getString(3));
                operator.setMNC(cursor.getString(4));
                operator.setLogoURL(cursor.getString(5));
                operator.setState(cursor.getInt(6));
                operator.setLogoVersion(cursor.getInt(7));
                operator.setCountryID(cursor.getInt(8));
                operator.setLogoImage(cursor.getBlob(9));

                // Adding contact to list
                operatorsList.add(operator);
            }
            while (cursor.moveToNext());
        }

        // return contact list
        return operatorsList;
    }

    // Getting contacts Count
    public int getOperatorsCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_OPERATORS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating single contact
    public int updateOperator(Operator operator)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OPERATOR_NAME, operator.getOperatorName());
        values.put(KEY_BRAND, operator.getBrand());
        values.put(KEY_LOGO, operator.getLogo());
        values.put(KEY_MNC, operator.getMNC());
        values.put(KEY_LOGO_URL, operator.getLogoURL());
        values.put(KEY_STATE, operator.getState());
        values.put(KEY_LOGO_VERSION, operator.getLogoVersion());
        values.put(KEY_COUNTRY_ID, operator.getCountryID());
        values.put(KEY_LOGO_IMAGE, operator.getLogoImage());

        // updating row
        return db.update(TABLE_OPERATORS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(operator.getID()) });
    }

    // Deleting single contact
    public void deleteOperator(Operator operator)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPERATORS, KEY_ID + " = ?",
                new String[] { String.valueOf(operator.getID()) });
        db.close();
    }

    // Deleting operators by country
    public void deleteCountryOperators(int pCountryID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPERATORS, KEY_COUNTRY_ID + " = ?",
                new String[] { String.valueOf(pCountryID) });
        db.close();
    }
    //endregion



    //region NOTIFICATIONS
    public void addNotification(LocalNotification notification)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTIF_ID, notification.getNotificationID());
        values.put(KEY_AZME_ID, notification.getAzmeNotificationID());
        values.put(KEY_TITLE, notification.getNotificationTitle());
        values.put(KEY_MESSAGE, notification.getNotificationMessage());

        // Inserting Row
        db.insert(NOTIFICATIONS_TABLE, null, values);
        db.close(); // Closing database connection
    }

    public LocalNotification getNotification(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(NOTIFICATIONS_TABLE, new String[] { KEY_NOTIF_ID,
                        KEY_AZME_ID, KEY_TITLE, KEY_MESSAGE }, KEY_NOTIF_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        LocalNotification notification = new LocalNotification();
        notification.setNotificationID(cursor.getInt(0));
        notification.setAzmeNotificationID(cursor.getInt(1));
        notification.setNotificationTitle(cursor.getString(2));
        notification.setNotificationMessage(cursor.getString(3));

        // return contact
        return notification;
    }

    public LocalNotification getNotificationByAzmeID(int pAzmID)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(NOTIFICATIONS_TABLE, new String[] { KEY_NOTIF_ID,
                        KEY_AZME_ID, KEY_TITLE, KEY_MESSAGE }, KEY_AZME_ID + "=?",
                new String[] { String.valueOf(pAzmID) }, null, null, null, null);

        LocalNotification notification = new LocalNotification();

        if(cursor.moveToFirst())
        {
            notification.setNotificationID(cursor.getInt(0));
            notification.setAzmeNotificationID(cursor.getInt(1));
            notification.setNotificationTitle(cursor.getString(2));
            notification.setNotificationMessage(cursor.getString(3));
        }
        cursor.close();

        return notification;
    }

    public LocalNotification getNotificationByTitle(String pTitle)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(NOTIFICATIONS_TABLE, new String[] { KEY_NOTIF_ID,
                        KEY_AZME_ID, KEY_TITLE, KEY_MESSAGE }, KEY_TITLE + "=?",
                new String[] { pTitle }, null, null, null, null);

        LocalNotification notification = new LocalNotification();
        if (cursor.moveToFirst())
        {

            notification.setNotificationID(cursor.getInt(0));
            notification.setAzmeNotificationID(cursor.getInt(1));
            notification.setNotificationTitle(cursor.getString(2));
            notification.setNotificationMessage(cursor.getString(3));
        }
        cursor.close();



        //String selectQuery = "SELECT  * FROM " + NOTIFICATIONS_TABLE + " WHERE " + KEY_TITLE + "=?";

        //Cursor cursor = db.rawQuery(selectQuery,  new String[] {pTitle});
        // you can change it to
        // db.rawQuery("SELECT * FROM "+table+" WHERE KEY_KEY LIKE ?", new String[] {key+"%"});
        // if you want to get everything starting with that key value

        // looping through all rows and adding to list
        //if (cursor.moveToFirst()) {
        //    do {
        //        Log.d("searchKeyString","searching");
        //
        //        rtn.append(",").append(cursor.getString(2));
        //    } while (cursor.moveToNext());
        //
        //cursor.close();
        //Log.d("searchKeyString","finish search");


        //if (cursor != null)
        //    cursor.moveToFirst();
        //
        //LocalNotification notification = new LocalNotification();
        //notification.setNotificationID(cursor.getInt(0));
        //notification.setAzmeNotificationID(cursor.getInt(1));
        //notification.setNotificationTitle(cursor.getString(2));
        //notification.setNotificationMessage(cursor.getString(3));

        // return contact
        return notification;
    }

    public List<LocalNotification> getNotifications()
    {
        List<LocalNotification> notificationsList = new ArrayList<LocalNotification>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + NOTIFICATIONS_TABLE + " LIMIT 25 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                LocalNotification notif = new LocalNotification();
                notif.setNotificationID(Integer.parseInt(cursor.getString(0)));
                notif.setAzmeNotificationID(Integer.parseInt(cursor.getString(1)));
                notif.setNotificationTitle(cursor.getString(2));
                notif.setNotificationMessage(cursor.getString(3));

                // Adding contact to list
                notificationsList.add(notif);

            } while (cursor.moveToNext());
        }

        // return contact list
        return notificationsList;
    }

    public void deleteNotification(LocalNotification notification)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTIFICATIONS_TABLE, KEY_AZME_ID + " = ?",
                new String[] { String.valueOf(notification.getAzmeNotificationID()) });
        db.close();
    }

    public void deleteAllNotifications()
    {
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(NOTIFICATIONS_TABLE, null, null);

    }
    //endregion

}
