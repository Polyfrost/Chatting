package org.polyfrost.chatting.gui;

import kotlin.Pair;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.polyfrost.chatting.chat.ChatShortcuts;
import java.util.List;

/**
 * Native 1.8.9 editor for command aliases.  It deliberately uses vanilla GUI
 * widgets so it remains available when OneConfig's compose UI is unavailable.
 */
public final class ChatShortcutManagerScreen extends GuiScreen {
    private static final int LIST_LEFT = 20;
    private static final int LIST_TOP = 48;
    private static final int ROW_HEIGHT = 20;

    private GuiTextField alias;
    private GuiTextField replacement;
    private String originalAlias;
    private String status = "Select an alias or create a new one.";

    @Override
    public void initGui() {
        ChatShortcuts.INSTANCE.initialize();
        int editorLeft = width / 2 + 10;
        alias = new GuiTextField(0, fontRendererObj, editorLeft, 66, width / 2 - 30, 20);
        replacement = new GuiTextField(1, fontRendererObj, editorLeft, 112, width / 2 - 30, 20);
        alias.setMaxStringLength(100);
        replacement.setMaxStringLength(256);
        buttonList.add(new GuiButton(10, editorLeft, 146, 70, 20, "Save"));
        buttonList.add(new GuiButton(11, editorLeft + 76, 146, 70, 20, "Delete"));
        buttonList.add(new GuiButton(12, width - 92, 20, 72, 20, "New"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 10: save(); break;
            case 11: delete(); break;
            case 12: beginNew(); break;
            default: break;
        }
    }

    private void beginNew() {
        originalAlias = null;
        alias.setText("");
        replacement.setText("");
        alias.setFocused(true);
        status = "Enter an alias and its replacement.";
    }

    private void save() {
        String key = alias.getText().trim();
        String value = replacement.getText().trim();
        if (key.isEmpty() || value.isEmpty()) {
            status = "Alias and replacement are required.";
            return;
        }
        List<Pair<String, String>> shortcuts = ChatShortcuts.INSTANCE.getShortcuts();
        boolean collision = false;
        for (Pair<String, String> shortcut : shortcuts) {
            if (shortcut.getFirst().equals(key) && !shortcut.getFirst().equals(originalAlias)) {
                collision = true;
                break;
            }
        }
        if (collision) {
            status = "Alias already exists; edit or delete it first.";
            return;
        }
        if (originalAlias != null && !originalAlias.equals(key)) ChatShortcuts.INSTANCE.removeShortcut(originalAlias);
        ChatShortcuts.INSTANCE.writeShortcut(key, value);
        originalAlias = key;
        status = "Saved /" + key + ".";
    }

    private void delete() {
        if (originalAlias == null) {
            status = "Select an alias to delete.";
            return;
        }
        String deleted = originalAlias;
        ChatShortcuts.INSTANCE.removeShortcut(deleted);
        originalAlias = null;
        alias.setText("");
        replacement.setText("");
        status = "Deleted /" + deleted + ".";
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0 || mouseX < LIST_LEFT || mouseX >= width / 2 - 10 || mouseY < LIST_TOP) return;
        int index = (mouseY - LIST_TOP) / ROW_HEIGHT;
        List<Pair<String, String>> shortcuts = ChatShortcuts.INSTANCE.getShortcuts();
        if (index < 0 || index >= shortcuts.size()) return;
        Pair<String, String> shortcut = shortcuts.get(index);
        originalAlias = shortcut.getFirst();
        alias.setText(shortcut.getFirst());
        replacement.setText(shortcut.getSecond());
        status = "Editing /" + originalAlias + ".";
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
            return;
        }
        if (keyCode == 28 && (alias.isFocused() || replacement.isFocused())) {
            save();
            return;
        }
        alias.textboxKeyTyped(typedChar, keyCode);
        replacement.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        alias.updateCursorCounter();
        replacement.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Chat Shortcuts", width / 2, 20, 0xFFFFFF);
        drawString(fontRendererObj, "Saved Aliases", LIST_LEFT, 34, 0xAAAAAA);
        List<Pair<String, String>> shortcuts = ChatShortcuts.INSTANCE.getShortcuts();
        for (int i = 0; i < shortcuts.size() && LIST_TOP + i * ROW_HEIGHT < height - 24; i++) {
            Pair<String, String> shortcut = shortcuts.get(i);
            int y = LIST_TOP + i * ROW_HEIGHT;
            boolean selected = shortcut.getFirst().equals(originalAlias);
            drawRect(LIST_LEFT, y, width / 2 - 10, y + ROW_HEIGHT - 1, selected ? 0xFF555577 : 0x66333333);
            drawString(fontRendererObj, "/" + shortcut.getFirst(), LIST_LEFT + 5, y + 6, 0xFFFFFF);
        }
        drawString(fontRendererObj, "Alias", width / 2 + 10, 52, 0xAAAAAA);
        drawString(fontRendererObj, "Replacement", width / 2 + 10, 98, 0xAAAAAA);
        alias.drawTextBox();
        replacement.drawTextBox();
        drawString(fontRendererObj, status, width / 2 + 10, 178, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
