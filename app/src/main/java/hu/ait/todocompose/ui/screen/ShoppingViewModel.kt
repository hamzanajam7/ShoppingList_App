package hu.ait.todocompose.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.todocompose.data.TodoDAO
import hu.ait.todocompose.data.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    val todoDAO: TodoDAO
) : ViewModel() {


    fun getAllToDoList(): Flow<List<TodoItem>> {
        return todoDAO.getAllTodos()
    }

    fun addTodoList(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDAO.insert(todoItem)
        }
    }

    fun removeTodoItem(todoItem: TodoItem) {
        //
        viewModelScope.launch(Dispatchers.IO) {
            todoDAO.delete(todoItem)
        }
    }

    fun editTodoItem(originalTodo: TodoItem, editedTodo: TodoItem) {
        //
        viewModelScope.launch(Dispatchers.IO) {
            todoDAO.update(editedTodo)
        }
    }

    fun changeTodoState(todoItem: TodoItem, value: Boolean) {
        //
        val updatedTodo = todoItem.copy(isDone = value)
        editTodoItem(todoItem, updatedTodo)
    }

    fun clearAllTodos() {
        //
        viewModelScope.launch(Dispatchers.IO) {
            todoDAO.deleteAllTodos() // Call the deleteAll method to remove all todos
        }
    }
}

