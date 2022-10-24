package com.example.carsharingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carsharingapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    val BASE_URL:String = "http://192.168.100.5:8000/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.klik?.setOnClickListener {
            var intent = Intent(this,RegistrationForm::class.java)
            startActivity(intent)
        }

    }

    /*private fun getMyUsers() {
        val retrofiBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(APIInterface::class.java)

        val retrofitData = retrofiBuilder.getData()
        retrofitData.enqueue(object : Callback<List<UserItem>?> {
            override fun onResponse(call: Call<List<UserItem>?>, response: Response<List<UserItem>?>) {
                val responseBody = response.body()!!
                val stringBuilder = StringBuilder()
                for(data in responseBody){
                    stringBuilder.append(data.getString() + "\n")
                }
                binding?.textoutput?.text = stringBuilder
            }

            override fun onFailure(call: Call<List<UserItem>?>, t: Throwable) {
                binding?.textoutput?.text = t.message
            }
        })
    }*/
}