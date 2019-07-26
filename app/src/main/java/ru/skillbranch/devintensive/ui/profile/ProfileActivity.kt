package ru.skillbranch.devintensive.ui.profile

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import java.util.*
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    private lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for((k,v) in viewFields) {
                v.text = it[k].toString()
            }
            drawDefaultAvatar(it["initials"].toString())
        }
    }

    private fun initViews(savedInstanceState: Bundle?) {

        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)


        et_repository.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                val fullAddress = text.toString()
                if (isRepositoryValid(fullAddress)) {
                    wr_repository.error = null
                    wr_repository.isErrorEnabled = false
                } else {
                    wr_repository.error = "Невалидный адрес репозитория"
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        btn_edit.setOnClickListener {

            if (!isRepositoryValid(et_repository.text.toString())) {
                et_repository.setText("")
            }

            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for((_, value) in info){
            (value as EditText).apply {
                isFocusable = isEdit
                isFocusableInTouchMode = isEdit
                isEnabled = isEdit
                background.alpha = if(isEdit) 255 else 0
            }
        }
        ic_eye.visibility = if(isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit
        wr_repository.isErrorEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if(isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme), PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if(isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }
            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository =  et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }

    fun isRepositoryValid(fullAddress: String) : Boolean {
        val address = fullAddress.substringBeforeLast("/").toLowerCase(Locale.getDefault())
        var username = fullAddress.substringAfterLast("/").toLowerCase(Locale.getDefault())

        if (username == address) username = ""

        return when {
            fullAddress == "" -> true
            isAddressValid(address) && isUserNameValid(username) -> true
            else -> false
        }
    }

     private fun isUserNameValid(name: String) : Boolean {
        val invalidNames = listOf(
                "",
                "enterprise",
                "features",
                "topics",
                "collections",
                "trending",
                "events",
                "marketplace",
                "pricing",
                "nonprofit",
                "customer-stories",
                "security",
                "login",
                "join")

        return when {
            invalidNames.any{ it == name} -> false
            name.startsWith(" ") -> false
            name.contains(Regex("[^a-zA-Z0-9-]")) -> false
            name.startsWith("-") || name.endsWith("-") -> false
            else -> true
        }
    }

    private fun isAddressValid(address: String) : Boolean {
        val validAddresses = listOf(
                "https://www.github.com",
                "https://github.com",
                "www.github.com",
                "github.com"
        )
        return when {
            validAddresses.any{ it == address} -> true
            else -> false
        }
    }

    private fun drawDefaultAvatar(initials: String, textSize: Float = 48f, color: Int = Color.WHITE) {
        val bitmap = textAsBitmap(initials, textSize, color)
        val drawable = BitmapDrawable(resources, bitmap)
        iv_avatar.setImageDrawable(drawable)
    }

    private fun textAsBitmap(text:String, textSize:Float, textColor:Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        val image = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888)

        image.eraseColor(getThemeAccentColor(this))
        val canvas = Canvas(image)
        canvas.drawText(text, 56f, 56f + paint.textSize/3, paint)
        return image
    }

    private fun getThemeAccentColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }
}