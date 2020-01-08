package com.example.ourchat.ui.findUser

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ourchat.R
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.LastMessageOwner
import com.example.ourchat.data.model.User
import com.example.ourchat.ui.home.MY_PREFS
import com.example.ourchat.ui.home.PROFILE_PIC_URL
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*





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
    textView.text = getTimeAgo(Date(date), textView.context)

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


@BindingAdapter("setNameOfLastMessageOwner")
fun setNameOfLastMessageOwner(textView: TextView, lastMessageOwner: LastMessageOwner) {
    if (lastMessageOwner.ownerUser == null) {
        textView.text = textView.context.getString(R.string.you)
    } else {
        textView.text = lastMessageOwner.ownerUser!!.username
    }

}


@BindingAdapter("setRoundImageFromLastMessageOwner")
fun setRoundImageFromLastMessageOwner(imageView: ImageView, lastMessageOwner: LastMessageOwner) {
    if (lastMessageOwner.ownerUser == null) {
        //this means last message is from logged in user
        val sp = imageView.context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val profile_pic_url = sp.getString(PROFILE_PIC_URL, null)
        Glide.with(imageView.context)
            .load(profile_pic_url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.anonymous_profile)
                    .circleCrop()
            )
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load(lastMessageOwner.ownerUser!!.profile_picture_url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.anonymous_profile)
                    .circleCrop()
            )
            .into(imageView)
    }

}



fun currentDate(): Date {
    val calendar: Calendar = Calendar.getInstance()
    return calendar.time
}

fun getTimeAgo(date: Date?, ctx: Context): String? {
    if (date == null) {
        return null
    }
    val time: Long = date.time
    val curDate: Date = currentDate()
    val now: Long = curDate.time
    if (time > now || time <= 0) {
        return null
    }
    val dim = getTimeDistanceInMinutes(time)
    var timeAgo: String? = null
    timeAgo = if (dim == 0) {
        ctx.resources.getString(R.string.date_util_term_less).toString() + " " + ctx.resources.getString(
            R.string.date_util_term_a
        ) + " " + ctx.resources.getString(R.string.date_util_unit_minute)
    } else if (dim == 1) {
        return "1 " + ctx.resources.getString(R.string.date_util_unit_minute)
    } else if (dim >= 2 && dim <= 44) {
        dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minutes)
    } else if (dim >= 45 && dim <= 89) {
        ctx.resources.getString(R.string.date_util_prefix_about).toString() + " " + ctx.resources.getString(
            R.string.date_util_term_an
        ) + " " + ctx.resources.getString(R.string.date_util_unit_hour)
    } else if (dim >= 90 && dim <= 1439) {
        ctx.resources.getString(R.string.date_util_prefix_about).toString() + " " + Math.round(
            dim / 60.toFloat()
        ) + " " + ctx.resources.getString(R.string.date_util_unit_hours)
    } else if (dim >= 1440 && dim <= 2519) {
        "1 " + ctx.resources.getString(R.string.date_util_unit_day)
    } else if (dim >= 2520 && dim <= 43199) {
        Math.round(dim / 1440.toFloat()).toString() + " " + ctx.resources.getString(
            R.string.date_util_unit_days
        )
    } else if (dim >= 43200 && dim <= 86399) {
        ctx.resources.getString(R.string.date_util_prefix_about).toString() + " " + ctx.resources.getString(
            R.string.date_util_term_a
        ) + " " + ctx.resources.getString(R.string.date_util_unit_month)
    } else if (dim >= 86400 && dim <= 525599) {
        Math.round(dim / 43200.toFloat()).toString() + " " + ctx.resources.getString(
            R.string.date_util_unit_months
        )
    } else if (dim >= 525600 && dim <= 655199) {
        ctx.resources.getString(R.string.date_util_prefix_about).toString() + " " + ctx.resources.getString(
            R.string.date_util_term_a
        ) + " " + ctx.resources.getString(R.string.date_util_unit_year)
    } else if (dim >= 655200 && dim <= 914399) {
        ctx.resources.getString(R.string.date_util_prefix_over).toString() + " " + ctx.resources.getString(
            R.string.date_util_term_a
        ) + " " + ctx.resources.getString(R.string.date_util_unit_year)
    } else if (dim >= 914400 && dim <= 1051199) {
        ctx.resources.getString(R.string.date_util_prefix_almost).toString() + " 2 " + ctx.resources.getString(
            R.string.date_util_unit_years
        )
    } else {
        ctx.resources.getString(R.string.date_util_prefix_about).toString() + " " + Math.round(
            dim / 525600.toFloat()
        ) + " " + ctx.resources.getString(R.string.date_util_unit_years)
    }
    return timeAgo + " " + ctx.resources.getString(R.string.date_util_suffix)
}

private fun getTimeDistanceInMinutes(time: Long): Int {
    val timeDistance: Long = currentDate().time - time
    return Math.round(Math.abs(timeDistance) / 1000 / 60.toFloat())
}
