package com.example.firebaseproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import android.content.Context
import android.content.Intent

import android.text.TextUtils

import android.widget.Toast


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.firebaseproject.ui.theme.greenColor

import com.google.firebase.firestore.FirebaseFirestore

class UpdateList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        topBar = {
                            TopAppBar(backgroundColor = greenColor,
                                title = {
                                    Text(
                                        text = "Update",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                })
                        }) { innerPadding ->
                        Text(
                            modifier = Modifier.padding(innerPadding),
                            text = "Cap nhat du lieu."
                        )

                        firebaseUI(
                            LocalContext.current,
                            intent.getStringExtra("Name"),
                            intent.getStringExtra("Age"),
                            intent.getStringExtra("Address"),
                            intent.getStringExtra("ID")
                        )


                    }
                }
            }
        }
    }


    @Composable
    fun firebaseUI(
        context: Context,
        name: String?,
        age: String?,
        address: String?,
        ID: String?
    ) {

        val Name = remember {
            mutableStateOf(name)
        }

        val Age = remember {
            mutableStateOf(age)
        }

        val Address = remember {
            mutableStateOf(address)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            TextField(
                value = Name.value.toString(),

                onValueChange = { Name.value = it },

                placeholder = { Text(text = "Nhập tên SV") },

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),

                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

                singleLine = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = Age.value.toString(),

                onValueChange = { Age.value = it },

                placeholder = { Text(text = "Nhập tuổi SV") },

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),

                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

                singleLine = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = Address.value.toString(),

                onValueChange = { Address.value = it },

                placeholder = { Text(text = "Nhập địa chỉ SV") },

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),

                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

                singleLine = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (TextUtils.isEmpty(Name.value.toString())) {
                        Toast.makeText(context, "Please enter course name", Toast.LENGTH_SHORT)
                            .show()
                    } else if (TextUtils.isEmpty(Age.value.toString())) {
                        Toast.makeText(
                            context,
                            "Please enter course Duration",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else if (TextUtils.isEmpty(Address.value.toString())) {
                        Toast.makeText(
                            context,
                            "Please enter course description",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        updateDataToFirebase(
                            ID,
                            Name.value,
                            Age.value,
                            Address.value,
                            context
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Update Data", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    private fun updateDataToFirebase(
        ID: String?,
        name: String?,
        age: String?,
        address: String?,
        context: Context
    ) {
        val updatedCourse = List(ID, name, age, address)

        val db = FirebaseFirestore.getInstance();
        db.collection("List").document(ID.toString()).set(updatedCourse)
            .addOnSuccessListener {
                Toast.makeText(context, "List Updated successfully..", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, CourseDetailsActivity::class.java))

            }.addOnFailureListener {
                Toast.makeText(context, "Fail to update: " + it.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }
}

