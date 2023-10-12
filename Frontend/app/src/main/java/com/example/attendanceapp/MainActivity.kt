package com.example.attendanceapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        var etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        var etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        var etRegister = findViewById<Button>(R.id.btnRegister)
        //retreiving data from sharedPref
        val editor = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE)
        val userDetails = Gson().fromJson(editor.getString("UserDetails",null),ResponseModel::class.java)
        if(userDetails!=null){
            startActivity(Intent(this,SecondActivity::class.java))
        }

        var etLogin = findViewById<Button>(R.id.btnLogin)
        etRegister.setOnClickListener {
            handleRegister(this,etEmail.text.toString(),etPassword.text.toString())
        }
        etLogin.setOnClickListener {
            handleLogin(this,etEmail.text.toString(),etPassword.text.toString())
        }
    }

    private fun handleLogin(context:Context,email: String, password: String) {
        //Log.d("email",password)
        //creating a retrofit instance and setting the base url
        apiFunction(context,email,password,1)
    }
    private fun handleRegister(context:Context, email: String, password: String) {
        apiFunction(context,email,password,2)
    }
    private fun showErrorFunction(message:String){
        var etError = findViewById<TextView>(R.id.etError)
        var countDownTimer = object : CountDownTimer(3000,3000) {
            override fun onTick(millisUntilFinished: Long){
                etError.text = message
            }
            override fun onFinish() {
                etError.text = ""
            }
        }
        countDownTimer.start()
    }
    private fun apiFunction(context:Context,email:String,password:String,num:Int){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://5ea6-223-238-123-118.ngrok.io/").addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofit.create(ApiInterface::class.java)

        //using apiService to send post request
        val data = LoginDetails(email,password)
        val call = when(num){
            1-> {
                apiService.userLogin(data)
            }
            else -> {
                apiService.userRegister(data)
            }
        }
        call.enqueue(object: Callback<ResponseModel?>{
            override fun onResponse(call: Call<ResponseModel?>, response: Response<ResponseModel?>) {
                val responseData = response.body()
                val receivedStatus = responseData?.data.toString()
                if (response.isSuccessful) {
                    val token = responseData?.token.toString()
                    val receivedEmail= responseData?.email.toString()

                    if(receivedStatus=="Success"){

                        showErrorFunction(receivedStatus)
                        //save to sharedPref
                        val editor = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE).edit()
                        val user = ResponseModel(receivedStatus,receivedEmail,token)
                        val gson = Gson()
                        val dataFinal = gson.toJson(user,ResponseModel::class.java)
                        //  Log.d("User", user.toString())
                        editor.putString("UserDetails",dataFinal )
                        editor.apply()
                        findViewById<TextInputEditText>(R.id.etEmail).text?.clear()
                        findViewById<TextInputEditText>(R.id.etPassword).text?.clear()
                        var countDownTimer = object : CountDownTimer(4000,4000) {
                            override fun onTick(millisUntilFinished: Long){
                            }
                            override fun onFinish() {

                                intent = Intent(context,SecondActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        countDownTimer.start()

                    }else if(receivedStatus=="Registration Success"){
                        Log.d("Register",receivedStatus)
                        val etEmail =findViewById<TextInputEditText>(R.id.etEmail)
                        findViewById<TextInputEditText>(R.id.etEmail).text?.clear()
                        findViewById<TextInputEditText>(R.id.etPassword).text?.clear()
                        showErrorFunction(receivedStatus)
                    }
                }
                else if(response.code()==400) {

                    val errorBody = response.errorBody()?.string()
                    val message = Gson().fromJson(errorBody,ResponseModel::class.java)


                    showErrorFunction(message.data.toString())
                }

            }
            override fun onFailure(call: Call<ResponseModel?>, t: Throwable) {
                Log.d("Error1",t.toString())
                showErrorFunction(t.toString())
            }
        })
    }
}