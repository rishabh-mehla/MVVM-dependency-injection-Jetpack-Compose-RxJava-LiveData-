package com.dummy.app

class CryptocurrencyRepositoryImpl : CryptocurrencyRepository{
    // Overriding the interface method and
    // providing implementation to it
    override fun getCryptoCurrency() = listOf(
        // here we are adding images from wikipedia
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/BTC_Logo.svg/1200px-BTC_Logo.svg.png", "BitCoin"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Ethereum_logo_translucent.svg/1200px-Ethereum_logo_translucent.svg.png", "Ethereum"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/Binance_logo.svg/1920px-Binance_logo.svg.png", "Binance"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/en/d/d0/Dogecoin_Logo.png", "DogeCoin"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Litecoin_Logo.jpg/2048px-Litecoin_Logo.jpg", "LiteCoin"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/5/56/Stellar_Symbol.png", "Stellar"),
        Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/5/59/Polkadot_Logotype_color.png", "Polkadot"),
    )

    override fun addRandomCrypto(): List<Cryptocurrency> {
        val list = getCryptoCurrency().toMutableList()
        list.add(Cryptocurrency("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/BTC_Logo.svg/1200px-BTC_Logo.svg.png", "BitCoin"))
        return list
    }


}