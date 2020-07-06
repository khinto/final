package com.example.afinal.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_landing.*

class LoginActivity : AppCompatActivity() {

    var displayStatus = true

    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_landing)

        // Login button
        login_btn.setOnClickListener {
            val email = signInEmail.text.toString()
            val password = signInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Input fields are empty!", Toast.LENGTH_SHORT).show()
            }

        }
        // Sign up button
        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java)) // Open Sign Up Page
            overridePendingTransition(R.anim.fadeout, R.anim.fadein) // Animation for switching layouts
        }
        // Show / Hide password
        signInPassword.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= signInPassword.right - signInPassword.compoundDrawables[drawableRight].bounds.width()
                ) {
                    if (displayStatus) {
                        signInPassword.transformationMethod = null // Show
                        signInPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_active, 0);
                        displayStatus = false
                    } else {
                        signInPassword.transformationMethod = PasswordTransformationMethod() // Hide
                        signInPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_default, 0);
                        displayStatus = true
                    }
                }
            }
            false
        })

    }


}