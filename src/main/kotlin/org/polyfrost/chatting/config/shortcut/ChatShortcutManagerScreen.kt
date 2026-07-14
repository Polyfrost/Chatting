package org.polyfrost.chatting.config.shortcut

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.oneconfig.internal.ui.compose.ComposeScreen
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.Theme
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry

class ChatShortcutManagerScreen : ComposeScreen() {

    private var closing = false

    override fun init() {
        ChatShortcuts.initialize()
        ThemeRegistry.init()
        ThemeRegistry.loadFromConfig()
        super.init()
    }

    override fun onClose() {
        if (closing) return
        closing = true
        Platform.screen().display(OneConfigUIScreen(ChattingConfig.id, "Shortcuts"))
    }

    override fun removed() {
        super.onClose()
        super.removed()
    }

    @Composable
    override fun compose() {
        Theme {
            ShortcutManagerContent(onClose = ::onClose)
        }
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
    val theme = LocalTheme.current
    var revision by remember { mutableIntStateOf(0) }
    var editor by remember { mutableStateOf<ShortcutEditorState?>(null) }
    var pendingOverwrite by remember { mutableStateOf<ShortcutEditorState?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

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
        message = null
    }

    fun newShortcut() {
        editor = ShortcutEditorState(
            originalAlias = null,
            originalReplacement = "",
            alias = "",
            replacement = "",
        )
        message = null
    }

    fun deleteShortcut(alias: String) {
        ChatShortcuts.removeShortcut(alias)
        if (editor?.originalAlias == alias) editor = null
        pendingOverwrite = null
        message = "Deleted \"$alias\"."
        refresh()
    }

    fun saveShortcut(state: ShortcutEditorState, overwrite: Boolean = false) {
        val alias = state.alias.trim()
        val replacement = state.replacement.trim()
        if (alias.isBlank() || replacement.isBlank()) {
            message = "Alias and replacement are required."
            return
        }

        val originalAlias = state.originalAlias
        val conflicts = ChatShortcuts.shortcuts.any { it.first == alias && it.first != originalAlias }
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
        message = "Saved \"$alias\"."
        refresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.pageBackground)
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 980.dp)
                .heightIn(max = 640.dp)
                .clip(theme.backgroundShape)
                .background(theme.sidebarBackground, theme.backgroundShape)
                .border(1.dp, theme.borderColor, theme.backgroundShape)
                .padding(20.dp),
        ) {
            val compact = maxWidth < 760.dp
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Header(onNew = ::newShortcut, onClose = onClose)
                if (compact) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        ShortcutList(
                            shortcuts = shortcuts,
                            selectedAlias = editor?.originalAlias,
                            onEdit = ::editShortcut,
                            onDelete = ::deleteShortcut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),
                        )
                        ShortcutEditor(
                            state = editor,
                            message = message,
                            onChange = { editor = it; message = null },
                            onReset = { editor = it.reset(); message = null },
                            onSave = ::saveShortcut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        ShortcutList(
                            shortcuts = shortcuts,
                            selectedAlias = editor?.originalAlias,
                            onEdit = ::editShortcut,
                            onDelete = ::deleteShortcut,
                            modifier = Modifier
                                .width(380.dp)
                                .fillMaxHeight(),
                        )
                        ShortcutEditor(
                            state = editor,
                            message = message,
                            onChange = { editor = it; message = null },
                            onReset = { editor = it.reset(); message = null },
                            onSave = ::saveShortcut,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                }
            }
        }

        pendingOverwrite?.let { pending ->
            ConfirmOverwriteDialog(
                alias = pending.alias,
                onConfirm = { saveShortcut(pending, overwrite = true) },
                onCancel = { pendingOverwrite = null },
            )
        }
    }
}

