package com.example.carsharingapp

import android.app.ActivityOptions
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.carsharingapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.mainlayout?.visibility = View.INVISIBLE

        binding?.mainScreenSignUpButton?.setOnClickListener{
            val intent = Intent(this,RegistrationForm::class.java)
            startActivity(intent)
        }


        binding?.mainscreenSignInButton?.setOnClickListener {
            /*var intent = Intent(this,MapScreen::class.java)
            startActivity(intent)*/
            var validated = true
            val email = binding?.mainscreenEmailInput?.text.toString()
            val password = binding?.mainscreenPasswordInput?.text.toString()
            intent = Intent(this,MapScreen::class.java)
            if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding?.mainscreenEmailInput?.setBackgroundResource(R.drawable.inputfield_error_background)
                binding?.mainscreenEmailError?.text = if(email.isEmpty()) "Email address is required" else "Invalid email adress"
                binding?.mainscreenEmailError?.visibility = View.VISIBLE
                validated = false
            }else{
                binding?.mainscreenEmailInput?.setBackgroundResource(R.drawable.inputfield_background)
                binding?.mainscreenEmailError?.visibility = View.INVISIBLE
            }
            if(password.isEmpty()){
                binding?.mainscreenPasswordInput?.setBackgroundResource(R.drawable.inputfield_error_background)
                binding?.mainscreenPasswordError?.text = "Password is required"
                binding?.mainscreenPasswordError?.visibility = View.VISIBLE
                validated = false
            }else{
                binding?.mainscreenPasswordInput?.setBackgroundResource(R.drawable.inputfield_background)
                binding?.mainscreenPasswordError?.visibility = View.INVISIBLE
            }
            if(validated){
                val user = LoginInUser(email,password)
                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.loginUser(user)
                    .enqueue(object : Callback<LoginInResponseUser> {
                        override fun onResponse(call: Call<LoginInResponseUser>, response: Response<LoginInResponseUser>) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                if(loginResponse.response){
                                    val user = User(
                                        loginResponse.email,
                                        loginResponse.first_name,
                                        loginResponse.last_name,
                                        loginResponse.phone_number,
                                        loginResponse.token,
                                        1
                                    )
                                    val response = UsersDb.loginUser(applicationContext,user)
                                    if(response != -1) {
                                        ServiceBuilder.currentUser = user
                                        startActivity(intent)

                                    }
                                }else{
                                    binding?.mainscreenPasswordInput?.text?.clear()
                                    binding?.mainscreenPasswordError?.text = "Unable to log in with provided credentials"
                                    binding?.mainscreenPasswordError?.visibility = View.VISIBLE


                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginInResponseUser>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
            }
            binding?.mainscreenEmailInput?.text?.clear()
            binding?.mainscreenPasswordInput?.text?.clear()

        }


    }

    override fun onStart() {
        var user = UsersDb.getActiveUser(applicationContext)
        if(user != null){
            ServiceBuilder.currentUser = user
            var intent = Intent(this,MapScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }else{
            binding?.mainlayout?.visibility = View.VISIBLE
            binding?.loadingLayout?.visibility = View.GONE
        }
        super.onStart()



    }

}