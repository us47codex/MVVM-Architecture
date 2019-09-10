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
public interface ComplaintDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertComplaint(Complaint complaint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertComplaints(List<Complaint> complaints);

    @Update
    int updateComplaint(Complaint complaint);

    @Query("SELECT * FROM tbl_complaints WHERE email = :email")
    Single<Complaint> getComplaintByEmail(String email);

    @Query("SELECT * FROM tbl_complaints")
    Maybe<Complaint> getComplaint();

    @Query("SELECT * FROM tbl_complaints WHERE id=:id")
    Maybe<Complaint> getComplaintById(long id);

    @Query("SELECT * FROM tbl_complaints")
    Single<List<Complaint>> getAllComplaints();

    @Query("DELETE FROM tbl_complaints")
    void deleteAllComplaint();

//    @Transaction
//    public void updateUserByUserId(User user) {
//        user.setId(getUserByUserId(user.getUserId()).blockingGet().getId());
//        updateUser(user);
//    }
}
