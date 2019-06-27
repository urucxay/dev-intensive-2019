package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*


const val SECOND = 1000L
const val MINUTE = SECOND * 60L
const val HOUR = MINUTE * 60L
const val DAY = HOUR * 24L

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnit = TimeUnit.SECOND): Date {
    var time = this.time

    time += when(units){
       TimeUnit.SECOND ->  value * SECOND
       TimeUnit.MINUTE ->  value * MINUTE
       TimeUnit.HOUR ->  value * HOUR
       TimeUnit.DAY ->  value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val difference = date.time - this.time

    if (difference < 0) {
        return when(-difference) {
            in 0..1*SECOND -> "только что"
            in 1*SECOND..45*SECOND -> "через несколько секунд"
            in 45*SECOND..75* SECOND -> "через минуту"
            in 75*SECOND..45* MINUTE -> "через ${-difference/ MINUTE}" +
                    " минут${if (-difference/ MINUTE in 1..1) "у" else if (-difference/ MINUTE in 2..4) "ы" else ""}"
            in 45*MINUTE..75* MINUTE -> "через час"
            in 75* MINUTE..22* HOUR -> "через ${-difference/ HOUR}" +
                    " час${if (-difference/ HOUR in 1..1) "" else if (-difference/ HOUR in 2..4) "а" else "ов"}"
            in 22* HOUR..26* HOUR -> "через день"
            in 26* HOUR..360* DAY -> "через ${-difference/ DAY}" +
                    " ${if (-difference/ DAY in 1..1) "день" else if (-difference/ DAY in 2..4) "дня" else "дней"}"
            else -> "более чем через год"
        }
    } else {
        return when(difference) {
            in 0..1*SECOND -> "только что"
            in 1*SECOND..45*SECOND -> "несколько секунд назад"
            in 45*SECOND..75* SECOND -> "минуту назад"
            in 75*SECOND..45* MINUTE -> "${difference/ MINUTE}" +
                    " минут${if (difference / MINUTE in 0..1) "у" else if (difference / MINUTE in 2..4) "ы" else ""} назад"
            in 45*MINUTE..75* MINUTE -> "час назад"
            in 75* MINUTE..22* HOUR -> "${difference/ HOUR}" +
                    " час${if (difference / HOUR in 1..1) "" else if (difference / HOUR in 2..4) "а" else "ов"} назад"
            in 22* HOUR..26* HOUR -> "День назад"
            in 26* HOUR..360* DAY -> "${difference/ DAY}" +
                    " ${if (difference / DAY in 1..1) "день" else if (difference / DAY in 2..4) "дня" else "дней"} назад"
            else -> "более года назад"
        }
    }
}

enum class TimeUnit {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}