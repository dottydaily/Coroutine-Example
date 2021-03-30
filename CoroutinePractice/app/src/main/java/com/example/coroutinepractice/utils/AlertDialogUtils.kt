package com.example.coroutinepractice.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import com.example.coroutinepractice.R
import com.example.coroutinepractice.databinding.AlertDialogBinding

object AlertDialogUtils {

    /**
     * A method for showing alert dialog
     * @param context The Application's context.
     * @param promptMessage The prompt message of this alert box.
     * @param buttonMessage The button's message of this alert box.
     * @param buttonColor The button's color id.
     * @param isShowCancel The Boolean tell if needed to show or hide cancel button.
     * @param isCancelable The Boolean tell if this dialog is cancelable or not.
     * @param titleMessage The title message to show this dialog with title.
     * @param buttonListener The listener when pressing the primary button.
     */
    fun show(context: Context,
             promptMessage: String,
             buttonMessage: String,
             buttonColor: Int = R.color.colorBlue,
             isShowCancel: Boolean = false,
             isCancelable: Boolean = true,
             titleMessage: String? = null,
             buttonListener: (AlertDialog) -> Unit) {
        val binding = AlertDialogBinding.inflate(LayoutInflater.from(context))
        val alertBuilder = AlertDialog.Builder(context).setView(binding.root)
        val alertDialog = alertBuilder.create()
        alertDialog.run {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setGravity(Gravity.CENTER)
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(isCancelable)
            show()
        }

        binding.apply {
            if (titleMessage != null) {
                textWithTitleViewGroup.visibility = View.VISIBLE
                messageTextView.visibility = View.GONE

                titleTextView.text = titleMessage
                descriptionTextView.text = promptMessage
            } else {
                messageTextView.text = promptMessage
            }

            cancelButton.apply {
                visibility = if (isShowCancel) View.VISIBLE else View.GONE
                setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            button.apply {
                text = buttonMessage
                setTextColor(ContextCompat.getColor(context, buttonColor))
                setOnClickListener {
                    buttonListener(alertDialog)
                }
            }
        }
    }

    /**
     * A method for handle when user has select don't ask again on permission request.
     */
    fun handleNeverAskLocationAgain(context: Context) {
        val promptMessage = "Please allow location permission in setting page."
        val buttonMessage = "Setting"
        show(context, promptMessage, buttonMessage, isShowCancel = true, isCancelable = true,
            buttonListener = {
                it.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        )
    }
}