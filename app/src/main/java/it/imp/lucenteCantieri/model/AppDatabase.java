package it.imp.lucenteCantieri.model;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ClienteGerarchiaEntity.class, ClienteValoreLivelloEntity.class, TaskCantiereEntity.class, TaskCantiereImg.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract ClienteLivelliDao clienteLivelloDao();
    public abstract ClienteGerarchiaDao clienteGerarchiaDao();
    public abstract TaskCantiereDao taskCantiereDao();

    public abstract TaskCantiereImgDao taskCantiereImgDao();

    public static AppDatabase getInstance(Context context) {
        //Il codice necessario per creare una instanza singleton
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }

        return instance;
    }

}
