package com.us47codex.mvvmarch.roomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Upendra Shah on 04 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

@Entity(tableName = "tbl_visit_draft")
public class VisitDraft {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "visit_data")
    private String visitData;

    @ColumnInfo(name = "report_no")
    private String report_no;

    @ColumnInfo(name = "mc_type")
    private String mcType;

    @ColumnInfo(name = "visit_type")
    private String visit_type;

    @ColumnInfo(name = "visit_date")
    private String visit_date;
}
