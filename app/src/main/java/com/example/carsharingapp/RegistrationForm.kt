package com.example.carsharingapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.carsharingapp.databinding.ActivityRegistrationFormBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegistrationForm : AppCompatActivity() {

    private var binding: ActivityRegistrationFormBinding? = null
    private var inputArray:Array<EditText>? = null
    private var errorArray:Array<TextView>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationFormBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        fillArrays()


        binding?.registrationButton?.setOnClickListener {
            var notFoundErrors = true
            for(i in 0..(inputArray?.size!!-1)){
                notFoundErrors = controlEmptyField(inputArray?.get(i)!!,errorArray?.get(i)!!)
            }
            if(!binding?.registrationPasswordInput?.text!!.isEmpty() && !binding?.registrationRepeatPasswordInput?.text!!.isEmpty()){
                if(binding?.registrationPasswordInput?.text!!.toString() != binding?.registrationRepeatPasswordInput?.text.toString()){
                    binding?.registrationRepeatPasswordInput?.setBackgroundResource(R.drawable.inputfield_error_background)
                    binding?.registrationRepeatPasswordError?.text = "Your password and confirmation password must match"
                    binding?.registrationRepeatPasswordError?.visibility = View.VISIBLE
                }else{
                    binding?.registrationRepeatPasswordInput?.setBackgroundResource(R.drawable.inputfield_background)
                    binding?.registrationRepeatPasswordError?.visibility = View.GONE
                }
            }
            if(notFoundErrors){
                var firstName = binding?.registrationFirstNameInput?.text.toString()
                var lastName = binding?.registrationLastNameInput?.text.toString()
                var email = binding?.registrationEmailInput?.text.toString()
                var password = binding?.registrationPasswordInput?.text.toString()
                var username = email.subSequence(0,email.indexOf('@')).toString()

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.createUser(User(username,firstName,lastName,email,password,true))
                    .enqueue(object : Callback<User>{
                        override fun onResponse(call: Call<User>, thisresponse: Response<User>) {

                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Log.d("respone",t.message.toString())
                        }

                    })

            }
            }

    }

    private fun controlEmptyField(editText:EditText,textView:TextView):Boolean{
        if(editText.text!!.isEmpty()){
            editText.setBackgroundResource(R.drawable.inputfield_error_background)
            textView.visibility = View.VISIBLE
            return false
        }else{
            editText.setBackgroundResource(R.drawable.inputfield_background)
            textView.visibility = View.GONE
            return true
        }

    }

    private fun fillArrays(){
        inputArray = arrayOf(
            binding?.registrationFirstNameInput!!,
            binding?.registrationLastNameInput!!,
            binding?.registrationEmailInput!!,
            binding?.registrationMobilenumberInput!!,
            binding?.registrationPasswordInput!!,
            binding?.registrationRepeatPasswordInput!!
        )
        errorArray = arrayOf(
            binding?.registrationFirstNameError!!,
            binding?.registrationLastNameError!!,
            binding?.registrationEmailError!!,
            binding?.registrationMobilenumberError!!,
            binding?.registrationPasswordError!!,
            binding?.registrationRepeatPasswordError!!
        )
    }
}