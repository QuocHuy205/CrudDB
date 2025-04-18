package com.example.firebaseproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cruddbbasic.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

class UpdateList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpdateScreen(
                LocalContext.current,
                intent.getStringExtra("Title") ?: "",
                intent.getStringExtra("Description") ?: "",
                intent.getStringExtra("ID") ?: ""
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(context: Context, title: String, description: String, ID: String) {
    var titleState by remember { mutableStateOf(TextFieldValue(title)) }
    var descriptionState by remember { mutableStateOf(TextFieldValue(description)) }
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Note App",
                        color = Color(0xFFFFA500),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = {
                        db.collection("List").document(ID).delete().addOnSuccessListener {
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF1E1E1E)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                label = { Text("Title", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA500),
                    unfocusedBorderColor = Color.Gray
                )
            )
            OutlinedTextField(
                value = descriptionState,
                onValueChange = { descriptionState = it },
                label = { Text("Description", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA500),
                    unfocusedBorderColor = Color.Gray
                )
            )
            Button(
                onClick = {
                    if (titleState.text.isEmpty() || descriptionState.text.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        db.collection("List").document(ID).set(mapOf(
                            "Title" to titleState.text,
                            "Description" to descriptionState.text
                        )).addOnSuccessListener {
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("UPDATE ITEM", modifier = Modifier.padding(8.dp), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
