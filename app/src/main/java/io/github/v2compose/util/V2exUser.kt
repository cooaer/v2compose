package io.github.v2compose.util

import io.github.v2compose.Constants

fun String.isUserPath() = this.startsWith(Constants.userPath)