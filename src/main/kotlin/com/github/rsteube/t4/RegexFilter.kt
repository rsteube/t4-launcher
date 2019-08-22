package com.github.rsteube.t4

import android.text.Html
import java.util.*

class RegexFilter : Observable() {
    private val p = mutableListOf<String>()
    private var filter = p.toRegex()

    private fun update() {
        filter = p.toRegex()
        setChanged()
        notifyObservers()
    }

    fun add(pattern: Pattern) {
        p.add(pattern.value)
        update()
    }

    fun removeLast() {
        if (p.isNotEmpty()) {
            p.removeAt(p.size - 1)
        }
        update()
    }

    fun clear() {
        p.clear()
        update()
    }

    fun matches(input: CharSequence) = filter.pattern.isEmpty() || filter.containsMatchIn(input)

    fun format(input: CharSequence) = Html.fromHtml(
        filter.replace(input) { "<font color=\"#33b5e5\">${it.value}</font>" }
    )!!

    enum class Pattern {
        A_F, G_L, M_R, S_Z;

        val value = with(name.replace('_', '-')) {
            "([${toLowerCase()}]|[${toUpperCase()}])\\W*"
        }
    }

    private fun <Pattern> Collection<Pattern>.toRegex() = Regex(this.joinToString(""))
}