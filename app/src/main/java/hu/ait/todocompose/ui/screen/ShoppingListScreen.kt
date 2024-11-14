package hu.ait.todocompose.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.todocompose.data.TodoCategory
import hu.ait.todocompose.data.TodoItem
import hu.ait.todocompose.data.TodoPriority
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val todoList by viewModel.getAllToDoList().collectAsState(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<TodoItem?>(null) }

    // Scaffold with top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.clearAllTodos() }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete All", tint = Color.Red)
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Add")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // LazyColumn for displaying items
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(todoList) { todoItem ->
                    TodoCard(
                        todoItem,
                        onTodoDelete = { item -> viewModel.removeTodoItem(item) },
                        onTodoChecked = { item, checked -> viewModel.changeTodoState(item, checked) },
                        onEditClick = { item -> editItem = item }
                    )
                }
            }
            // Total cost
            val totalCost = "%.2f".format(todoList.sumOf { it.price.toDoubleOrNull() ?: 0.0 })
            Text(
                text = "Total Estimated Cost: $$totalCost",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    // Show add dialog
    if (showAddDialog) {
        TodoDialog(
            viewModel = viewModel,
            onCancel = { showAddDialog = false }
        )
    }
    // Show edit dialog
    editItem?.let { item ->
        TodoEditDialog(
            todoItem = item,
            viewModel = viewModel,
            onDismiss = { editItem = null }
        )
    }
}
// TodoEditDialog composable
@Composable
fun TodoEditDialog(
    todoItem: TodoItem,
    viewModel: ShoppingViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(todoItem.title) }
    var description by remember { mutableStateOf(todoItem.description) }
    var price by remember { mutableStateOf(todoItem.price) }
    var isImportant by remember { mutableStateOf(todoItem.isDone) }
    var category by remember { mutableStateOf(todoItem.category) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text("Edit Item", style = MaterialTheme.typography.titleMedium)
                CategoryDropdown(
                    selectedCategory = category,
                    onSelectionChanged = { category = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = title,
                    onValueChange = { title = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    value = description,
                    onValueChange = { description = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Estimated price") },
                    value = price,
                    onValueChange = { price = it }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                    Text("Bought")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        viewModel.editTodoItem(
                            originalTodo = todoItem,
                            editedTodo = todoItem.copy(
                                title = title,
                                description = description,
                                price = price,
                                isDone = isImportant,
                                category = category
                            )
                        )
                        onDismiss()
                    }) { Text("Save") }
                }
            }
        }
    }
}
// TodoDialog composable
@Composable
fun TodoDialog(
    viewModel: ShoppingViewModel,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isImportant by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(TodoCategory.FOOD) }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text("Add Item", style = MaterialTheme.typography.titleMedium)
                CategoryDropdown(
                    selectedCategory = category,
                    onSelectionChanged = { category = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = title,
                    onValueChange = { title = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    value = description,
                    onValueChange = { description = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Estimated price") },
                    value = price,
                    onValueChange = { price = it }
                )

                if (showError) {
                    Text("Please fill in all fields", color = Color.Red, modifier = Modifier.padding(top = 4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                    Text("Bought")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        if (title.isNotBlank() && description.isNotBlank() && price.isNotBlank()) {
                            viewModel.addTodoList(
                                TodoItem(
                                    title = title,
                                    description = description,
                                    createDate = Date().toString(),
                                    priority = TodoPriority.NORMAL,
                                    isDone = isImportant,
                                    category = category,
                                    price = price
                                )
                            )
                            onCancel()
                        } else {
                            showError = true
                        }
                    }) { Text("Add item") }
                }
            }
        }
    }
}
// CategoryDropdown composable
@Composable
fun CategoryDropdown(
    selectedCategory: TodoCategory,
    onSelectionChanged: (TodoCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard(
        modifier = Modifier.clickable { expanded = !expanded }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory.name,
                modifier = Modifier.padding(16.dp)
            )
            Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TodoCategory.values().forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onSelectionChanged(category)
                        expanded = false
                    },
                    text = { Text(category.name) }
                )
            }
        }
    }
}
// TodoCard composable
@Composable
fun TodoCard(todoItem: TodoItem,
             onTodoDelete: (TodoItem) -> Unit,
             onTodoChecked: (TodoItem, checked: Boolean) -> Unit,
             onEditClick: (TodoItem) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ), modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        var expanded by remember { mutableStateOf(false) }
        var todoChecked by remember { mutableStateOf(todoItem.isDone) }
        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = todoItem.category.getIcon()),
                    contentDescription = "Category Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp)

                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = todoItem.title,
                        textDecoration = if (todoChecked) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = todoChecked,
                        onCheckedChange = { isChecked ->
                            todoChecked = isChecked
                            onTodoChecked(todoItem.copy(isDone = isChecked), isChecked)
                        },
                    )
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable {
                            onTodoDelete(todoItem)
                        },
                        tint = Color.Red
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) {
                                "Less"
                            } else {
                                "More"
                            }
                        )
                    }
                    IconButton(onClick = { onEditClick(todoItem) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue
                        )
                    }
                }
            }
            if (expanded) {
                Text(
                    text = todoItem.description,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = todoItem.createDate,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
            }
        }
    }
}