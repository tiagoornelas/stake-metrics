package net.stakemetrics.application.utils

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Component
class DateHelper {

    fun getDateRangeByString(date: String): Pair<Date, Date> {
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        val parsedDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dateStart = calendar.time

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val dateEnd = calendar.time

        return Pair(dateStart, dateEnd)
    }
}