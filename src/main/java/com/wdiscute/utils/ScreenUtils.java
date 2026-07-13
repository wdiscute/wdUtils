package com.wdiscute.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class ScreenUtils
{
    public static void renderCenteredScrollingString(GuiGraphicsExtractor guiGraphics, Font font, String text, int centerX, int minX, int maxX, int y, int color)
    {
        renderCenteredScrollingString(guiGraphics, font, Component.literal(text), centerX, minX, maxX, y, color, false);
    }

    public static void renderCenteredScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int centerX, int minX, int maxX, int y, int color)
    {
        renderCenteredScrollingString(guiGraphics, font, text, centerX, minX, maxX, y, color, false);
    }

    public static void renderCenteredScrollingString(GuiGraphicsExtractor guiGraphics, Font font, String text, int centerX, int minX, int maxX, int y, int color, boolean shadow)
    {
        renderCenteredScrollingString(guiGraphics, font, Component.literal(text), centerX, minX, maxX, y, color, shadow);
    }

    public static void renderCenteredScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int centerX, int minX, int maxX, int y, int color, boolean shadow)
    {
        int i = font.width(text);
        int k = maxX - minX;
        if (i > k)
        {
            int l = i - k;
            double d0 = (double) Util.getMillis() / (double) 300.0F;
            double d1 = Math.max((double) l * (double) 0.5F, 3.0F);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / (double) 2.0F + (double) 0.5F;
            double d3 = Mth.lerp(d2, 0.0F, l);
            guiGraphics.enableScissor(minX, y - 10, maxX, y + 10);
            int x = minX - (int) d3;
            guiGraphics.text(font, text, x, y, color, shadow);
            guiGraphics.disableScissor();
        }
        else
        {
            int i1 = Mth.clamp(centerX, minX + i / 2, maxX - i / 2);
            guiGraphics.text(font, text.getVisualOrderText(), i1 - font.width(text.getVisualOrderText()) / 2, y, color, shadow);
        }
    }

    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int minX, int maxX, int y, int color, boolean shadow)
    {
        renderScrollingString(guiGraphics, font, text, minX, maxX, y, color, shadow, 300);
    }

    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int minX, int maxX, int y, int color, boolean shadow, int scrollingSpeed)
    {
        boolean hovering = true;

        int i = font.width(text);
        int k = maxX - minX;
        if (i > k)
        {
            int l = i - k;
            double d0 = (double) Util.getMillis() / (double) scrollingSpeed;
            double d1 = Math.max((double) l * (double) 0.5F, 3.0F);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / (double) 2.0F + (double) 0.5F;
            double d3 = Mth.lerp(d2, 0.0F, l);
            guiGraphics.enableScissor(minX, y - 20, maxX, y + 20);
            int x = minX - (int) d3;
            if (!hovering) x = minX;
            guiGraphics.text(font, text, x, y, color, shadow);
            guiGraphics.disableScissor();
        }
        else
        {
            int i1 = Mth.clamp(minX, minX + i / 2, maxX - i / 2);
            guiGraphics.text(font, text.getVisualOrderText(), i1 - font.width(text.getVisualOrderText()) / 2, y, color, shadow);
        }
    }


}
