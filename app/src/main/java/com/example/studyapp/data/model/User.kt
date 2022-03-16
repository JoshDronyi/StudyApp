package com.example.studyapp.data.model

import androidx.room.ColumnInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    var uid: String,
    @ColumnInfo(name = "photo_url")
    var photoUrl: String? = null,
    @ColumnInfo(name = "first_name")
    var firstName: String = "Cat Morpheus",
    @ColumnInfo(name = "last_name")
    var lastName: String = "The Great.",
    var alias: String? = "Steven",
    var email: String? = "cats@rule.com",
    var role: String? = "student",
    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = null,
    var isDefault: Boolean = true,
    @ColumnInfo(name = "batch_start_date")
    var batchStartDate: String = ""
) {
    companion object {
        private const val DEFAULT_USER_PIC =
            "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/close-up-of-cat-wearing-sunglasses-while-sitting-royalty-free-image-1571755145.jpg"

        fun newBlankInstance(): User {
            return User(
                uid = "",
                photoUrl = DEFAULT_USER_PIC
            )
        }
    }
}