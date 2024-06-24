package com.hoangnh.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Base64OutputStream
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private val gson = Gson()

fun Number.dpToPx(): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()
}

fun Number.pxToDp(): Float {
    val displayMetrics = Resources.getSystem().displayMetrics
    return this.toFloat() / (displayMetrics.densityDpi / 160f)
}

fun TextView.textSize(@DimenRes dimens: Int) {
    this.setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        resources.getDimension(dimens)
    )
}

fun EditText.setMaxLength(length: Int) {
    val filterArray: Array<InputFilter?> = arrayOfNulls(1)
    filterArray[0] = InputFilter.LengthFilter(length)
    this.filters = filterArray
}


fun Int.asColor(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        Resources.getSystem().getColor(this, null)
    else Resources.getSystem().getColor(this)
}

fun String.asHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    else Html.fromHtml(this)
}

fun CharSequence.setPhone(phoneNumber: String, @ColorRes color: Int): CharSequence {
    val spannable = SpannableString(this)
    if (this.isNotEmpty() && phoneNumber.isNotEmpty()) {
        val positionStart =
            if (this.indexOf(phoneNumber.substring(0)) > 0) this.indexOf(phoneNumber.substring(0)) else 0
        val positionEnd = positionStart + (phoneNumber.length)
        spannable.setSpan(
            ForegroundColorSpan(Resources.getSystem().getColor(color)),
            positionStart, positionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            UnderlineSpan(),
            positionStart, positionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                //what happens whe i click
                val uri = Uri.parse("tel:$phoneNumber")
                val i = Intent(Intent.ACTION_DIAL, uri)
                view.context.startActivity(i)
            }
        }
        spannable.setSpan(
            clickableSpan,
            positionStart, positionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun CharSequence.setUnderLine(textUnderLine: String, @ColorRes color: Int): CharSequence {
    val spannable = SpannableString(this)
    if (this.isNotEmpty()) {
        val positionStart = this.indexOf(textUnderLine.substring(0))
        val positionEnd = this.indexOf(textUnderLine.substring(0)) + (textUnderLine.length)
        spannable.setSpan(
            ForegroundColorSpan(Resources.getSystem().getColor(color)),
            positionStart, positionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            UnderlineSpan(),
            positionStart, positionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun String.asDateTime(
    formatFrom: String = "yyyy-MM-dd'T'HH:mm:ss",
    formatTo: String = "dd/MM/yyyy・HH:mm:ss",
    locate: Locale = Locale("vi", "VN"),
): String? {
    return try {
        SimpleDateFormat(formatFrom, locate).parse(this)
            ?.let { SimpleDateFormat(formatTo, locate).format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.asDateToTimestamp(
    formatFrom: String = "yyyy-MM-dd'T'HH:mm:ss",
    locate: Locale = Locale("vi", "VN"),
): Long {
    return try {
        SimpleDateFormat(formatFrom, locate).parse(this)?.time?.let {
            it + (TimeUnit.MILLISECONDS.toHours(TimeZone.getDefault().rawOffset.toLong()) * 3600000)
        } ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun Long.asMoney(): String {
    val numberFormat = NumberFormat.getIntegerInstance(Locale("vi"))
    val formatted = numberFormat.format(this)
    return "${formatted}đ"
}

fun Double.asMoney(): String {
    val numberFormat = NumberFormat.getIntegerInstance(Locale("vi"))
    val formatted = numberFormat.format(this)
    return "${formatted}đ"
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visble() {
    this.visibility = View.VISIBLE
}

fun View.invisble() {
    this.visibility = View.INVISIBLE
}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content)?.windowToken, 0)
}

fun Fragment.hideKeyboard(): Boolean {
    context?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(
            this.view?.findViewById<View>(android.R.id.content)?.windowToken,
            0
        )
    }

    return true
}

fun Fragment.hideKeyboardForce(): Boolean {
    context?.let {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    return true
}

fun AppCompatActivity.getDateTimeCurrent(formatTo: String = "dd/MM/yyyy・HH:mm:ss"): String {
    val calendar = Calendar.getInstance()
    val format = SimpleDateFormat(formatTo, Locale("vi", "VN"))
    return format.format(calendar.time)
}

fun Fragment.getDateTimeCurrent(formatTo: String = "dd/MM/yyyy・HH:mm:ss"): String {
    val calendar = Calendar.getInstance()
    val format = SimpleDateFormat(formatTo, Locale("vi", "VN"))
    return format.format(calendar.time)
}


fun Activity.setStatusBarGradiant(@DrawableRes drawableRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = this.window
        val background = this.resources.getDrawable(drawableRes)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.resources.getColor(android.R.color.transparent)
        window.navigationBarColor = this.resources.getColor(android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }
}

fun Activity.setColorStatusBar(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.resources.getColor(color)
        window.navigationBarColor = this.resources.getColor(color)
    }
}

fun Activity.getScreenWidth(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.x
}

fun Activity.getScreenHeight(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.y
}

fun Activity.getAppScreenWidth(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.x
}

fun Activity.getAppScreenHeight(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.y
}

fun Any.toJson(): String {
    return gson.toJson(this)
}

fun <T> String.fromJson(objects: Class<T>): T {
    return gson.fromJson(this, objects)
}

fun <T> String.fromJsonList(objects: Class<T>): MutableList<T> {
    return gson.fromJson(
        this,
        TypeToken.getParameterized(MutableList::class.java, objects).type
    )
}

fun Any.toHashMap(): HashMap<String, Any> {
    return gson.fromJson(this.toString(), HashMap::class.java) as HashMap<String, Any>
}

fun <T> HashMap<*, *>.toJson(objects: Class<T>): T {
    return gson.fromJson(gson.toJsonTree(this), objects)
}

fun <T, K> Any.toHashMap(objects: T, objects2: K): HashMap<T, K> {
    return gson.fromJson(this.toString(), HashMap::class.java) as HashMap<T, K>
}


fun File.convertImageFileToBase64(): String {

    return FileInputStream(this).use { inputStream ->
        ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                inputStream.copyTo(base64FilterStream)
                base64FilterStream.flush()
                outputStream.toString()
            }
        }
    }
}

fun String.convertBase64ToBitmap(): Bitmap {
    val imageAsBytes =
        Base64.decode(this.replace("data:image/png;base64,", "").toByteArray(), Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
}

fun String.displayTimeComment(fomatDate: String = "yyyy-MM-dd HH:mm:ss"): String {
    if (this.isEmpty()) {
        return ""
    }
    //API.log("Day Ago "+dayago);
    var result = "Vừa xong";
    val formatter = SimpleDateFormat(fomatDate, Locale("vi"))
    val todayDate = formatter.format(Date())
    val calendar = Calendar.getInstance()

    val dayagolong = formatter.parse(this)?.time ?: 0L

    calendar.timeInMillis = dayagolong
    val agoformater = formatter.format(calendar.time)

    try {
        val currentDate = formatter.parse(todayDate)
        val createDate = formatter.parse(agoformater)

        var different = Math.abs(currentDate.time - createDate.time);

        val secondsInMilli = 1000;
        val minutesInMilli: Long = secondsInMilli.toLong() * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different %= daysInMilli

        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli

        val elapsedSeconds = different / secondsInMilli

        different %= secondsInMilli

        if (elapsedDays == 0L) {
            if (elapsedHours == 0L) {
                if (elapsedMinutes == 0L) {
                    if (elapsedSeconds < 0) {
                        return "0" + " s"
                    } else {
                        if (elapsedDays > 0 && elapsedSeconds < 59) {
                            return "Vừa xong"
                        }
                    }
                } else {
                    return "$elapsedMinutes phút trước"
                }
            } else {
                return "$elapsedHours giờ trước"
            }

        } else {
            if (elapsedDays <= 29) {
                return "$elapsedDays ngày trước"
            }
            if (elapsedDays in 30..58) {
                return "1 tháng trước"
            }
            if (elapsedDays in 59..87) {
                return "2 tháng trước"
            }
            if (elapsedDays in 88..116) {
                return "3 tháng trước"
            }
            if (elapsedDays in 117..145) {
                return "4 tháng trước"
            }
            if (elapsedDays in 146..174) {
                return "5 tháng trước"
            }
            if (elapsedDays in 175..203) {
                return "6 tháng trước"
            }
            if (elapsedDays in 204..232) {
                return "7 tháng trước"
            }
            if (elapsedDays in 233..261) {
                return "8 tháng trước"
            }
            if (elapsedDays in 262..290) {
                return "9 tháng trước"
            }
            if (elapsedDays in 291..319) {
                return "10 tháng trước"
            }
            if (elapsedDays in 320..348) {
                return "11 tháng trước"
            }
            if (elapsedDays in 349..360) {
                return "12 tháng trước"
            }

            if (elapsedDays in 361..720) {
                return "1 năm trước"
            }

            if (elapsedDays > 720) {
                val formatterYear = SimpleDateFormat("MM/dd/yyyy", Locale("vi", "VN"))
                val calendarYear = Calendar.getInstance()
                calendarYear.timeInMillis = dayagolong
                return formatterYear.format(calendarYear.time) + ""
            }

        }

    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return result;
}

fun Activity.showFragment(
    supportFragmentManager: FragmentManager,
    contentView: View,
    fragment: Fragment,
    tag: String = "",
    removeBackstack: Boolean = false,
) {
    contentView.visble()
    if (removeBackstack)
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }

    supportFragmentManager.beginTransaction()
        .add(contentView.id, fragment, tag)
        .addToBackStack(fragment::class.java.simpleName)
        .commit()
}

fun Fragment.showFragment(
    supportFragmentManager: FragmentManager,
    contentView: View,
    fragment: Fragment,
    removeBackstack: Boolean = false,
) {
    contentView.visble()
    if (removeBackstack)
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }

    supportFragmentManager.beginTransaction()
        .add(contentView.id, fragment, tag)
        .addToBackStack(fragment::class.java.simpleName)
        .commit()
}

@SuppressLint("HardwareIds")
fun Activity.getAndroidId(): String {
    val androidId =
        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    return androidId
}

@SuppressLint("HardwareIds")
fun Fragment.getAndroidId(): String {
    val androidId =
        Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
    return androidId
}
