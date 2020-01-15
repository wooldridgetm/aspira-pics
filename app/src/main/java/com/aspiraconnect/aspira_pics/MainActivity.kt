package com.aspiraconnect.aspira_pics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import coil.api.load
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import java.io.File
import java.io.IOException

class MainActivity : BaseActivity()
{
    private var currentPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraButton.setOnClickListener {
            takePhoto()
        }
        // getPermissions()
    }

    private fun takePhoto()
    {
        debug("Fx takePhoto()")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            // Ensure that there's a camera activity to handle the intent
            intent.resolveActivity(packageManager)?.also {

                val photoFile: File? = try
                {
                    createImageFile()
                }
                catch (ex: Exception)
                {
                    // error occurred while creating the File
                    exception(ex)
                    null
                }

                // continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "com.aspiraconnect.aspira_pics", it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // getExternalStoragePublicDirectory() with DIRECTORY_PICTURES (needs READ/WRITE permissions)
    // getExternalFilesDir() with
    @Throws(IOException::class)
    private fun createImageFile(): File
    {
        val now = DateTime.now()
        val timeStamp = "${now.year}-${padLeft(now.monthOfYear)}-${padLeft(now.dayOfMonth)}" +
          "_${padLeft(now.hourOfDay)}:${padLeft(now.minuteOfHour)}:${padLeft(now.secondOfMinute)}:${now.millisOfSecond} "

        val dir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw Exception("Error - couldn't create file!")
        return File.createTempFile("PIC_$timeStamp", ".jpg", dir).apply {
            this@MainActivity.currentPath = this.absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        debug("Fx onActivityResult()")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            debug("resultCode: RESULT_OK, data: $data")
            loadImage()
            return
        }
    }

    private fun loadImage()
    {
        if (currentPath == null) return
        val path = currentPath!!
        imageView.load(File(path))
    }

    /*private fun loadImage()
    {
        if (currentPath == null) return
        val path = currentPath!!

        // NOTE:
        // stackOverflow - https://stackoverflow.com/questions/3647993/android-bitmaps-loaded-from-gallery-are-rotated-in-imageview
        // exif orientation - http://sylvana.net/jpegcrop/exif_orientation.html
        val exif = ExifInterface(path)
        val rotate = when(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) {
            1 -> ExifInterface.ORIENTATION_NORMAL
            2 -> ExifInterface.ORIENTATION_FLIP_HORIZONTAL
            3 -> ExifInterface.ORIENTATION_ROTATE_180
            4 -> ExifInterface.ORIENTATION_FLIP_VERTICAL
            5 -> ExifInterface.ORIENTATION_TRANSPOSE
            6 -> ExifInterface.ORIENTATION_ROTATE_90
            7 -> ExifInterface.ORIENTATION_TRANSVERSE
            8 -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_NORMAL
        }

        debug("rotate: $rotate")
        val targetW = 200
        val targetH = 200

        val bmpOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            val photoW = outWidth
            val photoH = outHeight

            // determine how much to scale down the image
            val sf = Math.min(photoW / targetW, photoH / targetH)

            // decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = sf

            // https://stackoverflow.com/questions/40423555/bitmapfactory-options-inpurgeable-method-deprecated
            // inPurgeable = true

            // TODO: need to use inBitmap instead
        }
        val bmp = BitmapFactory.decodeFile(path, bmpOptions)

//        val matrix = Matrix()
//        matrix.postRotate(rotate.toFloat())
//        val bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)

        val bmp2 = ExifUtil.rotateBitmap(path, bmp)
        imageView.setImageBitmap(bmp2)
    }*/







}
