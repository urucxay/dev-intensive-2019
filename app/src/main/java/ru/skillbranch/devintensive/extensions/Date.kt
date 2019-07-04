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

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when(units){
       TimeUnits.SECOND ->  value * SECOND
       TimeUnits.MINUTE ->  value * MINUTE
       TimeUnits.HOUR ->  value * HOUR
       TimeUnits.DAY ->  value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var difference = date.time - this.time

    fun dif(type: Long, forOne: String = "", forTwoToFour: String = "", other: String = "") : String {

        if (difference < 0) {
            difference = -difference
        }

        return when {
            difference/type in 5..20 -> other
            (difference/type)%10  == 1L  -> forOne
            (difference/type)%10 in 2..4  -> forTwoToFour
            else -> other
        }
    }

    if (difference < 0) {
        return when(-difference) {
            in 0..1*SECOND -> "только что"
            in 1*SECOND..45*SECOND -> "через несколько секунд"
            in 45*SECOND..75*SECOND -> "через минуту"
            in 75*SECOND..45*MINUTE -> "через ${-difference/ MINUTE}" +
                    " ${dif(MINUTE, "минуту", "минуты", "минут")}"
            in 45*MINUTE..75*MINUTE -> "через час"
            in 75*MINUTE..22*HOUR -> "через ${-difference/ HOUR}" +
                    " ${dif(HOUR, "час", "часа", "часов")}"
            in 22*HOUR..26*HOUR -> "через день"
            in 26*HOUR..360*DAY -> "через ${-difference/ DAY}" +
                    " ${dif(DAY, "день", "дня", "дней")}"
            else -> "более чем через год"
        }
    } else {
        return when(difference) {
            in 0..1*SECOND -> "только что"
            in 1*SECOND..45*SECOND -> "несколько секунд назад"
            in 45*SECOND..75*SECOND -> "минуту назад"
            in 75*SECOND..45*MINUTE -> "${difference/ MINUTE}" +
                    " ${dif(MINUTE, "минуту", "минуты", "минут")} назад"
            in 45*MINUTE..75*MINUTE -> "час назад"
            in 75*MINUTE..22*HOUR -> "${difference/ HOUR}" +
                    " ${dif(HOUR, "час", "часа", "часов")} назад"
            in 22*HOUR..26*HOUR -> "день назад"
            in 26*HOUR..360*DAY -> "${difference/ DAY}" +
                    " ${dif(DAY, "день", "дня", "дней")} назад"
            else -> "более года назад"
        }
    }
}

enum class TimeUnits(val ONE: String, val FEW: String, val MANY: String) {

    SECOND("секунду", "секунды", "секунд"),
    MINUTE("минуту", "минуты", "минут"),
    HOUR("час", "часа", "часов"),
    DAY("день", "дня", "дней");

    fun plural(num: Int) : String {
        return "$num ${this.getAmount(num)}"
    }

    private fun getAmount(num: Int) : String {
        return when{
            num in 5..20 -> MANY
            num%10  == 1  -> ONE
            num%10 in 2..4  -> FEW
            else -> MANY
        }
    }


}