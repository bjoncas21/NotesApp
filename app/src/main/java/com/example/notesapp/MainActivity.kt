package com.example.notesapp

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "note_list") {
        composable("note_list") {
            val noteViewModel: NoteViewModel = viewModel()
            NoteListScreen(navController, noteViewModel)
        }
        composable("create_note") {
            CreateNoteScreen(navController)
        }
        composable(
            "view_note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: return@composable
            ViewEditNoteScreen(noteId)
        }
    }
}

@Composable
fun NoteListScreen(navController: NavHostController, noteViewModel: NoteViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("lastModified") }
    var expandedSort by remember { mutableStateOf(false) }

    val notes by when (sortOrder) {
        "lastModified" -> noteViewModel.allNotes.collectAsState(initial = emptyList())
        "dateCreated" -> noteViewModel.getNotesByDateCreated().collectAsState(initial = emptyList())
        "length" -> noteViewModel.getNotesByLength().collectAsState(initial = emptyList())
        "alphabetical" -> noteViewModel.getNotesAlphabetically().collectAsState(initial = emptyList())
        else -> noteViewModel.allNotes.collectAsState(initial = emptyList())
    }

    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notepad") },
                backgroundColor = Color(0xFF795548),
                contentColor = Color.White
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_note") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search notes") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = { expandedSort = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF795548))
                ) {
                    Text(
                        text = "Sort by: ${sortOrder.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }}",
                        color = Color.White // Set the text color to white to match the top bar content color
                    )
                }

                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    DropdownMenuItem(onClick = {
                        sortOrder = "lastModified"
                        expandedSort = false
                    }) {
                        Text("Last Modified")
                    }
                    DropdownMenuItem(onClick = {
                        sortOrder = "dateCreated"
                        expandedSort = false
                    }) {
                        Text("Date Created")
                    }
                    DropdownMenuItem(onClick = {
                        sortOrder = "length"
                        expandedSort = false
                    }) {
                        Text("Note Length")
                    }
                    DropdownMenuItem(onClick = {
                        sortOrder = "alphabetical"
                        expandedSort = false
                    }) {
                        Text("Alphabetical")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(filteredNotes) { note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { navController.navigate("view_note/${note.id}") },
                        onDeleteClick = { noteViewModel.delete(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onNoteClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNoteClick)
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(note.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Last edit: ${note.lastModified}", fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
    Divider(color = Color.LightGray, thickness = 1.dp)
}

@Composable
fun ViewEditNoteScreen(noteId: Int) {
    val viewModel: NoteViewModel = viewModel()
    var note by remember { mutableStateOf<Note?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedContent by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId).collect { fetchedNote ->
            note = fetchedNote
            editedTitle = fetchedNote?.title ?: ""
            editedContent = fetchedNote?.content ?: ""
        }
    }

    note?.let { currentNote ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditing) "Edit Note" else "View Note") },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = {
                                viewModel.update(currentNote.copy(title = editedTitle, content = editedContent, lastModified = Date()))
                                isEditing = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (isEditing) {
                    TextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = editedContent,
                        onValueChange = { editedContent = it },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxHeight()
                    )
                } else {
                    Text(currentNote.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentNote.content)

                    currentNote.imageUri?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }
    } ?: run {
        Text("Loading...")
    }
}


@Composable
fun CreateNoteScreen(navController: NavHostController) {
    val viewModel: NoteViewModel = viewModel()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Only the launcher for image picking (uploading from the gallery)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Note") },
                actions = {
                    IconButton(onClick = {
                        viewModel.insert(Note(
                            title = title,
                            content = content,
                            lastModified = Date(),
                            imageUri = imageUri?.toString() // Store image URI
                        ))
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = content, onValueChange = { content = it }, label = { Text("Content") })

            Spacer(modifier = Modifier.height(16.dp))

            // Button to select an image from the gallery (camera roll)
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Attach Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display selected image if available
            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Attached Image",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}
