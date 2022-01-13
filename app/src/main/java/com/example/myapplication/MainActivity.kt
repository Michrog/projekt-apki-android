package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()
    val FORM = "application/x-www-form-urlencoded".toMediaTypeOrNull()

    fun httpPost(url: String, body: RequestBody, success: (response: Response) -> Unit, failure: () -> Unit) {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failure()
            }

            override fun onResponse(call: Call, response: Response) {
                success(response)
            }
        })
    }

    fun login(login: String, password: String){
        //Toast.makeText(this, "login: "+login+" haslo: "+password, Toast.LENGTH_SHORT).show()
        val url = "https://young-ocean-20644.herokuapp.com/login"
        val body = ("session[user_index]=" + login + "&session[password]=" +
                password).toRequestBody(FORM)
        httpPost(url, body,
            fun (response: Response){
                val response_string = response.body?.string()
                val json = JSONObject(response_string)
                if(json.has("message")){
                    this.runOnUiThread{
                        Toast.makeText(this, json["message"] as String, Toast.LENGTH_LONG).show()
                    }
                }
                else if(json.has("token")){
                    this.runOnUiThread{
                        Toast.makeText(this, json["token"] as String + "\nZalogowano", Toast.LENGTH_LONG).show()
                    }
                }
                Log.v("INFO", response_string.toString())
            },
            fun (){
                Log.v("Info", "Failed")
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(login_field.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        login_button.setOnClickListener{
            val login = login_field.text.toString()
            val password = password_field.text.toString()
            login(login, password)
        }
    }
}