package com.example.afinal.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.example.afinal.database.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {

    var displayStatus = true

    private lateinit var auth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_signup)

        // Sign In Button (Switches to Login Activity from Sign Up Activity)
        signInBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)) // Open Sign Up Page
            overridePendingTransition(R.anim.fadeout, R.anim.fadein) // Animation for switching layouts
        }
        // Register Button
        register_btn.setOnClickListener() {
            sign()
        }
        // signUpPassword validation
        signUpPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!passwordValidation(s)) {
                    passStrength.text = "Poor"
                    passStrength.setTextColor(Color.parseColor("#ec583f"))
                } else {
                    passStrength.text = "Strong"
                    passStrength.setTextColor(Color.parseColor("#39c26d"))
                }
                if (s.isNotEmpty()) {
                    passStrengthText.text = "Password Strength: "
                } else {
                    passStrengthText.text = ""
                    passStrength.text = ""
                }
            }
        })

        // signUpPassword eye click uncover password (Uncovers both signUpPassword and signUpConfirmPassword inputs)
        signUpPassword.setOnTouchListener(View.OnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= signUpPassword.right - signUpPassword.compoundDrawables[drawableRight].bounds.width()
                ) {
                    if (displayStatus) {
                        signUpPassword.transformationMethod = null // Show
                        signUpPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_active, 0);
                        signUpConfirmPassword.transformationMethod = null // Show
                        signUpConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_active, 0);
                        displayStatus = false
                    } else {
                        signUpPassword.transformationMethod = PasswordTransformationMethod() // Hide
                        signUpPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_default, 0);
                        signUpConfirmPassword.transformationMethod = PasswordTransformationMethod() // Hide
                        signUpConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_default, 0);
                        displayStatus = true
                    }
                }
            }
            false
        })

        // signUpConfirmPassword eye click uncover password (Uncovers both signUpPassword and signUpConfirmPassword inputs)
        signUpConfirmPassword.setOnTouchListener(View.OnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= signUpConfirmPassword.right - signUpConfirmPassword.compoundDrawables[drawableRight].bounds.width()
                ) {
                    if (displayStatus) {
                        signUpPassword.transformationMethod = null // Show
                        signUpPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_active, 0);
                        signUpConfirmPassword.transformationMethod = null // Show
                        signUpConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_active, 0);
                        displayStatus = false
                    } else {
                        signUpPassword.transformationMethod = PasswordTransformationMethod() // Hide
                        signUpPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_default, 0);
                        signUpConfirmPassword.transformationMethod = PasswordTransformationMethod() // Hide
                        signUpConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24_default, 0);
                        displayStatus = true
                    }
                }
            }
            false
        })
    }
    // Password Validation function (Currently required - 1 Uppercase letter / 1 number and at least 6 characters long)
    fun passwordValidation(password: CharSequence): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
    // Toast (remake)
    private fun successToast() {
        var myToast: Toast = Toast.makeText(this, "dsa", Toast.LENGTH_SHORT)
        val txtToast : TextView = myToast.view.findViewById(android.R.id.message)
        txtToast.text="Account created successfully"
        txtToast.setTextColor(Color.WHITE)
        txtToast.setBackgroundColor(Color.GRAY)
        txtToast.textSize = 20F
        myToast.setGravity(Gravity.CENTER,0,100 )
        myToast.show()
    }

    private fun saveData() {
        val ref= FirebaseDatabase.getInstance().getReference("Users")

        val data = Data(idcard.text.toString(), firstNameInput.text.toString(), lastNameInput.text.toString(), signupEmail.text.toString(), signUpPassword.text.toString())
        ref.child(auth.currentUser?.uid!!).setValue(data)
    }

    private fun failed() {
        signupEmail.error = "This email is already in use"
        signupEmail.requestFocus()
        return
    }

    private fun sign() {
        if (signupEmail.text.toString().isEmpty()) {
            signupEmail.error = "Required"
            signupEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(signupEmail.text.toString()).matches()) {
            signupEmail.error = "Enter valid email"
            signupEmail.requestFocus()
            return
        }

        if (signUpPassword.text.toString().isEmpty()) {
            signUpPassword.error = "Required"
            signUpPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(
            signupEmail.text.toString(),
            signUpPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    successToast()
                    saveData()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    try {
                        throw task.exception!!
                    } catch (existEmail: FirebaseAuthUserCollisionException) {
                        failed()
                    }
                }
            }
    }
}


