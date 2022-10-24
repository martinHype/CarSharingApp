package com.example.carsharingapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carsharingapp.databinding.ActivityRegistrationFormBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegistrationForm : AppCompatActivity() {
    private var TAG = "registrationForm"
    private var binding: ActivityRegistrationFormBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationFormBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.registrationButton?.setOnClickListener {
            val firstName = binding?.registrationFirstNameInput?.text.toString()
            val lastName = binding?.registrationLastNameInput?.text.toString()
            val email = binding?.registrationEmailInput?.text.toString()
            val mobilePhone = binding?.registrationMobilenumberInput?.text.toString()
            val password = binding?.registrationPasswordInput?.text.toString()
            val password2 = binding?.registrationRepeatPasswordInput?.text.toString()
            val username = email.subSequence(0,email.indexOf('@')).toString()
            val response = ServiceBuilder.buildService(APIInterface::class.java)
            var mapintent = Intent(this,MapScreen::class.java)
            response.createUser(RegistrationUser(email,username,firstName,lastName,mobilePhone,password,password2))
                .enqueue(object : Callback<RegistrationResponseUser>{
                    override fun onResponse(call: Call<RegistrationResponseUser>, thisresponse: Response<RegistrationResponseUser>) {
                        startActivity(mapintent)

                    }

                    override fun onFailure(call: Call<RegistrationResponseUser>, t: Throwable) {
                        binding?.registrationCheckbox?.text = t.message.toString()
                    }
                    })
            }
        }

}