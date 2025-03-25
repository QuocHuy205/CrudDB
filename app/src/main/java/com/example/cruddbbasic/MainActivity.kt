package com.example.cruddbbasic

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaseproject.ListDetailsActivity
import com.example.firebaseproject.List
import com.example.firebaseproject.UpdateList
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                setContent { FirebaseprojectTheme {
                    MaterialTheme(
                        colorScheme = darkColorScheme(
                            background = Color(0xFF1E1E1E)
                        )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF1C2A3A)
                        ) {
                            Scaffold(
                                topBar = {
                                    CenterAlignedTopAppBar(
                                        title = {
                                            Text(
                                                "Note App",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFA500)
                                            )
                                        },
                                        actions = {
                                            IconButton(onClick = { logout(this@MainActivity) }) {
                                                Icon(
                                                    imageVector = Icons.Default.ExitToApp,
                                                    contentDescription = "Logout",
                                                    tint = Color.White
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                            containerColor = Color(0xFF121212)
                                        )
                                    )
                                },
                                floatingActionButton = {
                                    val context = LocalContext.current
                                    FloatingActionButton(
                                        onClick = {
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    ListDetailsActivity::class.java
                                                )
                                            )
                                        },
                                        containerColor = Color(0xFFFFA500)
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Add",
                                            tint = Color.White
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                Column(modifier = Modifier.padding(innerPadding)) {
                                    val courseList = remember { mutableStateListOf<List?>() }
                                    val db = FirebaseFirestore.getInstance()

                                    db.collection("List").get()
                                        .addOnSuccessListener { queryDocumentSnapshots ->
                                            if (!queryDocumentSnapshots.isEmpty) {
                                                val list = queryDocumentSnapshots.documents
                                                for (d in list) {
                                                    val c: List? = d.toObject(List::class.java)
                                                    c?.ID = d.id
                                                    courseList.add(c)
                                                }
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "No data found in Database",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this@MainActivity,
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
                } }
            }
        }, 1000)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CourseListUI(context: Context, courseList: SnapshotStateList<List?>) {
        LazyColumn(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(courseList) { _, item ->
                Card(
                    onClick = {
                        val i = Intent(context, UpdateList::class.java)
                        i.putExtra("Title", item?.Title)
                        i.putExtra("Description", item?.Description)
                        i.putExtra("ID", item?.ID)
                        context.startActivity(i)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF394B5F)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        item?.Title?.let {
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        item?.Description?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = Color(0xFFD0D0D0)
                            )
                        }
                    }
                }
            }
        }
    }
}


