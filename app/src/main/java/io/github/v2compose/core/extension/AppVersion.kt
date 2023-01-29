package io.github.v2compose.core.extension

//https://semver.org/ eg: v0.0.1 、 1.0.0
fun String.toAppVersion(): Triple<Int, Int, Int> {
    val parts = (if (this.startsWith('v')) substring(startIndex = 1) else this).split('.')
    if (parts.size >= 3) {
        return Triple(
            first = parts[0].toIntOrNull() ?: 0,
            second = parts[1].toIntOrNull() ?: 0,
            third = parts[2].toIntOrNull() ?: 0
        )
    }
    return Triple(0, 0, 0)
}

fun Triple<Int, Int, Int>.newerThan(other: Triple<Int, Int, Int>): Boolean {
    return if (this.first > other.first) {
        true
    } else if (this.second > other.second) {
        true
    } else {
        this.third > other.third
    }
}