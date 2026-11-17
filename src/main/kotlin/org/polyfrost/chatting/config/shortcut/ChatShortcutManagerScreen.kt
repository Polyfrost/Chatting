@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.chatting.config.shortcut

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.components.CenteredPanel
import org.polyfrost.oneconfig.api.ui.v1.components.CenteredPanelSurface
import org.polyfrost.oneconfig.api.ui.v1.components.ConfirmDialog
import org.polyfrost.oneconfig.api.ui.v1.components.ControlSize
import org.polyfrost.oneconfig.api.ui.v1.components.ControlStyle
import org.polyfrost.oneconfig.api.ui.v1.components.EmptyState
import org.polyfrost.oneconfig.api.ui.v1.components.EscapeCloses
import org.polyfrost.oneconfig.api.ui.v1.components.IconButton
import org.polyfrost.oneconfig.api.ui.v1.components.LabeledField
import org.polyfrost.oneconfig.api.ui.v1.components.MasterDetailLayout
import org.polyfrost.oneconfig.api.ui.v1.components.MenuAction
import org.polyfrost.oneconfig.api.ui.v1.components.MenuSeparator
import org.polyfrost.oneconfig.api.ui.v1.components.PanelHeader
import org.polyfrost.oneconfig.api.ui.v1.components.ScrollContainer
import org.polyfrost.oneconfig.api.ui.v1.components.SectionHeader
import org.polyfrost.oneconfig.api.ui.v1.components.SelectionDefaults
import org.polyfrost.oneconfig.api.ui.v1.components.StatusMessage
import org.polyfrost.oneconfig.api.ui.v1.components.StatusText
import org.polyfrost.oneconfig.api.ui.v1.components.SurfaceKind
import org.polyfrost.oneconfig.api.ui.v1.components.Text
import org.polyfrost.oneconfig.api.ui.v1.components.TextButton
import org.polyfrost.oneconfig.api.ui.v1.components.TextField
import org.polyfrost.oneconfig.api.ui.v1.components.contextMenu
import org.polyfrost.oneconfig.api.ui.v1.components.rememberStatusMessage
import org.polyfrost.oneconfig.api.ui.v1.components.selectableRow
import org.polyfrost.oneconfig.api.ui.v1.components.themedSurface
import org.polyfrost.oneconfig.api.ui.v1.page.OneConfigDestination
import org.polyfrost.oneconfig.api.ui.v1.page.OneConfigPages
import org.polyfrost.oneconfig.api.ui.v1.platform.OneConfigComposeScreen
import org.polyfrost.oneconfig.api.ui.v1.theme.OneConfigTheme

private val PANEL_WIDTH = 980.dp
private val PANEL_MAX_HEIGHT = 640.dp

class ChatShortcutManagerScreen : OneConfigComposeScreen() {

    private var closing = false

    override val closeOnEscape: Boolean get() = false

    override fun init() {
        ChatShortcuts.initialize()
        OneConfigTheme.ensureLoaded()
        super.init()
    }

    private fun back() {
        if (closing) return
        closing = true
        OneConfigUI.open(OneConfigDestination.ModConfig(OneConfigPages.modConfig(ChattingConfig.id, "Shortcuts")))
    }

    @Composable
    override fun Content() {
        EscapeCloses(::back)
        ShortcutManagerContent(onClose = ::back)
    }
}

private data class ShortcutEditorState(
    val originalAlias: String?,
    val originalReplacement: String,
    val alias: String,
    val replacement: String,
) {
    val editingExisting: Boolean get() = originalAlias != null

    fun reset(): ShortcutEditorState = copy(
        alias = originalAlias.orEmpty(),
        replacement = originalReplacement,
    )
}

