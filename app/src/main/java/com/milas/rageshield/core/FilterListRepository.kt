package com.milas.rageshield.core

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Community keyword list. Downloads keywords.txt from the filter-list repository,
 * caches it on the device and merges it with the built-in words.
 *
 * Only the list is downloaded. Nothing about what the user reads is ever uploaded.
 */
object FilterListRepository {

    /** Change this if the filter list repository moves. */
    const val LIST_URL =
        "https://raw.githubusercontent.com/Killersparrow1/RageShield-FilterList/main/keywords.txt"

    private const val PREFS = "rageshield_filterlist"
    private const val KEY_TEXT = "list_text"
    private const val KEY_FETCHED_AT = "fetched_at"
    private const val MAX_BYTES = 512 * 1024
    private const val MAX_ENTRIES = 5000
    private const val MAX_ENTRY_LENGTH = 60
    private const val AUTO_REFRESH_MS = 24L * 60 * 60 * 1000

    private val executor = Executors.newSingleThreadExecutor()

    @Volatile
    var keywords: Set<String> = RageDetectionEngine.BUILT_IN_KEYWORDS
        private set

    @Volatile
    var communityCount: Int = 0
        private set

    /** Loads the cached list, then refreshes from the network if it is stale. */
    fun init(context: Context) {
        val app = context.applicationContext
        val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.getString(KEY_TEXT, null)?.let { apply(parse(it)) }
        val age = System.currentTimeMillis() - prefs.getLong(KEY_FETCHED_AT, 0L)
        if (age > AUTO_REFRESH_MS) refresh(app) {}
    }

    fun lastUpdated(context: Context): Long =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_FETCHED_AT, 0L)

    /** Downloads the list in the background. [onDone] receives an error message or null. */
    fun refresh(context: Context, onDone: (error: String?) -> Unit) {
        val app = context.applicationContext
        executor.execute {
            val error = try {
                val text = download()
                val parsed = parse(text)
                if (parsed.isEmpty()) {
                    "Downloaded list was empty"
                } else {
                    app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                        .putString(KEY_TEXT, text)
                        .putLong(KEY_FETCHED_AT, System.currentTimeMillis())
                        .apply()
                    apply(parsed)
                    null
                }
            } catch (e: Exception) {
                e.message ?: "Could not download the list"
            }
            onDone(error)
        }
    }

    private fun apply(community: Set<String>) {
        communityCount = community.size
        keywords = RageDetectionEngine.BUILT_IN_KEYWORDS + community
    }

    private fun download(): String {
        val conn = URL(LIST_URL).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            if (conn.responseCode != 200) error("Server returned ${conn.responseCode}")
            val bytes = conn.inputStream.use { it.readNBytesCapped(MAX_BYTES) }
            return String(bytes, Charsets.UTF_8)
        } finally {
            conn.disconnect()
        }
    }

    private fun java.io.InputStream.readNBytesCapped(max: Int): ByteArray {
        val out = java.io.ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (true) {
            val n = read(buf)
            if (n < 0) break
            out.write(buf, 0, n)
            if (out.size() > max) error("List is too large")
        }
        return out.toByteArray()
    }

    /** One entry per line; blank lines and lines starting with # are ignored. */
    internal fun parse(text: String): Set<String> =
        text.lineSequence()
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() && !it.startsWith("#") && it.length in 2..MAX_ENTRY_LENGTH }
            .take(MAX_ENTRIES)
            .toSet()
}
