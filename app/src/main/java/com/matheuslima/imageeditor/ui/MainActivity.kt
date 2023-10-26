package com.matheuslima.imageeditor.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.matheuslima.imageeditor.R
import com.matheuslima.imageeditor.utils.PermissionResolver
import org.koin.android.ext.android.inject

const val PICK_IMAGE_REQUEST = 1

class MainActivity : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private var initialX = 0f
    private var initialY = 0f
    private var textSize = 40f
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var enteredText = ""
    private lateinit var newTextView: TextView
    private lateinit var imageView: ImageView

    private val permissionResolver: PermissionResolver by inject()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureEditButton()
        configureImageAddButton()
        configureCloseButton()

        imageView = findViewById(R.id.image)
        frameLayout = findViewById(R.id.frameLayout)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        permissionResolver.requestStoragePermission(this, baseContext)
    }

    private fun configureEditButton() {
        val editButton = findViewById<ImageView>(R.id.ic_edit)
        configureAlertEditTextDialog(editButton)
    }

    private fun configureImageAddButton() {
        val imageAddButton = findViewById<ImageView>(R.id.ic_image_add)
        imageAddButton.setOnClickListener {
            pickImage()
        }
    }

    private fun configureCloseButton() {
        val closeButton = findViewById<ImageView>(R.id.ic_close)
        closeButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("InflateParams")
    private fun configureAlertEditTextDialog(editButton: ImageView) {
        editButton.setOnClickListener {
            val dialogView =
                LayoutInflater.from(this).inflate(R.layout.transparent_dialog, null)

            val editText = dialogView.findViewById<EditText>(R.id.editText)

            createAndShowDialog(dialogView, editText)
        }
    }

    private fun createAndShowDialog(dialogView: View?, editText: EditText) {
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                enteredText = editText.text.toString()
                addTextView(enteredText)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                imageView.setImageURI(selectedImageUri)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addTextView(text: String) {
        val userText = text

        if (userText.isNotEmpty()) {
            newTextView = TextView(this)
            newTextView.text = userText
            newTextView.textSize = textSize
            newTextView.setTextColor(resources.getColor(android.R.color.white))

            frameLayout.addView(newTextView)

            newTextView.setOnTouchListener { view, event ->
                touchListener(event, view)
            }
        }
    }

    private fun touchListener(event: MotionEvent, view: View): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = view.x - event.rawX
                initialY = view.y - event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                view.x = event.rawX + initialX
                view.y = event.rawY + initialY
            }
        }
        return true
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            textSize *= scaleFactor
            newTextView.textSize = textSize
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return true
    }
}