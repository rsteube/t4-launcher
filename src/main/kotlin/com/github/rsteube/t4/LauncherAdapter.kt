package com.github.rsteube.t4

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class LauncherAdapter(context: Context, private val filter: RegexFilter) :
    ArrayAdapter<LauncherAdapter.Launcher>(context, android.R.layout.simple_list_item_1, mutableListOf()) {
    private lateinit var launchers: List<Launcher>

    init {
        reload()
        filter.addObserver { _, _ -> performFiltering() }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        (super.getView(position, convertView, parent) as TextView).apply {
            getItem(position)?.apply {
                text = filter.format(label)
                setOnClickListener { startActivity(pkg) }
                setOnLongClickListener { openAppSettings(pkg); false }
            }
        }

    private fun startActivity(pkg: String) = reloadOnException {
        context.startActivity(context.packageManager.getLaunchIntentForPackage(pkg))
    }

    private fun openAppSettings(pkg: String) = reloadOnException {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$pkg")
        })
    }

    private fun reloadOnException(block: () -> Unit) = try {
        block.invoke()
    } catch (e: Exception) {
        reload()
    }

    private fun performFiltering() {
        launchers.filter { filter.matches(it.label) }.let { filtered ->
            when {
                filtered.isEmpty() -> filter.removeLast()
                else -> {
                    clear()
                    addAll(filtered)
                }
            }
        }
    }

    fun reload() {
        launchers = queryLaunchers()
        performFiltering()
    }

    private fun queryLaunchers() = context.packageManager
        .queryIntentActivities(
            Intent(
                Intent.ACTION_MAIN,
                null
            ).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        .map { Launcher(it.loadLabel(context.packageManager) as String, it.activityInfo.packageName) }
        .filterNot { it.pkg in listOf("com.android.settings", context.packageName) }
        .sortedBy { it.label.toLowerCase() }

    data class Launcher(val label: String, val pkg: String)
}