package com.example.ourchat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callbackManager = CallbackManager.Factory.create()


        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        println("MainActivity.onCreate:accestoken:${accessToken.token}&&&is loged in:$isLoggedIn")



        //perform login


       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
