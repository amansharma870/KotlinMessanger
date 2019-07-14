package samuel.griffiths.kotlinmessanger

import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_btn_login.setOnClickListener {
            performLogin()
        }

        back_to_registration_tv_login.setOnClickListener {
            finish()
        }
    }

    private fun performLogin(){
        val email = email_tv_login.text.toString()
        val password = password_tv_login.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter an email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempt to login with Email: ${email} and Password: ${password}")

        // Firebase authentication to login using email and password credentials.
        var auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                //if not successful
                if (!it.isSuccessful) return@addOnCompleteListener

                //else
                Log.d(TAG, "Successfully logged in user with UID: ${it.result?.user?.uid}")
            }
            .addOnFailureListener{
                Log.d(TAG, "Login authentication unsuccessful: ${it.message}")
                Toast.makeText(this, "Login authentication unsuccessful: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}