# Modern-feature port checklist

Target: carry the modern upstream Chatting architecture and feature set into
the Fabric (Ornithe) 1.8.9 target, while keeping 1.8.9-specific mixins as
renderer adapters. The modern feature inventory is based on origin/main
(3.1.5 at the time this checklist was created).

Status meanings:

- `[x]` implemented in the Ornithe target and package-build verified.
- `[ ]` not yet ported or not yet verified in a 1.8.9 client.
- `[-]` intentionally excluded from the target.

## Architecture

- [x] Establish modern-style client lifecycle ownership (ChattingClient),
  shared constants, and a feature-specific dimensions module.
- [ ] Port modern configuration descriptions, callbacks, migrations, and
  runtime refresh behavior without exposing options before their feature works.
- [ ] Port modern compatibility reevaluation only where the target mod and API
  actually exist on Fabric (Ornithe).
- [ ] Add focused tests or repeatable client checks for each renderer adapter.

## General chat rendering

- [ ] Chat background color.
- [ ] Hovered message background color.
- [ ] Rounded chat corners.
- [ ] Corner radius.
- [ ] Message fade and time-before-fade.
- [ ] Modern text-rendering behavior.
- [ ] Modern custom chat dimensions: chat width, custom chat width, custom
  chat height, focused height, and unfocused height.
- [x] Modern smooth chat messages, using a 1.8.9 renderer adapter.
- [x] Modern smooth chat scrolling, using a 1.8.9 renderer adapter.
- [x] Remove scroll bar.
- [-] Smooth chat background.
- [-] Open/close chat extension animation ("chat smooth extend").

## Buttons and chat actions

- [ ] Modern button renderer and textures.
- [ ] Button shadow.
- [ ] Extend chat background around buttons.
- [ ] Per-message copy button.
- [ ] Right-click copy and shortcut-key gating.
- [ ] Per-message delete button.
- [ ] Modern clear-history button behavior.
- [ ] Chat screenshot button and screenshot keybind.
- [ ] Chat search UI and filtered-render refresh behavior.

## Chat heads

- [ ] Show chat heads from the modern implementation.
- [ ] Show 3D heads.
- [ ] Chat head shadow modes.
- [ ] Center chat heads.
- [ ] Offset non-player messages.
- [ ] Hide chat heads on consecutive messages.
- [ ] Texture/render compatibility needed by the 1.8.9 player renderer.

## Screenshots

- [ ] Screenshot mode: save, clipboard, or both.
- [ ] Add border.
- [ ] Force shadow.
- [ ] Include background.
- [ ] Native 1.8.9 capture/clipboard implementation and safe AWT handling.

## Tabs, shortcuts, and peek

- [ ] Modern tab model, renderer, refresh behavior, and persistent migration.
- [ ] Modern search model integration with tabs.
- [ ] Modern chat shortcuts persistence and command rewriting.
- [ ] Edit chat shortcuts screen.
- [ ] Modern chat peek behavior, keybind semantics, and scroll reset.

## Optional modern integrations

- [ ] Chat heads compatibility reevaluation.
- [ ] Chat-impressive animation compatibility.
- [ ] Text Tunnels compatibility.
- [ ] Mod Menu / OneConfig integration and chat preview HUD, if their 1.8.9
  APIs support a faithful implementation.

## Verification gate

- [ ] Java 21 Fabric (Ornithe) build.
- [ ] Retained Forge build.
- [ ] In-game client smoke test for every checked feature.
- [ ] Confirm no unported modern option is visible in OneConfig.
- [ ] Re-audit against newer upstream main before release.
