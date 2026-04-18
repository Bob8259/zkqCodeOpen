package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.coc.zkqcode.core.util.fileactions.saveCustomBackground

/**
 * Transparent Activity used to launch the system image picker from a Service context.
 * It immediately opens the picker on creation and finishes after handling the result.
 */
class ImagePickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                saveCustomBackground(applicationContext, uri)
            }
        }
        finish()
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
