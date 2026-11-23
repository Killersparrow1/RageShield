package com.milas.rageshield.core

import com.milas.rageshield.adapters.BlueskyAdapter
import com.milas.rageshield.adapters.FacebookAdapter
import com.milas.rageshield.adapters.InstagramAdapter
import com.milas.rageshield.adapters.LinkedInAdapter
import com.milas.rageshield.adapters.MastodonAdapter
import com.milas.rageshield.adapters.PinterestAdapter
import com.milas.rageshield.adapters.RedditAdapter
import com.milas.rageshield.adapters.SnapchatAdapter
import com.milas.rageshield.adapters.ThreadsAdapter
import com.milas.rageshield.adapters.TiktokAdapter
import com.milas.rageshield.adapters.TwitterAdapter
import com.milas.rageshield.adapters.YoutubeAdapter

object PlatformRegistry {
    val adapters: List<PlatformAdapter> = listOf(
        FacebookAdapter(),
        InstagramAdapter(),
        YoutubeAdapter(),
        TiktokAdapter(),
        TwitterAdapter(),
        ThreadsAdapter(),
        RedditAdapter(),
        LinkedInAdapter(),
        SnapchatAdapter(),
        PinterestAdapter(),
        BlueskyAdapter(),
        MastodonAdapter()
    )

    fun getAdapterForPackage(packageName: String): PlatformAdapter? {
        return adapters.find { it.canIdentifyContent(packageName) }
    }
}
