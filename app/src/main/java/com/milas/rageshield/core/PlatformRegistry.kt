package com.milas.rageshield.core

import com.milas.rageshield.adapters.FacebookAdapter
import com.milas.rageshield.adapters.InstagramAdapter
import com.milas.rageshield.adapters.RedditAdapter
import com.milas.rageshield.adapters.ThreadsAdapter
import com.milas.rageshield.adapters.TiktokAdapter
import com.milas.rageshield.adapters.TwitterAdapter
import com.milas.rageshield.adapters.YoutubeAdapter

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
