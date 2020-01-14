package com.example.ourchat.ui.main


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.ourchat.R
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.Utils.eventbus_events.CallbackManagerEvent
import com.example.ourchat.Utils.eventbus_events.ConnectionChangeEvent
import com.example.ourchat.Utils.eventbus_events.KeyboardEvent
import com.example.ourchat.Utils.eventbus_events.SelectGalleryImageEvent
import com.example.ourchat.databinding.ActivityMainBinding
import com.example.ourchat.ui.chat.SELECT_CHAT_IMAGE_REQUEST
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.example.ourchat.ui.profile.SELECT_PROFILE_IMAGE_REQUEST
import com.facebook.CallbackManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.issue_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {


    val REQUEST_IMAGE_CAPTURE = 1
    var isActivityRecreated = false
    lateinit var callbackManager: CallbackManager
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //register to event bus to receive callbacks
        EventBus.getDefault().register(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        //change title text color of toolbar to white
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.white))


        //hide toolbar on signup,login fragments
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label == "SignupFragment" || destination.label == "LoginFragment") {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }


        //setup toolbar with navigation
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.loginFragment))
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)


        //handle any change in loading state in whole app
        binding.issueLayout.cancelImage.setOnClickListener {
            binding.issueLayout.visibility = View.GONE
        }

        sharedViewModel.loadStateMutableLiveData.observe(this, Observer {

            when (it) {
                LoadState.LOADING -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.SUCCESS -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.FAILURE -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.VISIBLE
                    binding.issueLayout.textViewIssue.text = ErrorMessage.errorMessage
                }

            }
        })


    }


    // Show snackbar whenever the connection state changes
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onConnectionChangeEvent(event: ConnectionChangeEvent): Unit {
        if (!isActivityRecreated) {//to not show toast on configuration changes
            Snackbar.make(binding.coordinator, event.message, Snackbar.LENGTH_LONG).show()
        }
    }


    //facebook fragment will pass callbackManager to activity to continue FB login
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CallbackManagerEvent) {
        callbackManager = event.callbackManager
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectGalleryImageEvent(event: SelectGalleryImageEvent) {
        selectFromGallery(event.REQUEST_CODE)
    }


    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    fun selectFromGallery(requestCode: Int) {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //update live data so profile fragment will show result image
            sharedViewModel.imageBitmap.postValue(imageBitmap)
        }
        if (requestCode == SELECT_PROFILE_IMAGE_REQUEST && data != null && resultCode == RESULT_OK) {
            //update live data so profile fragment will show result image
            sharedViewModel.galleryImageUri.postValue(data.data)
            //upload image to firebase storage
            sharedViewModel.uploadProfileImageByUri(data.data)
        }


        if (requestCode == SELECT_CHAT_IMAGE_REQUEST && data != null && resultCode == RESULT_OK) {

            //upload image to firebase storage
            sharedViewModel.uploadChatImageByUri(data.data)
        }




        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: KeyboardEvent) {
        println("MainActivity.onMessageEvent:")
        hideKeyboard()
    }


    fun hideKeyboard() {


        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.toolbar.windowToken, 0)

    }

}



