package converter

import java.util.*

enum class Measures {
    Weight, Length, Temp
}

enum class MeasureDescription(val shortStr: String, val singularStr: String, val pluralStr: String, val num: Double, val measure: Measures) {
    G("g", "gram", "grams",1.0, Measures.Weight),
    KG("kg", "kilogram", "kilograms",1000.0, Measures.Weight),
    MG("mg", "milligram", "milligrams", 0.001, Measures.Weight),
    LB("lb", "pound", "pounds", 453.592, Measures.Weight),
    OZ("oz", "ounce", "ounces", 28.3495, Measures.Weight),
    M("m", "meter", "meters", 1.0, Measures.Length),
    KM("km", "kilometer", "kilometers", 1000.0, Measures.Length),
    CM("cm", "centimeter", "centimeters", 0.01, Measures.Length),
    MM("mm", "millimeter", "millimeters", 0.001, Measures.Length),
    MI("mi", "mile", "miles", 1609.35, Measures.Length),
    YD("yd", "yard", "yards", 0.9144, Measures.Length),
    FT("ft", "foot", "feet", 0.3048, Measures.Length),
    IN("in", "inch", "inches", 0.0254, Measures.Length),
    C("c", "dc", "celsius", 1.0, Measures.Temp),
    K("k", "kelvin", "kelvins", 273.15, Measures.Temp),
    F("f", "df", "fahrenheit", 9.0 / 5.0, Measures.Temp)
}

fun findByString(str: String): MeasureDescription? {
    for (enum in MeasureDescription.values()) {
        if (str == enum.shortStr || str == enum.singularStr || str == enum.pluralStr) return enum
    }
    return null
}

fun enterMsg() = print("Enter what you want to convert (or exit): ")

fun errorMsg(fromStr: String, toStr: String) = "Conversion from $fromStr to $toStr is impossible\n"

fun measureStr(measure: MeasureDescription): String {
    return if (measure.name != MeasureDescription.C.name && measure.name != MeasureDescription.F.name) measure.pluralStr
    else "degrees ${measure.pluralStr.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"
}

fun notValid(from: MeasureDescription?, to: MeasureDescription?): String {
    return if (from == null) {
        if (to == null) errorMsg("???", "???")
        else errorMsg("???", measureStr(to))
    } else if (to == null) errorMsg(measureStr(from), "???") else errorMsg(measureStr(from), measureStr(to))
}

fun partOfMsg(num: Double, measure: MeasureDescription): String {
    return if (measure.name != MeasureDescription.C.name && measure.name != MeasureDescription.F.name) {
        "$num ${if (num != 1.0) measure.pluralStr else measure.singularStr}"
    } else
        "$num degree${if (num != 1.0) "s" else ""} ${measure.pluralStr.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"
}

fun convMsg(fromNum: Double, fromMeasure: MeasureDescription, toNum: Double, toMeasure: MeasureDescription): String {
    return "${partOfMsg(fromNum, fromMeasure)} is ${partOfMsg(toNum, toMeasure)}\n"
}

fun negNumMsg(fromMeasure: MeasureDescription) = "${fromMeasure.measure} shouldn't be negative"

fun convertTemp(fromMeasure: MeasureDescription, toMeasure: MeasureDescription, num: Double): String {
    when (fromMeasure.name) {
        MeasureDescription.K.name -> {
            return when (toMeasure.name) {
                MeasureDescription.K.name -> convMsg(num, fromMeasure, num, toMeasure)
                MeasureDescription.C.name -> convMsg(num, fromMeasure, num - fromMeasure.num, toMeasure)
                else -> convMsg(num, fromMeasure, num * toMeasure.num - 459.67, toMeasure)
            }
        }
        MeasureDescription.C.name -> {
            return when (toMeasure.name) {
                MeasureDescription.K.name -> convMsg(num, fromMeasure, num + toMeasure.num, toMeasure)
                MeasureDescription.C.name -> convMsg(num, fromMeasure, num, toMeasure)
                else -> convMsg(num, fromMeasure, num * toMeasure.num + 32, toMeasure)
            }
        }
        else -> {
            return when (toMeasure.name) {
                MeasureDescription.K.name -> convMsg(num, fromMeasure, (num + 459.67) / fromMeasure.num, toMeasure)
                MeasureDescription.C.name -> convMsg(num, fromMeasure, (num - 32) / fromMeasure.num, toMeasure)
                else -> convMsg(num, fromMeasure, num, toMeasure)
            }
        }
    }
}

fun convert(fromMeasure: MeasureDescription, toMeasure: MeasureDescription, num: Double): String {
    return if (num > 0 || fromMeasure.measure == Measures.Temp) {
        if (fromMeasure.measure == Measures.Temp) convertTemp(fromMeasure, toMeasure, num)
        else convMsg(num, fromMeasure, num * fromMeasure.num / toMeasure.num, toMeasure)
    } else negNumMsg(fromMeasure)
}

fun output(from: MeasureDescription?, to: MeasureDescription?, num: Double) {
    println(if (from == null || to == null || from.measure != to.measure) notValid(from, to) else convert(from, to, num))
}

fun main() {
    while (true) {
        enterMsg()
        val str = readln().lowercase()
        val arr = if (str == "exit") return else str.split(' ').toMutableList()
        arr.removeAll(listOf("degree", "degrees"))
        val num = arr[0].toDoubleOrNull()
        if (num != null && arr.size == 4) output(findByString(arr[1]), findByString(arr[3]), num)
        else println("Parse error\n")
    }
}
