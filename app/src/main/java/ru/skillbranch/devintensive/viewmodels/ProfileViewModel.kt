package ru.skillbranch.devintensive.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository

class ProfileViewModel : ViewModel() {

    private val repository : PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()
    private val isRepoValid = MutableLiveData<Boolean>()

    init {
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
    }

    fun isRepoValid(): LiveData<Boolean> = isRepoValid

    fun getProfileData(): LiveData<Profile> = profileData

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value = profile
    }

    fun getTheme(): LiveData<Int> = appTheme

    fun switchTheme() {
        if(appTheme.value == AppCompatDelegate.MODE_NIGHT_YES) {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES
        }
        repository.saveAppTheme(appTheme.value!!)
    }

    fun repositoryValidation(repository: String) {
        isRepoValid.value = repository.isEmpty() || repository.matches(
                Regex("^(https://)?(www.)?(github.com/)(?!(${getRegexExceptions()})(?=/|$))(?![\\W])(?!\\w+[-]{2})[a-zA-Z0-9-]+(?<![-])(/)?$"))
    }

    private fun getRegexExceptions(): String {
        val exceptions = arrayOf(
                "enterprise", "features", "topics", "collections", "trending", "events", "join",
                "pricing", "nonprofit", "customer-stories", "security", "login", "marketplace"
        )
        return exceptions.joinToString("|")
    }
}