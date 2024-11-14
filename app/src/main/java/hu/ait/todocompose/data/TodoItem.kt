package hu.ait.todocompose.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.ait.todocompose.R
import java.io.Serializable
import androidx.annotation.DrawableRes

@Entity(tableName = "todotable")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title:String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "description") val description:String,
    @ColumnInfo(name = "createDate") val createDate:String,
    @ColumnInfo(name = "priority") var priority:TodoPriority,
    @ColumnInfo(name = "isDone") var isDone: Boolean,
    @ColumnInfo(name = "category") var category: TodoCategory
) : Serializable

enum class TodoCategory {
    FOOD, SUPPLIES, BOOK;

    @DrawableRes
    fun getIcon(): Int {
        return when (this) {
            FOOD -> R.drawable.ic_food
            SUPPLIES -> R.drawable.house_supplies
            BOOK -> R.drawable.ic_book
        }
    }
}

enum class TodoPriority {
    NORMAL, HIGH;

    fun getIcon(): Int {
        return if (this == NORMAL) R.drawable.normal else R.drawable.important
    }
}
