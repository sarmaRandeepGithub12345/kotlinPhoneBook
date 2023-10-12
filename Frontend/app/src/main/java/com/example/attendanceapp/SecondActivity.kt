package com.example.attendanceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SecondActivity : AppCompatActivity() {
    lateinit var recyclerView :RecyclerView
    lateinit var layoutList:MutableList<Result>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        recyclerView = findViewById(R.id.recyclerView)

        val editor = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE)
        val userDetails = Gson().fromJson(editor.getString("UserDetails",null),ResponseModel::class.java)
        if(userDetails==null){
            startActivity(Intent(this,MainActivity::class.java))
        }
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            startActivity(Intent(this,AddCandidateActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val editor1 = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE).edit()
            editor1.putString("UserDetails","")
            editor1.apply()
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        layoutList = mutableListOf()
        Log.d("Hi","Hi")
        apiFunction(this)



    }

    private fun showErrorFunction(message:String){
        var etError = findViewById<TextView>(R.id.etErrorCandidate)
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
    private fun apiFunction(mainActivity: SecondActivity){
       // https://5ea6-223-238-123-118.ngrok.io
        val retrofit = Retrofit.Builder()
            .baseUrl("https://5ea6-223-238-123-118.ngrok.io/").addConverterFactory(
                GsonConverterFactory.create()).build()
        val apiService = retrofit.create(ApiInterface::class.java)
        val editor = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE)
        val userDetails = Gson().fromJson(editor.getString("UserDetails",null),ResponseModel::class.java)

        //using apiService to send post request

        val call = apiService.getCandidate("Bearer ${userDetails.token}")

        call.enqueue(object: Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseData = response.body()
                val receivedStatus = responseData?.data.toString()

                if (response.isSuccessful) {

                    if(receivedStatus=="Success"){

                        layoutList = responseData?.result!!
                        recyclerView.layoutManager = LinearLayoutManager(mainActivity)

                        recyclerView.adapter = CandidateAdapter(mainActivity,layoutList)

                    }
                }
                else if(response.code()==400) {

                    val errorBody = response.errorBody()?.string()
                    val message = Gson().fromJson(errorBody,ResponseModel::class.java)
                    showErrorFunction(message.data.toString())
                }

            }
            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Log.d("Error1",t.toString())
                showErrorFunction(t.toString())
            }
        })
    }

}