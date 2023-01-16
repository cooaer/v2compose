package io.github.v2compose.core.extension

inline fun <reified T> Any?.castOrNull(): T? = if (this is T) this else null

inline fun <reified T> Any?.cast(orElse: () -> T): T = if (this is T) this else orElse()