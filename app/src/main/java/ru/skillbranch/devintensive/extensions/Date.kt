package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import ru.skillbranch.devintensive.extensions.TimeUnits.*

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = SECOND): Date {
    this.time = this.time + (units.value * value)
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var prefix = ""
    var postfix = ""

    var difference = date.time - this.time

    if (difference < 0){
        prefix = "через "
        difference = -difference
    } else {
        postfix = " назад"
    }

    return when(difference) {
        in 0..1*SECOND.value -> "только что"
        in 1*SECOND.value..45*SECOND.value -> "${prefix}несколько секунд$postfix"
        in 45*SECOND.value..75*SECOND.value -> "${prefix}минуту$postfix"
        in 75*SECOND.value..45*MINUTE.value -> "$prefix${MINUTE.plural(difference/MINUTE.value)}$postfix"
        in 45*MINUTE.value..75*MINUTE.value -> "${prefix}час$postfix"
        in 75*MINUTE.value..22*HOUR.value -> "$prefix${HOUR.plural(difference/HOUR.value)}$postfix"
        in 22*HOUR.value..26*HOUR.value -> "${prefix}день$postfix"
        in 26*HOUR.value..360*DAY.value -> "$prefix${DAY.plural(difference/DAY.value)}$postfix"
        else -> if(date.time - this.time < 0) "более чем через год" else "более года назад"
    }
}

enum class TimeUnits(val value: Long, private val ONE: String, private val FEW: String, private val MANY: String) {

    SECOND(1000L,"секунду", "секунды", "секунд"),
    MINUTE(1000L*60L, "минуту", "минуты", "минут"),
    HOUR(1000L*60L*60L, "час", "часа", "часов"),
    DAY(1000L*60L*60L*24L, "день", "дня", "дней");

    fun plural(num: Long) : String {
        return "$num ${this.getAmount(num)}"
    }

    private fun getAmount(num: Long) : String {
        return when{
            num in 5..20L -> MANY
            num%10  == 1L  -> ONE
            num%10 in 2..4L  -> FEW
            else -> MANY
        }
    }
}

fun Date.shortFormat(): String? {
    val pattern = if (this.issSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.issSameDay(date: Date) : Boolean {
    return this.time/ DAY.value == date.time/ DAY.value
}