package com.example.expenses.entities.exchange_rates

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeRate(
    @PrimaryKey
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "value") val value: Float
)