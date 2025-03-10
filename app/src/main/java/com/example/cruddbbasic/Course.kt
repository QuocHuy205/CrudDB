package com.example.firebaseproject

import com.google.firebase.firestore.Exclude

data class List(
    @Exclude var ID: String? = "",
    var Name: String? = "",
    var Age: String? = "",
    var Address: String? = ""



)
