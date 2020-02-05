package com.us47codex.mvvmarch.roomDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by Upendra Shah on 24 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

@Dao
public interface VisitDraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVisitDraft(VisitDraft visitDraft);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVisitDrafts(List<VisitDraft> visitDrafts);

    @Update
    int updateVisitDraft(VisitDraft visitDraft);

    @Query("SELECT * FROM tbl_visit_draft")
    Maybe<VisitDraft> getVisitDraft();

    @Query("SELECT * FROM tbl_visit_draft WHERE id=:id")
    Maybe<VisitDraft> getVisitDraftById(long id);

    @Query("SELECT * FROM tbl_visit_draft ORDER BY id DESC")
    Single<List<VisitDraft>> getAllVisitDrafts();

    @Query("DELETE FROM tbl_visit_draft WHERE id=:id")
    Completable deleteVisitDraft(long id);

    @Query("DELETE FROM tbl_visit_draft")
    Completable deleteAllVisitDrafts();

//    @Transaction
//    public void updateUserByUserId(User user) {
//        user.setId(getUserByUserId(user.getUserId()).blockingGet().getId());
//        updateUser(user);
//    }
}
