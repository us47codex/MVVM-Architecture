package com.us47codex.mvvmarch.roomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created by Upendra Shah on 24 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

@Database(entities = {User.class,
        Complaint.class},
        version = 2,
        exportSchema = false)
@TypeConverters(Converter.class)
public abstract class SunTecDatabase extends RoomDatabase {

    private static SunTecDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract ComplaintDao complaintDao();

    public static SunTecDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SunTecDatabase.class, "aavgo_guest_db")
//                    .allowMainThreadQueries()  // allow queries on the main thread. Don't do this on a real app! See PersistenceBasicSample for an example.
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add migration for upgrading db version
                    .fallbackToDestructiveMigration() // Gracefully handle missing migration paths :: https://developer.android.com/training/data-storage/room/migrating-db-versions.html#handle-missing-migrations
                    .build();
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
// Since we didn't alter the table, there's nothing else to do here.
        }
    };
}