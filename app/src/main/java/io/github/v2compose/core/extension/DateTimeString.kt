package io.github.v2compose.core.extension

import android.content.Context
import io.github.v2compose.R
import java.text.SimpleDateFormat
import java.util.*

private const val UTC_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

fun String.toDateTime(): Date? {
    return SimpleDateFormat(UTC_TIME_PATTERN).parse(this)
}


fun String.toTimeText(context: Context): String {
    val timeMills = toDateTime()?.time ?: return this

    val timeDelta = System.currentTimeMillis() - timeMills
    val minMills = 60 * 1000
    val hourMills = 60 * minMills
    val dayMills = 24 * hourMills

    if (timeDelta >= 8 * dayMills) {
        val newFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return newFormatter.format(Date(timeMills))
    }
    if (timeDelta >= dayMills) {
        return context.getString(R.string.n_days_ago, timeDelta / dayMills)
    }
    if (timeDelta >= hourMills) {
        return context.getString(R.string.n_hours_ago, timeDelta / hourMills)
    }
    if (timeDelta >= minMills) {
        return context.getString(R.string.n_minutes_ago, timeDelta / minMills)
    }
    return context.getString(R.string.just_now)
}