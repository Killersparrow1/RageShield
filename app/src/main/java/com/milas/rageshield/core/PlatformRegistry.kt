package com.milas.rageshield.core

import com.milas.rageshield.adapters.FacebookAdapter

object PlatformRegistry {
    private val adapters = listOf(
        FacebookAdapter(),
        InstagramAdapter(),
        YoutubeAdapter(),
        TiktokAdapter(),
        TwitterAdapter(),
        ThreadsAdapter(),
        RedditAdapter()
    )

    fun getAdapterForPackage(packageName: String): PlatformAdapter? {
        return adapters.find { it.canIdentifyContent(packageName) }
    }
}
