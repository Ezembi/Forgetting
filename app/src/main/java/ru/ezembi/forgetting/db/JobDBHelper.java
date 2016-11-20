package ru.ezembi.forgetting.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import ru.ezembi.forgetting.Job;

/**
 * Created by Victor on 06.11.2016.
 * Helper для работы с БД
 */
public class JobDBHelper extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "jobs";         //название таблицы
    public final static String _ID = BaseColumns._ID;       //id записи
    public final static String COLUMN_JOB = "job";          //название столбца для дела
    public final static String COLUMN_COMPLETE = "complete";//название столбца для подтверждения

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "job.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    public JobDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // sql строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_JOB + " TEXT NOT NULL, "
                + COLUMN_COMPLETE + " INTEGER DEFAULT 0);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("JobDBHelper", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }

    public ArrayList<Job> select(){
        ArrayList<Job> jobs = new ArrayList<>();

        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                _ID,
                COLUMN_JOB,
                COLUMN_COMPLETE };

        // Делаем запрос
        Cursor cursor = db.query(
                TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        try {
            Log.i("JobDBHelper", "selected " + cursor.getCount() + " rows.\n\n");

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int id = cursor.getInt(cursor.getColumnIndex(_ID));
                String job = cursor.getString(cursor.getColumnIndex(COLUMN_JOB));
                boolean complete = (cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETE)) == 0) ? false : true;

                Job newJob = new Job(id, job, complete);

                Log.i("JobDBHelper", "select " + newJob);

                jobs.add(newJob);

            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
            db.close();
        }

        return jobs;
    }

    public void insert(Job job) {

        // Gets the database in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(COLUMN_JOB, job.getJob());
        values.put(COLUMN_COMPLETE, job.isComplete());

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        Log.i("JobDBHelper", "1 row insert id = " + newRowId);
    }

    public void update(Job job) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_JOB, job.getJob());
        values.put(COLUMN_COMPLETE, job.isComplete());
        long newRowId = db.update(
                TABLE_NAME,
                values,
                _ID + "= ?",
                new String[]{job.getId() + ""});

        db.close();
        Log.i("JobDBHelper", "1 row update id = " + newRowId);
    }

    public void delete(Job job) {
        // Gets the database in write mode
        SQLiteDatabase db = getWritableDatabase();

        long newRowId = db.delete(
                TABLE_NAME,
                _ID + "= ?",
                new String[]{job.getId() + ""});

        db.close();
        Log.i("JobDBHelper", "1 row delete id = " + newRowId);
    }
}
