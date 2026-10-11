package core

import kotlin.math.*

data class StemBranch(val stem: String, val branch: String) {
    override fun toString(): String = "$stem-$branch"
}

object ZiWeiCalculator {
    private val heavenlyStems = listOf(
        "Jia", "Yi", "Bing", "Ding", "Wu",
        "Ji", "Geng", "Xin", "Ren", "Gui"
    )
    private val earthlyBranches = listOf(
        "Zi", "Chou", "Yin", "Mao", "Chen", "Si",
        "Wu", "Wei", "Shen", "You", "Xu", "Hai"
    )

    /** Compute the heavenly stem index (0‑9) for a Gregorian year. */
    private fun yearStemIndex(year: Int): Int = ((year - 4) % 10 + 10) % 10

    /** Compute the earthly branch index (0‑11) for a Gregorian year. */
    private fun yearBranchIndex(year: Int): Int = ((year - 4) % 12 + 12) % 12

    /** Simple deterministic month stem index based on year stem and month number (1‑12). */
    private fun monthStemIndex(yearStemIdx: Int, month: Int): Int {
        // Traditional rule: monthStem = (yearStem*2 + month) mod 10
        return (yearStemIdx * 2 + month) % 10
    }

    /** Month earthly branch follows the fixed sequence starting with Yin for month 1. */
    private fun monthBranchIndex(month: Int): Int = (month + 1) % 12

    /** Deterministic day stem index using a linear combination of Y,M,D. */
    private fun dayStemIndex(year: Int, month: Int, day: Int): Int {
        return (year * 5 + month * 3 + day) % 10
    }

    /** Deterministic day branch index using a linear combination of Y,M,D. */
    private fun dayBranchIndex(year: Int, month: Int, day: Int): Int {
        return (year * 7 + month * 5 + day) % 12
    }

    /** Hour earthly branch index: each Chinese hour = 2 modern hours, starting at Zi (23‑01). */
    private fun hourBranchIndex(hour: Int): Int {
        // hour 23‑0 => Zi (0), 1‑2 => Chou (1), …
        val adjusted = (hour + 1) % 24
        return (adjusted / 2) % 12
    }

    /** Hour stem index derived from day stem and hour branch. */
    private fun hourStemIndex(dayStemIdx: Int, hourBranchIdx: Int): Int {
        // Traditional rule: hourStem = (dayStem*2 + hourBranch) mod 10
        return (dayStemIdx * 2 + hourBranchIdx) % 10
    }

    /** Public API – compute the four pillar (year, month, day, hour) as Stem‑Branch pairs. */
    fun computeFourPillars(year: Int, month: Int, day: Int, hour: Int): Map<String, StemBranch> {
        require(month in 1..12) { "Month must be 1..12" }
        require(day in 1..31) { "Day must be 1..31" }
        require(hour in 0..23) { "Hour must be 0..23" }

        val yStemIdx = yearStemIndex(year)
        val yBranchIdx = yearBranchIndex(year)
        val yearPillar = StemBranch(heavenlyStems[yStemIdx], earthlyBranches[yBranchIdx])

        val mStemIdx = monthStemIndex(yStemIdx, month)
        val mBranchIdx = monthBranchIndex(month)
        val monthPillar = StemBranch(heavenlyStems[mStemIdx], earthlyBranches[mBranchIdx])

        val dStemIdx = dayStemIndex(year, month, day)
        val dBranchIdx = dayBranchIndex(year, month, day)
        val dayPillar = StemBranch(heavenlyStems[dStemIdx], earthlyBranches[dBranchIdx])

        val hBranchIdx = hourBranchIndex(hour)
        val hStemIdx = hourStemIndex(dStemIdx, hBranchIdx)
        val hourPillar = StemBranch(heavenlyStems[hStemIdx], earthlyBranches[hBranchIdx])

        return mapOf(
            "Year" to yearPillar,
            "Month" to monthPillar,
            "Day" to dayPillar,
            "Hour" to hourPillar
        )
    }
}