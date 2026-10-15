package org.polyfrost.chatting.compat;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.resources.Identifier;

public final class GuiGraphics {
    private final PoseStack pose = new PoseStack();
    private float red = 1f;
    private float green = 1f;
    private float blue = 1f;
    private float alpha = 1f;

    public PoseStack pose() {
        return pose;
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        GuiElement.fill(x1, y1, x2, y2, color);
    }

    public void fillGradient(int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
        Gradient.INSTANCE.fill(x1, y1, x2, y2, colorTop, colorBottom);
    }

    public int drawString(Font font, String text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        return font.draw(text, x, y, color, shadow);
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void blit(Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        blit(texture, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public void blit(Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        Minecraft.getInstance().getTextureManager().bind(texture);
        GlStateManager.enableBlend();
        GlStateManager.color4f(red, green, blue, alpha);
        GuiElement.drawTexture(x, y, u, v, regionWidth, regionHeight, width, height, textureWidth, textureHeight);
    }

    private static final class Gradient extends GuiElement {
        private static final Gradient INSTANCE = new Gradient();

        void fill(int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
            fillGradient(x1, y1, x2, y2, colorTop, colorBottom);
        }
    }
}
*///?}
