package com.aspiraconnect.aspira_pics

import android.Manifest
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList
import kotlin.math.abs

abstract class BaseActivity : AppCompatActivity()
{
    protected var permissions = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionsRejected = ArrayList<String>()

    protected fun getPermissions()
    {
        permissions = findUnAskedPermissions(permissions)
        if (canMakeSmores() && permissions.isNotEmpty())
        {
            requestPermissions(permissions.toTypedArray(), ALL_PERMISSIONS_RESULT)
        }
    }

    /**
     * *** PERMISSIONS ***
     */
    protected fun findUnAskedPermissions(wanted: List<String>): List<String>
    {
        val list = mutableListOf<String>()
        for (perm in wanted)
        {
            if (!hasPermission(perm))
            {
                list.add(perm)
            }
        }
        return list
    }

    protected fun hasPermission(permission: String): Boolean
    {
        if (canMakeSmores())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    protected fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener)
    {
        AlertDialog.Builder(this)
          .setMessage(message)
          .setPositiveButton("OK", okListener)
          .setNegativeButton("Cancel", null)
          .create().show()
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            ALL_PERMISSIONS_RESULT ->
            {
                for (perms in permissions)
                {
                    if (!hasPermission(perms))
                    {
                        permissionsRejected.add(perms)
                    }
                }
                if (permissionsRejected.size > 0)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0]))
                        {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.", DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                {
                                    requestPermissions(permissionsRejected.toTypedArray(), ALL_PERMISSIONS_RESULT)
                                }
                            })
                            return
                        }
                    }
                }
            }
        }
    }

    companion object {
        internal const val ALL_PERMISSIONS_RESULT = 107
        private const val IMAGE_RESULT = 200

        const val REQUEST_IMAGE_CAPTURE = 9001

        fun padLeft(int: Int, length: Int = 2): String = padLeft(int.toString(), length)
        fun padLeft(str: String, length: Int = 2) : String
        {
            return when(abs(length - str.length)) {
                0 -> str
                1 -> "0$str"
                2 -> "00$str"
                3 -> "000$str"
                else -> throw Exception("Error in Fx padLeft()!!")
            }
        }

        fun canMakeSmores(): Boolean = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

    }
}