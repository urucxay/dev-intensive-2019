package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*

import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    private var isEditMode = false
    private lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
        viewModel.isRepoValid().observe(this, Observer { checkValidationError(it) })
    }

    private fun updateTheme(mode: Int) {
        delegate.setLocalNightMode(mode)
    }

    private fun checkValidationError(isValidate: Boolean) {
        if (isValidate) {
            wr_repository.error = null
            nested_scroll.scrollY = et_repository.bottom
        } else {
            wr_repository.error = "Невалидный адрес репозитория"
            nested_scroll.scrollY = wr_repository.bottom
            et_repository.requestFocus()
        }
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for((k,v) in viewFields) {
                v.text = it[k].toString()
            }
            iv_avatar.setInitials(it["initials"].toString())
            iv_avatar.setBorderWidth(2)
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
                viewModel.repositoryValidation(text.toString())
            }
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        btn_edit.setOnClickListener {
            if (wr_repository.error == "Невалидный адрес репозитория"){
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
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }
}