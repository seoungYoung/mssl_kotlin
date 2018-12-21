package org.jsy.mssl_kotlin
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write.*

/**
 * A simple [Fragment] subclass.
 *
 */
class DialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = AlertDialog.Builder(activity)
        builder.setTitle("타이틀 입니다")
        builder.setMessage("메시지 입니다")

        var listener = DialogListener()

        builder.setPositiveButton("Yes", listener)
        builder.setNeutralButton("No", listener)
        builder.setNegativeButton("Cancel", listener)

        var alert = builder.create()
        return alert
    }

    inner class DialogListener : DialogInterface.OnClickListener{
        override fun onClick(dialog: DialogInterface?, which: Int) {
            var main_activity = activity as MainActivity
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    main_activity.textView3.text = "Yes"
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    main_activity.textView3.text = "No"
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    main_activity.textView3.text = "Cancel"
                }
            }
        }
    }
}