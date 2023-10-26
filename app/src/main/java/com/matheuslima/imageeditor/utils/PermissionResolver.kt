package com.matheuslima.imageeditor.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

interface PermissionResolver {
    fun requestStoragePermission(activity: AppCompatActivity, context: Context)
}