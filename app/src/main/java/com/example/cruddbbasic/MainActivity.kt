package com.example.cruddbbasic

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaseproject.List
import com.example.firebaseproject.CourseDetailsActivity
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold() { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FirebaseUI(LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context) {
    val ID = remember { mutableStateOf("") }
    val Name = remember { mutableStateOf("") }
    val Age = remember { mutableStateOf("") }
    val Address = remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = Name.value,
                onValueChange = { Name.value = it },
                label = { Text("Tên sinh viên") },
                textStyle = TextStyle(fontSize = 16.sp)
            )

            OutlinedTextField(
                value = Age.value,
                onValueChange = { Age.value = it },
                label = { Text("Tuổi") },
                textStyle = TextStyle(fontSize = 16.sp)
            )

            OutlinedTextField(
                value = Address.value,
                onValueChange = { Address.value = it },
                label = { Text("Địa chỉ") },
                textStyle = TextStyle(fontSize = 16.sp)
            )

            Button(
                onClick = {
                    if (Name.value.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
                    } else if (Age.value.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập tuổi", Toast.LENGTH_SHORT).show()
                    } else if (Address.value.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show()
                    } else {

                        ID.value = UUID.randomUUID().toString()
                        addDataToFirebase(
                            ID.value, Name.value, Age.value, Address.value, context)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Thêm sinh viên", modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = { context.startActivity(Intent(context, CourseDetailsActivity::class.java)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "Xem danh sách sinh viên", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

fun addDataToFirebase(ID: String, Name: String, Age: String, Address: String, context: Context) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val dbCourses: CollectionReference = db.collection("List")
    val courses = List(ID, Name, Age, Address)

    dbCourses.add(courses).addOnSuccessListener {
        Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Lỗi: $e", Toast.LENGTH_SHORT).show()
    }
}
