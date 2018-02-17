package com.tmc.concentrationgame.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by sammy on 2/17/2018.
 */

@Entity(tableName = "users")
class UserModel() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    var name: String = ""

    var score: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        score = parcel.readInt()
    }

    constructor(id: Int, name: String, score: Int) : this() {
        this.id = id
        this.name = name
        this.score = score
    }


    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}
