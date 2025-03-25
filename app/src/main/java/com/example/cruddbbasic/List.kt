package com.example.firebaseproject

import com.google.firebase.firestore.Exclude

data class List(
    @Exclude var ID: String? = "",
    var Title: String? = "",
    var Description: String? = "",
)
