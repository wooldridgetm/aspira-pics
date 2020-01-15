package com.aspiraconnect.aspira_pics

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.LruCache
import com.aspiraconnect.aspira_pics.archive.RecyclingBitmapDrawable
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.HashSet

/**
 * https://developer.android.com/topic/performance/graphics/manage-memory
 * https://stackoverflow.com/questions/34722467/android-how-to-recycle-bitmap-correctly-when-using-recyclerview
 */
object BitmapUtils
{
    val reusableBitmaps: MutableSet<SoftReference<Bitmap>> by lazy {
        Collections.synchronizedSet(HashSet<SoftReference<Bitmap>>())
    }
    private lateinit var memoryCache: LruCache<String, BitmapDrawable>

    // If you're running on Honeycomb or newer, create a
    // synchronized HashSet of references to reusable bitmaps.
    init
    {
        // TODO: need to set this
        memoryCache = object : LruCache<String, BitmapDrawable>(2048)
        {
            // Notify the removed entry that is no longer being cached.
            override fun entryRemoved(evicted: Boolean, key: String, oldValue: BitmapDrawable, newValue: BitmapDrawable)
            {
                if (oldValue is RecyclingBitmapDrawable)
                {
                    // The removed entry is a recycling drawable, so notify it
                    // that it has been removed from the memory cache.
                    oldValue.setIsCached(false)
                }
                else
                {
                    reusableBitmaps.add(SoftReference(oldValue.bitmap))
                }
            }
        }
    }
}
