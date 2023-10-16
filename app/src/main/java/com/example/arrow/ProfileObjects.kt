package com.example.arrow

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object ProfileObjects {
    var name: String? = null
    var role: String? = null
    var email: String? = null
    var profile: String? = null
    var profileOverlay: ConstraintLayout? = null
    var profileImage: Drawable? = null
}

fun setRawImage(resources: Resources, rawImageId: Int): BitmapDrawable {
    val rawImage = resources.openRawResource(rawImageId)
    val bitmap = BitmapFactory.decodeStream(rawImage)
    return BitmapDrawable(resources, bitmap)
}
class ViewModel : ViewModel() {
    // For Changing Avatar
    private val _imageDrawable = MutableLiveData<Drawable>()
    val imageDrawable: LiveData<Drawable> get() = _imageDrawable

    fun setImageDrawable(resourceDrawable: Drawable) {
        _imageDrawable.value = resourceDrawable
    }

    // For search bar clear Focus
    private val _hasFocus = MutableLiveData<Boolean>()
    val hasFocus: LiveData<Boolean> get() = _hasFocus

    fun clearFocus(focus: Boolean){
        _hasFocus.value = focus
    }
}