@Composable
private fun ShortcutManagerContent(onClose: () -> Unit) {
    val theme = OneConfigTheme.current
    var revision by remember { mutableIntStateOf(0) }
    var editor by remember { mutableStateOf<ShortcutEditorState?>(null) }
    var pendingOverwrite by remember { mutableStateOf<ShortcutEditorState?>(null) }
    val status = rememberStatusMessage(0L)

    val shortcuts = remember(revision) { ChatShortcuts.shortcuts.toList() }

    fun refresh() {
        revision++
    }

    fun editShortcut(shortcut: Pair<String, String>) {
        editor = ShortcutEditorState(
            originalAlias = shortcut.first,
            originalReplacement = shortcut.second,
            alias = shortcut.first,
            replacement = shortcut.second,
        )
        status.clear()
    }

    fun newShortcut() {
        editor = ShortcutEditorState(
            originalAlias = null,
            originalReplacement = "",
            alias = "",
            replacement = "",
        )
        status.clear()
    }

    fun deleteShortcut(alias: String) {
        ChatShortcuts.removeShortcut(alias)
        if (editor?.originalAlias == alias) editor = null
        pendingOverwrite = null
        status.show("Deleted \"$alias\".")
        refresh()
    }

    fun saveShortcut(state: ShortcutEditorState, overwrite: Boolean = false) {
        val alias = state.alias.trim()
        val replacement = state.replacement.trim()
        if (alias.isBlank() || replacement.isBlank()) {
            status.show("Alias and replacement are required.", NotificationType.ERROR)
            return
        }

        val originalAlias = state.originalAlias
        val conflicts = ChatShortcuts.shortcuts.any { it.first.equals(alias, ignoreCase = true) && it.first != originalAlias }
        if (conflicts && !overwrite) {
            pendingOverwrite = state.copy(alias = alias, replacement = replacement)
            return
        }

        if (originalAlias != null && originalAlias != alias) {
            ChatShortcuts.removeShortcut(originalAlias)
        }
        ChatShortcuts.writeShortcut(alias, replacement)
        val saved = state.copy(
            originalAlias = alias,
            originalReplacement = replacement,
            alias = alias,
            replacement = replacement,
        )
        editor = saved
        pendingOverwrite = null
        status.show("Saved \"$alias\".", NotificationType.SUCCESS)
        refresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.pageBackground),
    ) {
        CenteredPanel(
            width = PANEL_WIDTH,
            scrollable = false,
            maxHeight = PANEL_MAX_HEIGHT,
            surface = CenteredPanelSurface.Sidebar,
        ) {
            PanelHeader(
                "Chat Shortcuts",
                icon = "text-input",
                subtitle = "Manage the aliases expanded before messages are sent.",
                onClose = onClose,
                primaryAction = { TextButton("New", icon = "plus", onClick = ::newShortcut, style = ControlStyle.Primary) },
            )
            MasterDetailLayout(
                list = {
                    ShortcutList(
                        shortcuts = shortcuts,
                        selectedAlias = editor?.originalAlias,
                        onEdit = ::editShortcut,
                        onDelete = ::deleteShortcut,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
                detail = {
                    ShortcutEditor(
                        state = editor,
                        status = status,
                        onChange = { editor = it; status.clear() },
                        onReset = { editor = it.reset(); status.clear() },
                        onSave = ::saveShortcut,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
                modifier = Modifier.weight(1f),
                listWidth = 380.dp,
                stackBelow = 760.dp,
            )
        }

        pendingOverwrite?.let { pending ->
            ConfirmDialog(
                title = "Overwrite Alias?",
                text = "\"${pending.alias}\" already exists. Saving will replace its current replacement.",
                confirmLabel = "Overwrite",
                onConfirm = { saveShortcut(pending, overwrite = true) },
                onDismiss = { pendingOverwrite = null },
                icon = "tick",
            )
        }
    }
}

@Composable
private fun ShortcutList(
    shortcuts: List<Pair<String, String>>,
    selectedAlias: String?,
    onEdit: (Pair<String, String>) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = OneConfigTheme.current
    Column(
        modifier = modifier
            .themedSurface(SurfaceKind.Card)
            .padding(theme.spacing.l),
        verticalArrangement = Arrangement.spacedBy(theme.spacing.m),
    ) {
        SectionHeader("Saved Aliases", eyebrow = false, count = shortcuts.size)

        if (shortcuts.isEmpty()) {
            EmptyState("No shortcuts saved yet.", Modifier.fillMaxSize(), icon = "text-input")
            return@Column
        }

        val listState = rememberLazyListState()
        ScrollContainer(listState, Modifier.fillMaxSize(), scrollbar = false) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(theme.spacing.s),
            ) {
                items(shortcuts, key = { it.first }) { shortcut ->
                    ShortcutRow(
                        shortcut = shortcut,
                        selected = shortcut.first == selectedAlias,
                        onEdit = { onEdit(shortcut) },
                        onDelete = { onDelete(shortcut.first) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortcutRow(
    shortcut: Pair<String, String>,
    selected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val theme = OneConfigTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .contextMenu {
                listOf(
                    MenuAction("Edit", onEdit, icon = "text-input"),
                    MenuSeparator,
                    MenuAction("Delete", onDelete, icon = "trash", danger = true),
                )
            }
            .selectableRow(selected, onEdit, "/" + shortcut.first)
            .padding(theme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(theme.spacing.m),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(theme.spacing.xxs)) {
            Text(
                "/" + shortcut.first,
                Modifier.fillMaxWidth(),
                color = SelectionDefaults.itemContentColor(selected),
                style = OneConfigTheme.typography.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "/" + shortcut.second,
                Modifier.fillMaxWidth(),
                color = SelectionDefaults.itemSecondaryColor(selected),
                style = OneConfigTheme.typography.small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(
            "trash",
            "Delete",
            onClick = onDelete,
            size = ControlSize.Compact,
            restTint = SelectionDefaults.itemSecondaryColor(selected),
        )
    }
}

@Composable
private fun ShortcutEditor(
    state: ShortcutEditorState?,
    status: StatusMessage,
    onChange: (ShortcutEditorState) -> Unit,
    onReset: (ShortcutEditorState) -> Unit,
    onSave: (ShortcutEditorState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = OneConfigTheme.current
    Column(
        modifier = modifier
            .themedSurface(SurfaceKind.Card)
            .padding(theme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(theme.spacing.l),
    ) {
        Text(
            if (state?.editingExisting == true) "Edit Alias" else "New Alias",
            color = theme.textColor,
            style = OneConfigTheme.typography.heading,
        )

        if (state == null) {
            EmptyState("Select a shortcut or create a new one.", Modifier.fillMaxSize())
            return@Column
        }

        LabeledField("Alias") {
            ShortcutTextField(
                value = state.alias,
                placeholder = "m",
                label = "Alias",
                onValueChange = { onChange(state.copy(alias = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        LabeledField("Replacement") {
            ShortcutTextField(
                value = state.replacement,
                placeholder = "msg",
                label = "Replacement",
                onValueChange = { onChange(state.copy(replacement = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (status.text != null) StatusText(status)

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(theme.spacing.m, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton("Reset", icon = "refresh", onClick = { onReset(state) })
            TextButton("Save", icon = "tick", onClick = { onSave(state) }, style = ControlStyle.Primary)
        }
    }
}

@Composable
private fun ShortcutTextField(
    value: String,
    placeholder: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = OneConfigTheme.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        // the leading slash is a fixed adornment not part of the stored value so players can still type an extra slash for commands like //set
        leading = { Text("/", color = theme.textColorSecondary) },
        hidePlaceholderWhenFocused = true,
    )
}
