package net.stakemetrics.application.entities.enums

enum class StrategyScopeTypes(val daysValue: Int) {
    TWENTY_FOUR_HOURS(1),
    THREE_DAYS(3),
    ONE_WEEK(7),
    TWO_WEEKS(14),
    ONE_MONTH(30),
    TWO_MONTHS(60)
}