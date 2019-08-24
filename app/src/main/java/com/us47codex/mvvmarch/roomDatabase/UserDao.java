package com.us47codex.mvvmarch.roomDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Upendra Shah on 24 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Update
    int updateUser(User user);

    @Query("UPDATE tbl_user " +
            "SET email =:email " +
            ", profile =:photo " +
            ", mno=:phone " +
            ", name =:name " +
            ", first_name=:fName " +
            ", last_name=:lName " +
            "WHERE id = :user_id")
    void updateUserByUserId(String user_id, String email, String photo, String phone, String name, String fName, String lName);

    @Query("SELECT * FROM tbl_user WHERE email = :email")
    Single<User> getUserByEmail(String email);

    @Query("SELECT * FROM tbl_user")
    Single<List<User>> getAllUsers();

    @Query("DELETE FROM tbl_user")
    void deleteAllUser();

//    @Transaction
//    public void updateUserByUserId(User user) {
//        user.setId(getUserByUserId(user.getUserId()).blockingGet().getId());
//        updateUser(user);
//    }
}
