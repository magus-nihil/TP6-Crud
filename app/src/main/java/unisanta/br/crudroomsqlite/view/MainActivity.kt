package unisanta.br.crudroomsqlite.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unisanta.br.crudroomsqlite.dao.UserDao
import unisanta.br.crudroomsqlite.database.AppDatabase
import unisanta.br.crudroomsqlite.databinding.ActivityMainBinding
import unisanta.br.crudroomsqlite.model.User

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database-unisanta"
        ).fallbackToDestructiveMigration()
            .build()
        userDao = db.userDao()

        binding.btnSalvar.setOnClickListener {
            val firstName = binding.edtFname.text.toString()
            val lastName = binding.edtLname.text.toString()
            val email = binding.edtEmail.text.toString()
            val photoUrl = binding.edtPhotoUrl.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                val user = User(0, firstName, lastName, email, photoUrl)
                lifecycleScope.launch {
                    userDao.insertUser(user)
                    Log.i("TAG", "Usuário inserido: $firstName $lastName")
                }
            } else {
                Log.e("TAG", "Todos os campos devem ser preenchidos")
            }
        }

        binding.btnListar.setOnClickListener {
            lifecycleScope.launch {
                val list = withContext(Dispatchers.IO) {
                    userDao.getAll()
                }
                list.forEach {
                    Log.d("USUÁRIO", "${it.uid} - ${it.firstName} - ${it.lastName} - ${it.email}")
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userDao.getUserByEmail(email)
                    if (user != null) {
                        currentUser = user
                        binding.edtFname.setText(user.firstName)
                        binding.edtLname.setText(user.lastName)
                        binding.edtPhotoUrl.setText(user.photoUrl)
                        Log.i("Login", "Usuário carregado: ${user.firstName}")
                    } else {
                        Log.e("Login", "Usuário não encontrado")
                    }
                }
            } else {
                Log.e("Login", "Email não pode estar vazio")
            }
        }

        binding.btnAtualizar.setOnClickListener {
            val firstName = binding.edtFname.text.toString()
            val lastName = binding.edtLname.text.toString()
            val email = binding.edtEmail.text.toString()
            val photoUrl = binding.edtPhotoUrl.text.toString()

            if (currentUser != null && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                val updatedUser = currentUser!!.copy(firstName = firstName, lastName = lastName, email = email, photoUrl = photoUrl)
                lifecycleScope.launch {
                    userDao.updateUser(updatedUser)
                    Log.i("TAG", "Usuário atualizado: $firstName $lastName")
                }
            } else {
                Log.e("TAG", "Erro ao atualizar o usuário")
            }
        }

        binding.btnDeletar.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userDao.getUserByEmail(email)
                    user?.let {
                        userDao.deleteUser(it)
                        Log.i("TAG", "Usuário deletado: $email")
                    } ?: Log.e("TAG", "Usuário não encontrado")
                }
            } else {
                Log.e("TAG", "Email não pode estar vazio")
            }
        }
    }
}