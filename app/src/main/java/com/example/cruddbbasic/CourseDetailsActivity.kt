package com.example.firebaseproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnrememberedMutableState")
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
                            CenterAlignedTopAppBar(
                                title = { Text("Course List", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = Color.White
                                )
                            )
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            val courseList = mutableStateListOf<List?>()
                            val db = FirebaseFirestore.getInstance()

                            db.collection("List").get()
                                .addOnSuccessListener { queryDocumentSnapshots ->
                                    if (!queryDocumentSnapshots.isEmpty) {
                                        val list = queryDocumentSnapshots.documents
                                        for (d in list) {
                                            val c: List? = d.toObject(List::class.java)
                                            c?.ID = d.id
                                            Log.e("TAG", "Student id is : " + c!!.ID)
                                            courseList.add(c)
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@CourseDetailsActivity,
                                            "No data found in Database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@CourseDetailsActivity,
                                        "Fail to get the data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            Spacer(modifier = Modifier.height(10.dp))
                            CourseListUI(LocalContext.current, courseList)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListUI(context: Context,courseList: SnapshotStateList<List?>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(courseList) { index, item ->
            Card(
                onClick = {
                    val i = Intent(context, UpdateList::class.java)
                    i.putExtra("Name", item?.Name)
                    i.putExtra("Age", item?.Age)
                    i.putExtra("Address", item?.Address)
                    i.putExtra("ID", item?.ID)
                    context.startActivity(i)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item?.Name?.let {
                        Text(
                            text = it,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    item?.Age?.let {
                        Text(
                            text = "Age: $it",
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    item?.Address?.let {
                        Text(
                            text = "Address: $it",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            deleteDataFromFirebase(courseList[index]?.ID, courseList)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Delete Course", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun updateDataToFirebase(
    courseID: String?,
    name: String?,
    duration: String?,
    description: String?,
    context: Context
) {
    val updatedCourse = List(courseID, name, duration, description)

    val db = FirebaseFirestore.getInstance();
    db.collection("List").document(courseID.toString()).set(updatedCourse)
        .addOnSuccessListener {
            Toast.makeText(context, "List Updated successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, CourseDetailsActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(context, "Fail to update: " + it.message, Toast.LENGTH_SHORT)
                .show()
        }

}

private fun deleteDataFromFirebase(ID: String?, courseList: SnapshotStateList<List?>) {
    val db = FirebaseFirestore.getInstance()
    db.collection("List").document(ID.toString()).delete()
        .addOnSuccessListener {
            Log.d("Firebase", "Course Deleted successfully.")
            courseList.removeIf { it?.ID == ID }
        }
        .addOnFailureListener {
            Log.d("Firebase", "Fail to delete course.")
        }
}
