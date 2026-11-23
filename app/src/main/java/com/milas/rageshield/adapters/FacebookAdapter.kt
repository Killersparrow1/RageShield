package com.milas.rageshield.adapters

class FacebookAdapter : FeedAdapter(
    platformName = "Facebook",
    supportedPackageNames = setOf("com.facebook.katana", "com.facebook.lite"),
    isStable = true
)
