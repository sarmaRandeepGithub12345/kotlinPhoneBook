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
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddCandidateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidate)
        var etfirstName = findViewById<TextInputEditText>(R.id.etFirstname)
        var etlastName = findViewById<TextInputEditText>(R.id.etLastname)
        var etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        var etRoll = findViewById<TextInputEditText>(R.id.etRoll)
        var etSave = findViewById<Button>(R.id.btnSave)
        etSave.setOnClickListener {
            apiFunction(this,etfirstName,etlastName,etPhone,etRoll)
        }
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
    private fun apiFunction(context: Context,firstname:TextInputEditText,lastname:TextInputEditText,phone:TextInputEditText,roll: TextInputEditText){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://5ea6-223-238-123-118.ngrok.io/").addConverterFactory(
                GsonConverterFactory.create()).build()
        val apiService = retrofit.create(ApiInterface::class.java)
        val editor = getSharedPreferences("MY_SETTINGS", MODE_PRIVATE)
        val userDetails = Gson().fromJson(editor.getString("UserDetails",null),ResponseModel::class.java)

        //using apiService to send post request
        val dataInitial = Candidate(firstname.text.toString(),lastname.text.toString(),phone.text.toString(),roll.text.toString())
        val call = apiService.addCandidate(dataInitial,"Bearer ${userDetails.token}")

        call.enqueue(object: Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseData = response.body()
                val receivedStatus = responseData?.data.toString()
                if (response.isSuccessful) {

                    if(receivedStatus=="Success"){

                        firstname.setText("")
                        lastname.setText("")
                        phone.setText("")
                        roll.setText("")


                        showErrorFunction("Candidate Added Successfully")
                        //save to sharedPref

                        var countDownTimer = object : CountDownTimer(4000,4000) {
                            override fun onTick(millisUntilFinished: Long){

                            }
                            override fun onFinish() {

                                intent = Intent(context,SecondActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        countDownTimer.start()

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