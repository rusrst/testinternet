package com.example.testinternet.data

import android.text.Editable
import android.util.Log

class ZenParser {
    val dataItem: MutableList<DataItem> = mutableListOf()
    val textList: MutableList<String> = mutableListOf()
    fun GetZenList(str: String): MutableList<DataItem> {
        var text = str.substringAfter("zen-row-", "NULL")
        do {
            var textSize = text.length
            var string = text.substringBefore("zen-row-")
            if (textSize != string.length) {
                textList.add(string)
            }
            text = text.substringAfter("zen-row-")
        } while (textSize != text.length)
        var string = text.substringBefore("class=\"desktop-interview-modal\"")
        textList.add(string)
        textList.forEach {
            val item = DataItem()
            var txt = it.substringAfter("class=\"card-title-clamp\"")
            txt = txt.substringBefore("a>")
            item.urlPaper = txt.substringAfter("href=\"")
            item.urlPaper = item.urlPaper.substringBefore("\" target=\"_blank\">")
            item.header = txt.substringAfter("\" target=\"_blank\">")
            item.header = item.header.substringBefore("</").replace("&quot;", "\"")
            item.urlImage = it.substringAfter("<div class=\"card-layer-image-view__image\" style=\"background-image:url(")
            item.urlImage = item.urlImage.substringBefore(");")
            item.text = it.substringAfter("class=\"card-layer-snippet-view _theme_white\"><div class=\"zen-ui-line-clamp\"><div class=\"zen-ui-line-clamp__text\">")
            item.text = item.text.substringBefore("</div>").replace("&quot;", "\"")
            if (item.header != "Read full text of User Agreement" && item.header != "Читать полный текст пользовательского соглашения") {
                dataItem.add(item)
            }
        }
        return dataItem
    }
    fun GetCorrectURL (url: String): String{
        return url.substringBefore('?')
    }
    fun GetZenAutor(str: String, link: String): AuthorItem {
        var authorItem = AuthorItem()
        var text = str.substringAfter("class=\"desktop-channel-3-top__logo\"", "NULL")
        if (text != "NULL"){
            text = text.substringAfter("content=\"", "NULL")
            authorItem.urlImage = text.substringBefore("\"/>")
        }
        else authorItem.urlImage = "NULL"
        text = str.substringAfter("desktop-channel-3-title ", "NULL")
        if (text == "NULL") {
            authorItem.apply {
                descriptor = "NULL0"
                title = "NULL0"
                url = "NULL0"
                urlImage = "NULL0"
            }
            return authorItem
        }
        text = text.substringAfter("class=\"desktop-channel-3-title _size_s\"><span>", "NULL")
        if (text == "NULL") {
            authorItem.apply {
                descriptor = "NULL"
                title = "NULL"
                url = "NULL"
                urlImage = "NULL"
            }
            return authorItem
        }
        authorItem.title = text.substringBefore("</span>")
        if (text.contains("last-word\">")){
            text = text.substringAfter("last-word\">")
            authorItem.title += text.substringBefore("<!")
        }
        text = text.substringAfter("class=\"desktop-channel-3-description\">", "NULL")
        if (text == "NULL") {
            authorItem.apply {
                descriptor = "NULL"
                url = "NULL"
                urlImage = "NULL"
            }
            return authorItem
        }
        authorItem.descriptor = text.substringBefore("</").replace("<br/>", "\n")
        authorItem.url = link
        return authorItem
    }
}
