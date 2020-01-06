package com.example.ourchat.ui.findUser

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ourchat.R
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot


@BindingAdapter("setImage")
fun setImage(imageView: ImageView, item: User) {
    item.let {
        val imageUri = it.profile_picture_url
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.anonymous_profile)
            )
            .into(imageView)
    }

}


@BindingAdapter("setRoundImage")
fun setRoundImage(imageView: ImageView, item: User) {
    item.let {
        val imageUri = it.profile_picture_url
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.anonymous_profile)
                    .circleCrop()
            )
            .into(imageView)
    }

}

@BindingAdapter("formatDate")
fun formatDate(textView: TextView, date: Long) {
    textView.text = "todo date$date"
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


@BindingAdapter("setLoadingState")
fun MaterialButton.setTheLoadingState(state: LoadState) {
    when (state) {
        LoadState.SUCCESS -> {
            setIconResource(R.drawable.ic_person_add_black_24dp)
        }
        LoadState.LOADING -> {
            setIconResource(R.drawable.loading_animation)
        }


    }

}

/*
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
