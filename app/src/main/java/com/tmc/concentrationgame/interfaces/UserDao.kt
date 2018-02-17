package com.tmc.concentrationgame.interfaces

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.tmc.concentrationgame.models.UserModel

/**
 * Created by sammy on 2/17/2018.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserModel)

    @Query("SELECT * FROM users order by score desc")
    fun loadAllUsers(): Array<UserModel>
}
