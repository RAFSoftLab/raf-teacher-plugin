package edu.raf.plugins.teacher.utils

import java.time.LocalDate

class Utils {
    companion object {
        @JvmStatic
        fun generateSchoolYear(date: LocalDate): String {
            // Trenutna godina i mesec iz prosleđenog datuma
            val currentYear = date.year
            val currentMonth = date.monthValue

            // Ako je mesec između oktobra i decembra (10-12), koristi trenutnu godinu i sledeću
            if (currentMonth in 10..12) {
                return "$currentYear/${(currentYear + 1) % 100}"
            }
            // Ako je mesec između januara i septembra (1-9), koristi prethodnu godinu i trenutnu
            return "${currentYear - 1}/${currentYear % 100}"
        }
    }
}

