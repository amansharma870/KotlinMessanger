package samuel.griffiths.kotlinmessanger

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_btn_register.setOnClickListener {
            performRegister()
        }

        already_have_account_tv.setOnClickListener {
            Log.d("MainActivity", "Trying to show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Email is: " + email)
        Log.d(TAG, "Password: $password")

        //Firebase Authentication to create a user with email and password
        var auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                //if not successful
                if (!it.isSuccessful) return@addOnCompleteListener
                    //else
                    Log.d(TAG, "createUserWithUid: ${it.result?.user?.uid}")
                    //val user = auth.currentUser
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
