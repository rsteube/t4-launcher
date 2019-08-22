package com.github.rsteube.t4

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.Space

class T4Launcher : Activity() {
    private lateinit var adapter: LauncherAdapter
    private val filter = RegexFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onResume() {
        super.onResume()
        filter.clear()
    }
}
