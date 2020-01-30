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
        version = 4,
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4) // Add migration for upgrading db version
                    .fallbackToDestructiveMigration() // Gracefully handle missing migration paths :: https://developer.android.com/training/data-storage/room/migrating-db-versions.html#handle-missing-migrations
                    .build();
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
// Since we didn't alter the table, there's nothing else to do here.
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_start_date' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_end_date' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_lat' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_long' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_status' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'in_date' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'out_lat' VARCHAR");
            database.execSQL("ALTER TABLE 'tbl_complaints' ADD COLUMN 'out_long' VARCHAR");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'Fruit' ("
                    + "'id' INTEGER, "
                    + "'visit_data' TEXT, "
                    + "'report_no' TEXT, "
                    + "'mc_type' TEXT, "
                    + "'visit_type' TEXT, "
                    + "'visit_date' TEXT, "
                    + "PRIMARY KEY('id'))");
        }
    };

//    public Completable clearAllTables1(){
//        return Observable
//    }
}