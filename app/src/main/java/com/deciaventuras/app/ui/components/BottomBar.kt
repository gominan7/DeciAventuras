package com.deciaventuras.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.deciaventuras.app.R

enum class DeciAventurasTab { MAP, JOURNAL }

@Composable
fun DeciAventurasBottomBar(
    selectedTab: DeciAventurasTab,
    onSelectMap: () -> Unit,
    onSelectJournal: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == DeciAventurasTab.MAP,
            onClick = onSelectMap,
            icon = { Icon(Icons.Filled.Explore, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_map)) },
        )
        NavigationBarItem(
            selected = selectedTab == DeciAventurasTab.JOURNAL,
            onClick = onSelectJournal,
            icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_journal)) },
        )
    }
}
