package com.lukaszgalinski.gamefuture.repositories.network

import com.lukaszgalinski.gamefuture.models.ShopPricesModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.NullPointerException

private const val ULTIMA_CLASSNAME = "product-price"
private const val G2A_CLASSNAME = "price-new"
private const val EKEY_CLASSNAME = "price"
private const val EMPTY_ELEMENT = "Not known"

fun getShopPrices(shopLinks: ShopPricesModel): List<String?> {
    val shopPrices = ArrayList<String?>()
    shopPrices.add(priceUltima(shopLinks.shopUltima))
    shopPrices.add(priceG2A(shopLinks.morele))
    shopPrices.add(priceEKey(shopLinks.eKey))

    return shopPrices
}

private fun priceUltima(url: String): String? {
    var element = EMPTY_ELEMENT
    try {
        val doc: Document = Jsoup.connect(url).get()
        element = doc.getElementsByClass(ULTIMA_CLASSNAME).first().text()
    } catch (e: NullPointerException) {
        return element
    }
    return element
}

private fun priceG2A(url: String): String? {
    var element = EMPTY_ELEMENT
    try {
        val doc: Document = Jsoup.connect(url).get()
        element = doc.getElementsByClass(G2A_CLASSNAME).first().text()
    } catch (e: NullPointerException) {
        return element
    }
    return element
}

private fun priceEKey(url: String): String? {
    var element = EMPTY_ELEMENT
    try {
        val doc: Document = Jsoup.connect(url).get()
        element = doc.getElementsByClass(EKEY_CLASSNAME).first().text()
    } catch (e: NullPointerException) {
        return element
    }
    return element
}