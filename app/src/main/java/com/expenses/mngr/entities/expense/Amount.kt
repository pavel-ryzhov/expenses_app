package com.expenses.mngr.entities.expense

import org.json.JSONObject

class Amount(private val amounts: Map<String, Double>) : Iterable<Map.Entry<String, Double>> {
    constructor(currencies: List<String>) : this(kotlin.run {
        val result = mutableMapOf<String, Double>()
        currencies.forEach { currency ->
            result[currency] = 0.0
        }
        result
    })
    init {
        if (amounts.isEmpty())
            throw IllegalArgumentException("Amounts cannot be empty!")
    }
    fun first() = amounts.entries.first()
    fun getCurrencies() = amounts.keys
    fun has(currency: String) = currency in amounts.keys
    fun get(currency: String) = amounts[currency]!!
    fun getOpt(currency: String) = amounts[currency]
    fun getOpt(currency: String, defaultValue: Double) = amounts[currency] ?: defaultValue
    fun size() = amounts.size
    fun isZero() = first().value == 0.0
    fun asMap() = amounts

    fun toJsonString(): String {
        val json = JSONObject()
        for (entry in amounts.entries)
            json.put(entry.key, entry.value)
        return json.toString()
    }

    override fun iterator() = amounts.iterator()

    override fun toString(): String {
        return amounts.toString()
    }

    companion object {
        fun checkAmountIsNull(amount: Amount? = null, currencies: List<String>) = amount ?: Amount(currencies)
        fun fromJsonString(json: String): Amount{
            val jsonObject = JSONObject(json)
            val result = mutableMapOf<String, Double>()
            for (key in jsonObject.keys())
                result[key] = jsonObject.getDouble(key)
            return Amount(result)
        }
        fun sumOfAmounts(amounts: List<Amount>, currencies: List<String>) = sumOfAmounts(amounts) ?: Amount(currencies)
        fun sumOfAmounts(amounts: List<Amount>): Amount? {
            val result = mutableMapOf<String, Double>()
            val currencies = amounts.firstOrNull()?.getCurrencies() ?: return null
            val amountsArray = DoubleArray(currencies.size)
            for (amount in amounts){
                currencies.forEachIndexed { index, currency ->
                    amountsArray[index] += amount.get(currency)
                }
            }
            currencies.forEachIndexed { index, currency ->
                result[currency] = amountsArray[index]
            }
            return Amount(result)
        }
    }
}