@Composable
private fun Header(onNew: () -> Unit, onClose: () -> Unit) {
    val theme = LocalTheme.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon("text-input", color = Accent, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Chat Shortcuts", color = theme.textColor, fontSize = 22.sp, fontWeight = FontWeight.Medium)
            Text("Manage the aliases expanded before messages are sent.", color = theme.textColorSecondary, fontSize = 13.sp)
        }
        ActionButton("New", icon = "plus", onClick = onNew)
        IconAction("close", onClick = onClose)
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
    val theme = LocalTheme.current
    Column(
        modifier = modifier
            .clip(theme.modCardShape)
            .background(theme.modCardBackground, theme.modCardShape)
            .border(1.dp, theme.borderColor, theme.modCardShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Saved Aliases", color = theme.textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Text(shortcuts.size.toString(), color = theme.textColorSecondary, fontSize = 13.sp)
        }

        if (shortcuts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No shortcuts saved yet.", color = theme.textColorSecondary, fontSize = 14.sp)
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
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

@Composable
private fun ShortcutRow(
    shortcut: Pair<String, String>,
    selected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = when {
        selected -> Accent.copy(alpha = 0.22f)
        hovered -> theme.componentBackground
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(theme.sideBarNavigationEntryShape)
            .background(background, theme.sideBarNavigationEntryShape)
            .onClick(interactionSource, onEdit)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            EllipsizedText("/" + shortcut.first, color = theme.textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            EllipsizedText("/" + shortcut.second, color = theme.textColorSecondary, fontSize = 12.sp)
        }
        IconAction("trash", onClick = onDelete, subtle = true)
    }
}

@Composable
private fun ShortcutEditor(
    state: ShortcutEditorState?,
    message: String?,
    onChange: (ShortcutEditorState) -> Unit,
    onReset: (ShortcutEditorState) -> Unit,
    onSave: (ShortcutEditorState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    Column(
        modifier = modifier
            .clip(theme.modCardShape)
            .background(theme.modCardBackground, theme.modCardShape)
            .border(1.dp, theme.borderColor, theme.modCardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (state?.editingExisting == true) "Edit Alias" else "New Alias",
                color = theme.textColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.weight(1f))
        }

        if (state == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Select a shortcut or create a new one.", color = theme.textColorSecondary, fontSize = 14.sp)
            }
            return@Column
        }

        LabeledField("Alias") {
            ShortcutTextField(
                value = state.alias,
                placeholder = "m",
                onValueChange = { onChange(state.copy(alias = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        LabeledField("Replacement") {
            ShortcutTextField(
                value = state.replacement,
                placeholder = "msg",
                onValueChange = { onChange(state.copy(replacement = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        message?.let {
            Text(it, color = theme.textColorSecondary, fontSize = 13.sp)
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionButton("Reset", icon = "refresh", subtle = true, onClick = { onReset(state) })
            ActionButton("Save", icon = "tick", onClick = { onSave(state) })
        }
    }
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    val theme = LocalTheme.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = theme.textColorSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        content()
    }
}

@Composable
private fun ShortcutTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val focused by interactionSource.collectIsFocusedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor = when {
        focused -> Accent
        hovered -> theme.textColorSecondary
        else -> theme.borderColor
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = theme.textColor,
            fontSize = 14.sp,
            fontFamily = theme.typography.family,
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(theme.textColor),
        modifier = modifier
            .clip(theme.sideBarNavigationEntryShape)
            .background(theme.componentBackground, theme.sideBarNavigationEntryShape)
            .border(1.dp, borderColor, theme.sideBarNavigationEntryShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        decorationBox = { innerTextField ->
            // The leading slash is a fixed adornment, not part of the stored value: commands always
            // begin with one, and rendering it here keeps players from doubling it up by accident
            // while still letting them type extra slashes (e.g. `//set` for WorldEdit).
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("/", color = theme.textColorSecondary, fontSize = 14.sp)
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && !focused) {
                        Text(placeholder, color = theme.textColorSecondary, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun ConfirmOverwriteDialog(
    alias: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val theme = LocalTheme.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .clip(theme.popupShape)
                .background(theme.popupBackground, theme.popupShape)
                .border(1.dp, theme.borderColor, theme.popupShape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Overwrite Alias?", color = theme.textColor, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(
                "\"$alias\" already exists. Saving will replace its current replacement.",
                color = theme.textColorSecondary,
                fontSize = 13.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            ) {
                ActionButton("Cancel", subtle = true, onClick = onCancel)
                ActionButton("Overwrite", icon = "tick", onClick = onConfirm)
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: String? = null,
    subtle: Boolean = false,
    onClick: () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = if (subtle) {
        if (hovered) theme.componentBackground else Color.Transparent
    } else {
        if (hovered) Accent.copy(alpha = 0.75f) else Accent
    }
    val foreground = if (subtle) theme.textColor else theme.accentTextColor

    Row(
        modifier = Modifier
            .clip(theme.buttonShape)
            .background(background, theme.buttonShape)
            .border(if (subtle) 1.dp else 0.dp, if (subtle) theme.borderColor else Color.Transparent, theme.buttonShape)
            .onClick(interactionSource, onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        icon?.let { Icon(it, color = foreground, modifier = Modifier.size(14.dp)) }
        Text(label, color = foreground, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun IconAction(
    icon: String,
    subtle: Boolean = false,
    onClick: () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = if (hovered && !subtle) theme.componentBackground else Color.Transparent
    val iconColor = if (hovered) theme.textColor else theme.textColorSecondary

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(theme.buttonShape)
            .background(background, theme.buttonShape)
            .onClick(interactionSource, onClick)
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, color = iconColor, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun EllipsizedText(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    val theme = LocalTheme.current
    BasicText(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontFamily = theme.typography.family,
            fontWeight = fontWeight,
        ),
    )
}
