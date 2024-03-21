package dev.kobalt.holdem.android.component

import androidx.preference.PreferenceManager
import dev.kobalt.holdem.android.main.MainApplication

class Preferences(private val application: MainApplication) {

    val native get() = PreferenceManager.getDefaultSharedPreferences(application.native)!!

    var server: String?
        get() = get("server")
        set(value) = set("server", value)

    var name: String?
        get() = get("name")
        set(value) = set("name", value)

    inline operator fun <reified T> get(key: String): T? {
        return native.takeIf { it.contains(key) }?.let {
            when (T::class.java) {
                java.lang.Integer::class.java -> it.getInt(key, 0) as? T
                java.lang.Float::class.java -> it.getFloat(key, 0f) as? T
                java.lang.Boolean::class.java -> it.getBoolean(key, false) as? T
                java.lang.Long::class.java -> it.getLong(key, 0) as? T
                java.lang.String::class.java -> it.getString(key, null) as? T
                else -> throw Exception("${T::class.java.name} cannot exist in preferences.")
            }
        }
    }

    inline operator fun <reified T> set(key: String, value: T?) {
        native.edit()?.let {
            if (value == null) {
                it.remove(key)
            } else {
                when (T::class.java) {
                    java.lang.Integer::class.java -> it.putInt(key, value as Int)
                    java.lang.Float::class.java -> it.putFloat(key, value as Float)
                    java.lang.Boolean::class.java -> it.putBoolean(key, value as Boolean)
                    java.lang.Long::class.java -> it.putLong(key, value as Long)
                    java.lang.String::class.java -> it.putString(key, value as String)
                    else -> throw Exception("${T::class.java.name} cannot be put in preferences.")
                }
            }
        }?.apply()
    }

}