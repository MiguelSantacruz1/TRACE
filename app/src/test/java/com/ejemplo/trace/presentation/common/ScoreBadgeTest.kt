package com.ejemplo.trace.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias de la lógica semántica de niveles de TRACE SCORE.
 */
class ScoreBadgeTest {

    @Test
    fun verifyScoreToLevel_nullReturnsUnknown() {
        assertEquals(ScoreLevel.UNKNOWN, scoreToLevel(null))
    }

    @Test
    fun verifyScoreToLevel_criticalRange() {
        assertEquals(ScoreLevel.CRITICAL, scoreToLevel(0))
        assertEquals(ScoreLevel.CRITICAL, scoreToLevel(15))
        assertEquals(ScoreLevel.CRITICAL, scoreToLevel(20))
    }

    @Test
    fun verifyScoreToLevel_poorRange() {
        assertEquals(ScoreLevel.POOR, scoreToLevel(21))
        assertEquals(ScoreLevel.POOR, scoreToLevel(35))
        assertEquals(ScoreLevel.POOR, scoreToLevel(40))
    }

    @Test
    fun verifyScoreToLevel_moderateRange() {
        assertEquals(ScoreLevel.MODERATE, scoreToLevel(41))
        assertEquals(ScoreLevel.MODERATE, scoreToLevel(50))
        assertEquals(ScoreLevel.MODERATE, scoreToLevel(60))
    }

    @Test
    fun verifyScoreToLevel_goodRange() {
        assertEquals(ScoreLevel.GOOD, scoreToLevel(61))
        assertEquals(ScoreLevel.GOOD, scoreToLevel(70))
        assertEquals(ScoreLevel.GOOD, scoreToLevel(80))
    }

    @Test
    fun verifyScoreToLevel_excellentRange() {
        assertEquals(ScoreLevel.EXCELLENT, scoreToLevel(81))
        assertEquals(ScoreLevel.EXCELLENT, scoreToLevel(95))
        assertEquals(ScoreLevel.EXCELLENT, scoreToLevel(100))
    }

    @Test
    fun verifyScoreToLevel_outOfBoundsReturnsUnknown() {
        assertEquals(ScoreLevel.UNKNOWN, scoreToLevel(-1))
        assertEquals(ScoreLevel.UNKNOWN, scoreToLevel(101))
    }
}
