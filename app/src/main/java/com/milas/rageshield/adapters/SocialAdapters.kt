package com.milas.rageshield.adapters

class InstagramAdapter : FeedAdapter("Instagram", setOf("com.instagram.android"))

class YoutubeAdapter : FeedAdapter("YouTube", setOf("com.google.android.youtube"))

class TiktokAdapter : FeedAdapter(
    "TikTok",
    setOf("com.ss.android.ugc.trill", "com.zhiliaoapp.musically")
)

class TwitterAdapter : FeedAdapter("X / Twitter", setOf("com.twitter.android"))

class ThreadsAdapter : FeedAdapter("Threads", setOf("com.instagram.barcelona"))

class RedditAdapter : FeedAdapter("Reddit", setOf("com.reddit.frontpage"))

class LinkedInAdapter : FeedAdapter("LinkedIn", setOf("com.linkedin.android"))

class SnapchatAdapter : FeedAdapter("Snapchat", setOf("com.snapchat.android"))

class PinterestAdapter : FeedAdapter("Pinterest", setOf("com.pinterest"))

class BlueskyAdapter : FeedAdapter("Bluesky", setOf("xyz.blueskyweb.app"))

class MastodonAdapter : FeedAdapter("Mastodon", setOf("org.joinmastodon.android"))
