package com.example.ourchat.ui.findUser

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ourchat.R
import com.google.firebase.firestore.DocumentSnapshot


/*
@BindingAdapter("setImage")
fun setImage(imageView: ImageView, imgString: String?) {
    imgString?.let {
        val imageUri = imgString.toUri()
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imageView)
    }
}
*/



@BindingAdapter("setRoundImage")
fun setRoundImage(imageView: ImageView, item: DocumentSnapshot) {
    item.let {
        val imageUri = it.get("profile_picture_url")
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .circleCrop()
            )
            .into(imageView)
    }

}


/*
@BindingAdapter(requireAll = false,value = ["mySetText","queryString"])
fun mySetText(textView: TextView,user: DocumentSnapshot,queryString:String) {
    textView.text = user.get("username").toString()

    if (!queryString.isEmpty()){
    var str = user.get("username").toString()
    var delimiter = queryString
    val parts = str.split(delimiter)
    println("<top>.mySetText:$parts")
    val text ="<font color=#cc0029>${parts[0]}</font> <font color=#ffcc00>$delimiter</font> <font color=#cc0029>${parts[2]}</font>"
    textView.text = Html.fromHtml(text)
    }*/


@BindingAdapter("mySetText")
fun mySetText(textView: TextView, user: DocumentSnapshot) {
    textView.text = user.get("username").toString()

}
/*

@BindingAdapter("setLoadingState")
fun ImageView.setTheLoadingState(state: HomeViewModel.State) {
    visibility = when (state) {
        HomeViewModel.State.LOADING -> {
            setImageResource(R.drawable.loading_animation)
            View.VISIBLE
        }
        HomeViewModel.State.FAILED -> {
            setImageResource(R.drawable.ic_connection_error)
            View.VISIBLE
        }
        HomeViewModel.State.SUCCESS -> {
            View.GONE
        }
    }

}


@BindingAdapter("setVisibility")
fun TextView.setVisibility(favouriteListSize: Int): Unit {
    visibility = if (favouriteListSize == 0) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


@BindingAdapter("setVisibility")
fun ConstraintLayout.setVisibility(state: HomeViewModel.State) {
    visibility = when (state) {
        HomeViewModel.State.LOADING -> {
            View.GONE
        }
        HomeViewModel.State.FAILED -> {
            View.GONE
        }
        HomeViewModel.State.SUCCESS -> {
            View.VISIBLE
        }
    }
}
*/
