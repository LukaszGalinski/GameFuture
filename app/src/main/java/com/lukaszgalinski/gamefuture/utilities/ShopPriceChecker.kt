package com.lukaszgalinski.gamefuture.utilities

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

private const val ULTIMA_CLASSNAME = "product-price"

fun priceUltima(url: String): String?{
    val doc: Document = Jsoup.connect(url).get()
    return doc.getElementsByClass(ULTIMA_CLASSNAME).first().text()
}