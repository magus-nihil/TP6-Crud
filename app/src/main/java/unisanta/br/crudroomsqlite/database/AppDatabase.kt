package unisanta.br.crudroomsqlite.database

import androidx.room.Database
import androidx.room.RoomDatabase
import unisanta.br.crudroomsqlite.dao.UserDao
import unisanta.br.crudroomsqlite.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}