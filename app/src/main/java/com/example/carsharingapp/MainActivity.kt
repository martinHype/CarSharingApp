package com.example.carsharingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.carsharingapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    //val BASE_URL:String = "http://192.168.100.5:8000/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.mainScreenSignUpButton?.setOnClickListener{
            val intent = Intent(this,RegistrationForm::class.java)
            startActivity(intent)
        }


        binding?.mainscreenSignInButton?.setOnClickListener {
            var validated = true
            val email = binding?.mainscreenEmailInput?.text.toString()
            val password = binding?.mainscreenPasswordInput?.text.toString()
            val intent = Intent(this,MapScreen::class.java)
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
                            if(loginResponse?.token != null){
                                startActivity(intent)
                            }else{
                                binding?.mainscreenPasswordInput?.text?.clear()
                                binding?.mainscreenPasswordError?.text = "Unable to log in with provided credentials"
                                binding?.mainscreenPasswordError?.visibility = View.VISIBLE


                            }
                        }

                        override fun onFailure(call: Call<LoginInResponseUser>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
            }

        }


    }

}