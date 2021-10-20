package com.github.rsteube.t4

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.Space
import java.util.*

class T4Launcher : Activity() {
    private lateinit var adapter: LauncherAdapter
    private val filter = RegexFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val config = resources.configuration
        val lang = "zh" // your language code
        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale)
        else
            config.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)





        adapter = LauncherAdapter(this, filter)
        setContentView(
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                addView(ListView(this@T4Launcher).apply {
                    layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 2f)
                    isVerticalScrollBarEnabled = false
                    id = android.R.id.list
                    divider = null
                    adapter = this@T4Launcher.adapter
                })
                addView(Space(this@T4Launcher).apply { layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, 50) })
                addView(
                    LinearLayout(this@T4Launcher).apply {
                        RegexFilter.Pattern.values().forEach { pattern ->
                            addView(Button(this@T4Launcher).apply {
                                layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 2f).apply {
                                    setMargins(10, 10, 10, 10)
                                }
                                text = pattern.name
                                setOnClickListener { filter.add(pattern) }
                                setTextColor(Color.WHITE)
                                setBackgroundResource(R.drawable.button)
                            })
                        }
                    }
                )
            }
        )
    }

    override fun onBackPressed() {
        filter.removeLast()
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?) =
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> true.also { adapter.reload() }
            else -> super.onKeyLongPress(keyCode, event)
        }

    override fun onResume() {
        super.onResume()
        filter.clear()
    }
}
