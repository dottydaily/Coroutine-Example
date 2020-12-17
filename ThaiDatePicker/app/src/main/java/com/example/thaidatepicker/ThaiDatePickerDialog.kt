package com.example.thaidatepicker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener
import androidx.annotation.StyleRes
import java.util.*

/**
 * A simple dialog containing an [android.widget.DatePicker].
 *
 *
 * See the [Pickers]({@docRoot}guide/topics/ui/controls/pickers.html)
 * guide.
 */
open class DatePickerDialog private constructor(
    context: Context, @StyleRes themeResId: Int,
    listener: OnDateSetListener?, calendar: Calendar?, year: Int,
    monthOfYear: Int, dayOfMonth: Int
) :
    AlertDialog(context, DatePickerDialog.Companion.resolveDialogTheme(context, themeResId)),
    DialogInterface.OnClickListener, OnDateChangedListener {
    /**
     * Returns the [DatePicker] contained in this dialog.
     *
     * @return the date picker
     */
    @UnsupportedAppUsage
    val datePicker: DatePicker
    private var mDateSetListener: OnDateSetListener?

    /**
     * Creates a new date picker dialog for the current date using the parent
     * context's default date picker dialog theme.
     *
     * @param context the parent context
     */
    constructor(context: Context) : this(context, 0, null, Calendar.getInstance(), -1, -1, -1) {}

    /**
     * Creates a new date picker dialog for the current date.
     *
     * @param context the parent context
     * @param themeResId the resource ID of the theme against which to inflate
     * this dialog, or `0` to use the parent
     * `context`'s default alert dialog theme
     */
    constructor(context: Context, @StyleRes themeResId: Int) : this(
        context,
        themeResId,
        null,
        Calendar.getInstance(),
        -1,
        -1,
        -1
    ) {
    }

    /**
     * Creates a new date picker dialog for the specified date using the parent
     * context's default date picker dialog theme.
     *
     * @param context the parent context
     * @param listener the listener to call when the user sets the date
     * @param year the initially selected year
     * @param month the initially selected month (0-11 for compatibility with
     * [Calendar.MONTH])
     * @param dayOfMonth the initially selected day of month (1-31, depending
     * on month)
     */
    constructor(
        context: Context, listener: OnDateSetListener?,
        year: Int, month: Int, dayOfMonth: Int
    ) : this(context, 0, listener, null, year, month, dayOfMonth) {
    }

    /**
     * Creates a new date picker dialog for the specified date.
     *
     * @param context the parent context
     * @param themeResId the resource ID of the theme against which to inflate
     * this dialog, or `0` to use the parent
     * `context`'s default alert dialog theme
     * @param listener the listener to call when the user sets the date
     * @param year the initially selected year
     * @param monthOfYear the initially selected month of the year (0-11 for
     * compatibility with [Calendar.MONTH])
     * @param dayOfMonth the initially selected day of month (1-31, depending
     * on month)
     */
    constructor(
        context: Context, @StyleRes themeResId: Int,
        listener: OnDateSetListener?, year: Int, monthOfYear: Int, dayOfMonth: Int
    ) : this(context, themeResId, listener, null, year, monthOfYear, dayOfMonth) {
    }

    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        datePicker.init(year, month, dayOfMonth, this)
    }

    /**
     * Sets the listener to call when the user sets the date.
     *
     * @param listener the listener to call when the user sets the date
     */
    fun setOnDateSetListener(listener: OnDateSetListener?) {
        mDateSetListener = listener
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            BUTTON_POSITIVE -> if (mDateSetListener != null) {
                // Clearing focus forces the dialog to commit any pending
                // changes, e.g. typed text in a NumberPicker.
                datePicker.clearFocus()
                mDateSetListener!!.onDateSet(
                    datePicker, datePicker.year,
                    datePicker.month, datePicker.dayOfMonth
                )
            }
            BUTTON_NEGATIVE -> cancel()
        }
    }

    /**
     * Sets the current date.
     *
     * @param year the year
     * @param month the month (0-11 for compatibility with
     * [Calendar.MONTH])
     * @param dayOfMonth the day of month (1-31, depending on month)
     */
    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        datePicker.updateDate(year, month, dayOfMonth)
    }

    override fun onSaveInstanceState(): Bundle {
        val state = super.onSaveInstanceState()
        state.putInt(DatePickerDialog.Companion.YEAR, datePicker.year)
        state.putInt(DatePickerDialog.Companion.MONTH, datePicker.month)
        state.putInt(DatePickerDialog.Companion.DAY, datePicker.dayOfMonth)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val year = savedInstanceState.getInt(DatePickerDialog.Companion.YEAR)
        val month = savedInstanceState.getInt(DatePickerDialog.Companion.MONTH)
        val day = savedInstanceState.getInt(DatePickerDialog.Companion.DAY)
        datePicker.init(year, month, day, this)
    }

    private val mValidationCallback: ValidationCallback = object : ValidationCallback() {
        fun onValidationChanged(valid: Boolean) {
            val positive = getButton(BUTTON_POSITIVE)
            if (positive != null) {
                positive.isEnabled = valid
            }
        }
    }

    /**
     * The listener used to indicate the user has finished selecting a date.
     */
    interface OnDateSetListener {
        /**
         * @param view the picker associated with the dialog
         * @param year the selected year
         * @param month the selected month (0-11 for compatibility with
         * [Calendar.MONTH])
         * @param dayOfMonth the selected day of the month (1-31, depending on
         * month)
         */
        fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
    }

    companion object {
        private const val YEAR = "year"
        private const val MONTH = "month"
        private const val DAY = "day"
        @StyleRes
        fun resolveDialogTheme(context: Context, @StyleRes themeResId: Int): Int {
            return if (themeResId == 0) {
                val outValue = TypedValue()
                context.theme.resolveAttribute(R.attr.datePickerDialogTheme, outValue, true)
                outValue.resourceId
            } else {
                themeResId
            }
        }
    }

    init {
        var year = year
        var monthOfYear = monthOfYear
        var dayOfMonth = dayOfMonth
        val themeContext = getContext()
        val inflater = LayoutInflater.from(themeContext)
        val view: View = inflater.inflate(R.layout.date_picker_dialog, null)
        setView(view)
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), this)
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this)
        setButtonPanelLayoutHint(LAYOUT_HINT_SIDE)
        if (calendar != null) {
            year = calendar[Calendar.YEAR]
            monthOfYear = calendar[Calendar.MONTH]
            dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        }
        datePicker = view.findViewById<View>(R.id.datePicker) as DatePicker
        datePicker.init(year, monthOfYear, dayOfMonth, this)
        datePicker.setValidationCallback(mValidationCallback)
        mDateSetListener = listener
    }
}