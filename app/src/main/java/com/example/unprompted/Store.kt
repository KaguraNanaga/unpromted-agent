package com.example.unprompted

import android.content.Context

/** 存在本机的两样东西：被“放过”的 App，和你亲手取关了多少个账号 */
class Store(context: Context) {
    private val prefs = context.getSharedPreferences("unprompted", Context.MODE_PRIVATE)

    fun pardoned(): Set<String> = prefs.getStringSet(PARDONED, emptySet()).orEmpty().toSet()
    fun pardon(pkg: String) = prefs.edit().putStringSet(PARDONED, pardoned() + pkg).apply()
    fun clearPardons() = prefs.edit().remove(PARDONED).apply()

    fun unfollowed(): Int = prefs.getInt(UNFOLLOWED, 0)
    fun setUnfollowed(n: Int) = prefs.edit().putInt(UNFOLLOWED, n).apply()

    private companion object {
        const val PARDONED = "pardoned"
        const val UNFOLLOWED = "unfollowed"
    }
}
