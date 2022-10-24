package com.example.carsharingapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.carsharingapp.databinding.ActivityRegistrationFormBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegistrationForm : AppCompatActivity() {
    private var binding: ActivityRegistrationFormBinding? = null
    private var arrayInputFields:ArrayList<EditText>? = null
    private var arrayErrorFields:ArrayList<TextView>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationFormBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        loadArrays()
        binding?.registrationSignInButton?.setOnClickListener {
            finish()
        }

        binding?.registrationButton?.setOnClickListener {
            var validated = true
            for(i in 0 until arrayInputFields?.size!!){
                validated = checkInputField(arrayInputFields?.get(i)!!,arrayErrorFields?.get(i)!!)
            }
            if(validated){
                val firstname = binding?.registrationFirstNameInput?.text.toString()
                val lastname = binding?.registrationLastNameInput?.text.toString()
                val emailinput = binding?.registrationEmailInput?.text.toString()
                val mobilenumber = binding?.registrationMobilenumberInput?.text.toString()
                val password = binding?.registrationPasswordInput?.text.toString()
                val password2 = binding?.registrationRepeatPasswordInput?.text.toString()
                val username = emailinput.subSequence(0,emailinput.indexOf('@')).toString()

                if(!Patterns.EMAIL_ADDRESS.matcher(emailinput).matches()){
                    validated = false
                    binding?.registrationEmailInput?.setBackgroundResource(R.drawable.inputfield_error_background)
                    binding?.registrationEmailError?.text = "Invalid email adress"
                    binding?.registrationEmailError?.visibility = View.VISIBLE
                }else{
                    binding?.registrationEmailInput?.setBackgroundResource(R.drawable.inputfield_background)
                    binding?.registrationEmailError?.text = "Email address is required"
                    binding?.registrationEmailError?.visibility = View.GONE
                }
                if(password != password2){
                    validated = false
                    binding?.registrationRepeatPasswordInput?.setBackgroundResource(R.drawable.inputfield_error_background)
                    binding?.registrationPasswordRepeatError?.text = "Passwords must match"
                    binding?.registrationPasswordRepeatError?.visibility = View.VISIBLE
                }else{
                    binding?.registrationRepeatPasswordInput?.setBackgroundResource(R.drawable.inputfield_background)
                    binding?.registrationPasswordRepeatError?.text = "Confirm the password"
                    binding?.registrationPasswordRepeatError?.visibility = View.GONE
                }

                if(validated){
                    val mapintent = Intent(this, MapScreen::class.java)
                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    response.createUser(RegistrationUser(emailinput,username,firstname,lastname,mobilenumber,password,password2))
                        .enqueue(object : Callback<RegistrationResponseUser>{
                            override fun onResponse(call: Call<RegistrationResponseUser>, thisresponse: Response<RegistrationResponseUser>) {
                                startActivity(mapintent)
                            }

                            override fun onFailure(call: Call<RegistrationResponseUser>, t: Throwable) {

                            }
                        })
                }
            }



            /*val firstName = binding?.registrationFirstNameInput?.text.toString()
            val lastName = binding?.registrationLastNameInput?.text.toString()
            val email = binding?.registrationEmailInput?.text.toString()
            val mobilePhone = binding?.registrationMobilenumberInput?.text.toString()
            val password = binding?.registrationPasswordInput?.text.toString()
            val password2 = binding?.registrationRepeatPasswordInput?.text.toString()
            val username = email.subSequence(0, email.indexOf('@')).toString()

            var mapintent = Intent(this, MapScreen::class.java)
            val response = ServiceBuilder.buildService(APIInterface::class.java)
            response.createUser(RegistrationUser(email,username,firstName,lastName,mobilePhone,password,password2))
                .enqueue(object : Callback<RegistrationResponseUser>{
                    override fun onResponse(call: Call<RegistrationResponseUser>, thisresponse: Response<RegistrationResponseUser>) {
                        startActivity(mapintent)
                    }

                    override fun onFailure(call: Call<RegistrationResponseUser>, t: Throwable) {

                    }
                    })
            }*/
        }

    }

    private fun checkInputField(inputField:EditText,error:TextView):Boolean{
        return if(inputField.text.isEmpty()){
            inputField.setBackgroundResource(R.drawable.inputfield_error_background)
            error.visibility = View.VISIBLE
            false
        }else{
            inputField.setBackgroundResource(R.drawable.inputfield_background)
            error.visibility = View.GONE
            true
        }
    }


    private fun loadArrays(){
        arrayInputFields = arrayListOf()
        arrayErrorFields = arrayListOf()

        arrayInputFields?.add(binding?.registrationFirstNameInput!!)
        arrayErrorFields?.add(binding?.registrationFirstNameError!!)

        arrayInputFields?.add(binding?.registrationLastNameInput!!)
        arrayErrorFields?.add(binding?.registrationLastNameError!!)

        arrayInputFields?.add(binding?.registrationEmailInput!!)
        arrayErrorFields?.add(binding?.registrationEmailError!!)

        arrayInputFields?.add(binding?.registrationMobilenumberInput!!)
        arrayErrorFields?.add(binding?.registrationMobilenumberError!!)

        arrayInputFields?.add(binding?.registrationPasswordInput!!)
        arrayErrorFields?.add(binding?.registrationPasswordError!!)

        arrayInputFields?.add(binding?.registrationRepeatPasswordInput!!)
        arrayErrorFields?.add(binding?.registrationPasswordRepeatError!!)
    }
}