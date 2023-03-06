package io.github.v2compose.core.extension

private const val dayMills = 24 * 60 * 60 * 1000

fun Long.isBeforeTodayByUTC() = newDayThan(System.currentTimeMillis())

fun Long.newDayThan(other: Long): Boolean {
    return this / dayMills > other / dayMills
}