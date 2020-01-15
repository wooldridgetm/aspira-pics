package com.aspiraconnect.aspira_pics.archive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object ExifUtil
{
    const val TAG = "ExifUtil"

    /**
     * @see [http://sylvana.net/jpegcrop/exif_orientation.html]
     */
    fun rotateBitmap(context: Context?, src: String, bitmap: Bitmap): Bitmap
    {
        val task = object: AsyncTask<Pair<String, Bitmap>, Void, Bitmap>() {

            override fun doInBackground(vararg params: Pair<String, Bitmap>?): Bitmap
            {
                TODO("not implemented")
            }
            override fun onPostExecute(result: Bitmap?)
            {
                super.onPostExecute(result)
            }
        }
        try
        {
            val orientation = getExifOrientation(src)
            if (orientation == 1)
            {
                return bitmap
            }
            val matrix = Matrix()
            when (orientation)
            {
                2 -> matrix.setScale(-1f, 1f)
                3 -> matrix.setRotate(180f)
                4 ->
                {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                5 ->
                {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                6 -> matrix.setRotate(90f)
                7 ->
                {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                8 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            return try
            {
                val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
                oriented
            }
            catch (e: OutOfMemoryError)
            {
                e.printStackTrace()
                bitmap
            }
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
        return bitmap
    }

    @Throws(IOException::class)
    private fun getExifOrientation(src: String): Int
    {
        var orientation = 1
        try
        {
            /**
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            val exifClass = Class.forName("android.media.ExifInterface")
            val exifConstructor = exifClass.getConstructor(String::class.java)
            val exifInstance = exifConstructor.newInstance(src)
            val getAttributeInt = exifClass.getMethod("getAttributeInt", String::class.java, Int::class.javaPrimitiveType)
            val tagOrientationField = exifClass.getField("TAG_ORIENTATION")
            val tagOrientation = tagOrientationField[null] as String
            orientation = getAttributeInt.invoke(exifInstance, tagOrientation, 1) as Int
        }
        catch (e: ClassNotFoundException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: SecurityException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: NoSuchMethodException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: IllegalArgumentException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: InstantiationException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: IllegalAccessException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: InvocationTargetException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: NoSuchFieldException)
        {
            Log.e(TAG, e.toString())
        }
        return orientation
    }
}