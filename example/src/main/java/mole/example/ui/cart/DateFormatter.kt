package mole.example.ui.cart

import android.content.Context
import mole.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatter(
    context: Context,
) {
    private val formatter =
        SimpleDateFormat(
            context.getString(R.string.date_format),
            Locale.KOREA,
        )

    fun formatDate(timestamp: Long): String = formatter.format(Date(timestamp))
}
