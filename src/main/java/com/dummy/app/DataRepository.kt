package com.dummy.app

interface CryptocurrencyRepository {
    fun getCryptoCurrency(): List<Cryptocurrency>

    fun addRandomCrypto(): List<Cryptocurrency>
}