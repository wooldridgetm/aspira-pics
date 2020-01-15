package com.aspiraconnect.aspira_pics

import android.util.Log

inline fun <reified T: Any> clazz(): Class<T> = T::class.java
inline fun <reified T : Any> clazzName(kClass: T) : String = kClass::class.java.simpleName
inline fun <reified T : Any> clazzName(): String = T::class.java.simpleName

/**
 * prevents the logger tags from exceeding Android's Limits
 */
private const val MAX_LOG_TAG_LENGTH = 23

/**
 * [getTag] - returns the name of the class
 * NOTE: Don't use this when obfuscating class names!
 */
fun getTag(tag: String) = if (tag.length > MAX_LOG_TAG_LENGTH) tag.substring(0, MAX_LOG_TAG_LENGTH - 1) else tag


/**
 * Extension Functions
 */
inline fun <reified T : Any> T.info(msg: Any)= Log.i(getTag(clazzName<T>()), msg.toString())
inline fun <reified T : Any> T.verbose(msg: Any)= Log.v(getTag(clazzName<T>()), msg.toString())
inline fun <reified T : Any> T.debug(msg: Any?)= Log.d(getTag(clazzName<T>()), msg?.toString() ?: "null")
inline fun <reified T : Any> T.warn(msg: Any) = Log.w(getTag(clazzName<T>()), msg.toString())
inline fun <reified T : Any> T.warn(e: Throwable) = Log.w(getTag(clazzName<T>()), e)
inline fun <reified T : Any> T.exception(msg: String)= Log.e(getTag(clazzName<T>()), msg)
inline fun <reified T : Any> T.exception(e: Throwable)= Log.e(getTag(clazzName<T>()), e.message + "\n" + Log.getStackTraceString(e))
inline fun <reified T : Any> T.exception(msg: String, e: Throwable) {
    val tag = getTag(clazzName<T>())
    Log.e(tag, msg)
    Log.e(tag, e.message+'\n'+Log.getStackTraceString(e))
}
