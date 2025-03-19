package net.stakemetrics.application.entities.exceptions

import net.stakemetrics.application.entities.FifaMatch
import java.util.Date

class OddTooBigException(lineName: String, oddThreshold: Double, fifaMatch: FifaMatch) :
    IllegalArgumentException("$lineName odd cannot be greater than $oddThreshold: Now is ${Date()} and match time is ${fifaMatch.time}")