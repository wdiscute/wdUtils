package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.utils.compat.EmiCompat;
import com.wdiscute.utils.compat.JeiCompat;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenUtils
{
    //
    //  ,--.                   ,--.   ,--.   ,--.
    //,-'  '-.  ,---.   ,---.  |  | ,-'  '-. `--'  ,---.   ,---.
    //'-.  .-' | .-. | | .-. | |  | '-.  .-' ,--. | .-. | (  .-'
    //  |  |   ' '-' ' ' '-' ' |  |   |  |   |  | | '-' ' .-'  `)
    //  `--'    `---'   `---'  `--'   `--'   `--' |  |-'  `----'
    //                                            `--'

    public static class Tooltip
    {
        private static List<Component> tooltip = null;
        private static ItemStack stack = null;

        public static void set(ItemStack stack)
        {
            Tooltip.stack = stack;
        }

        public static void set(List<Component> list)
        {
            tooltip = new ArrayList<>(list);
        }

        public static void set(Component comp)
        {
            tooltip = new ArrayList<>(){{add(comp);}};
        }


        public static void addTranslatable(String string)
        {
            if (tooltip == null)
                tooltip = new ArrayList<>();
            tooltip.add(Component.translatable(string));
        }

        public static void addLiteral(String string)
        {
            if (tooltip == null)
                tooltip = new ArrayList<>();
            tooltip.add(Component.literal(string));
        }

        public static void add(Component component)
        {
            if (tooltip == null)
                tooltip = new ArrayList<>();
            tooltip.add(component);
        }

        public static void add(int index, Component component)
        {
            if (tooltip == null)
                tooltip = new ArrayList<>();
            tooltip.add(Math.clamp(index, 0, tooltip.size()), component);
        }

        @SuppressWarnings("unchecked")
        public static void render(GuiGraphicsExtractor guiGraphics, Font font, int x, int y)
        {
            if (stack != null)
                guiGraphics.setTooltipForNextFrame(font, stack, x, y);

            if (tooltip != null)
                guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), x, y);

            tooltip = null;
            stack = null;
        }

        //tooltip
        public static void raw(GuiGraphicsExtractor guiGraphics, Font font, int mouseX, int mouseY, List<Component> components)
        {
            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        //stack tooltip
        public static void raw(GuiGraphicsExtractor guiGraphics, Font font, int mouseX, int mouseY, ItemStack stack)
        {
            guiGraphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
        }
    }


    //
    //               ,--.
    // ,---.  ,---.  |  |  ,---.  ,--.--.
    //| .--' | .-. | |  | | .-. | |  .--'
    //\ `--. ' '-' ' |  | ' '-' ' |  |
    // `---'  `---'  `--'  `---'  `--'
    //

    private static int color = -1;

    public static void setColor(int color)
    {
        ScreenUtils.color = color;
    }

    /**
     * Sets a color to be used on the next render call
     *
     * @since 4.0
     */
    public static void setColorF(float alpha, float red, float green, float blue)
    {

    }

    public static void setColorI(int alpha, int red, int green, int blue)
    {

    }

    public static void resetColor()
    {
        ScreenUtils.color = -1;
        //RenderSystem.setShaderColor(1, 1, 1, 1);
        //RenderSystem.disableBlend();
    }

    private static void setColorInternal()
    {
        //todo color stuff for rendering images
        //RenderSystem.enableBlend();
        //RenderSystem.setShaderColor(
        //        ((color >> 16) & 0xFF) / 255.0f,
        //        ((color >> 8) & 0xFF) / 255.0f,
        //        (color & 0xFF) / 255.0f,
        //        ((color >> 24) & 0xFF) / 255.0f
        //);
    }

    //
    // ,--.
    // `--' ,--,--,--.  ,--,--.  ,---.   ,---.
    // ,--. |        | ' ,-.  | | .-. | | .-. :
    // |  | |  |  |  | \ '-'  | ' '-' ' \   --.
    // `--' `--`--`--'  `--`--' .`-  /   `----'
    //                          `---'
    // just like the famous synth pop album Image by Magdalena Bay
    // feeling disk inserted yet?
    public record Image(Identifier id, int textureWidth, int textureHeight)
    {
        public static final Codec<Image> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("identifier").forGetter(Image::id),
                        Codec.INT.fieldOf("texture_width").forGetter(Image::textureWidth),
                        Codec.INT.fieldOf("texture_height").forGetter(Image::textureHeight)
                ).apply(instance, Image::new));

        public static Codec<Image> codecFixedSize(int textureWidth, int textureHeight)
        {
            return Identifier.CODEC.xmap(
                    id -> new Image(id, textureWidth, textureHeight),
                    Image::id
            );
        }

        public static StreamCodec<ByteBuf, Image> streamCodecFixedSize(int textureWidth, int textureHeight)
        {
            return StreamCodec.composite(
                    Identifier.STREAM_CODEC, Image::id,
                    (o) -> new Image(o, textureWidth, textureHeight));
        }

        public static final StreamCodec<ByteBuf, Image> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Image::id,
                ByteBufCodecs.INT, Image::textureWidth,
                ByteBufCodecs.INT, Image::textureHeight,
                Image::new
        );

        public void render(GuiGraphicsExtractor guiGraphics)
        {
            if (color != -1) setColorInternal();

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                    0, 0,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight
            );

            resetColor();
        }

        public void render(GuiGraphicsExtractor guiGraphics, int x, int y)
        {
            if (color != -1) setColorInternal();

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                    x, y,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight
            );

            resetColor();
        }

        public void render(GuiGraphicsExtractor guiGraphics, int x, int y, float scale)
        {
            if (color != -1) setColorInternal();

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                    x, y,
                    (int) (textureWidth * scale), (int) (textureHeight * scale),
                    0, 0,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight
            );

            resetColor();
        }

        public void render(GuiGraphicsExtractor guiGraphics, int x, int y, int xOffset, int yOffset, int sectionWidth, int sectionHeight)
        {
            if (color != -1) setColorInternal();

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                    x, y,
                    xOffset, yOffset,
                    sectionWidth, sectionHeight,
                    sectionWidth, sectionHeight,
                    textureWidth, textureHeight
            );

            resetColor();
        }

        private void renderColor(GuiGraphicsExtractor guiGraphics, int color)
        {
            if (color != -1) setColorInternal();

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            );

            resetColor();
        }
    }

    public static void image(GuiGraphicsExtractor guiGraphics, Identifier rl, int x, int y, int xOffset, int yOffset, int textureWidth, int textureHeight)
    {
        if (color != -1) setColorInternal();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, rl, x, y, xOffset, yOffset, textureWidth, textureHeight, textureWidth, textureHeight);
        resetColor();
    }

    public static void image(GuiGraphicsExtractor guiGraphics, Identifier rl, int x, int y, int textureWidth, int textureHeight)
    {
        if (color != -1) setColorInternal();
        guiGraphics.blit(rl, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        resetColor();
    }

    //
    // ,--.   ,--.
    // `--' ,-'  '-.  ,---.  ,--,--,--.
    // ,--. '-.  .-' | .-. : |        |
    // |  |   |  |   \   --. |  |  |  |
    // `--'   `--'    `----' `--`--`--'
    //
    public static void item(GuiGraphicsExtractor guiGraphics, ItemStack itemStack, int x, int y)
    {
        guiGraphics.item(itemStack, x, y);
    }

    public static void item(GuiGraphicsExtractor guiGraphics, ItemStack itemStack, int x, int y, Matrix3x2fStack poseStack, float scale)
    {
        poseStack.pushMatrix();
        poseStack.translate(x - 8, y - 8);
        poseStack.scale(scale, scale);
        guiGraphics.item(itemStack, 0, 0);
        poseStack.popMatrix();
    }


    //
    //  ,--.                        ,--.
    //,-'  '-.  ,---.  ,--.  ,--. ,-'  '-.
    //'-.  .-' | .-. :  \  `'  /  '-.  .-'
    //  |  |   \   --.  /  /.  \    |  |
    //  `--'    `----' '--'  '--'   `--'
    //

    public record Text(Component text, int color, boolean shadow)
    {
        public void render(GuiGraphicsExtractor guiGraphics, Font font)
        {
            guiGraphics.text(font, text, 0, 0, color, shadow);
        }

        public void render(GuiGraphicsExtractor guiGraphics, Font font, int x, int y)
        {
            guiGraphics.text(font, text, x, y, color, shadow);
        }
    }

    public static void text(GuiGraphicsExtractor guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow)
    {
        guiGraphics.text(font, text, x, y, color, shadow);
    }

    public static void text(GuiGraphicsExtractor guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow, int sizeToScissor)
    {
        guiGraphics.enableScissor(x - 10, y - 10, x + sizeToScissor, y + 10);
        guiGraphics.text(font, text, x, y, color, shadow);
        guiGraphics.disableScissor();
    }

    public static void text(GuiGraphicsExtractor guiGraphics, Font font, Component text, int x, int y, int color)
    {
        guiGraphics.text(font, text, x, y, color, true);
    }

    public static void text(GuiGraphicsExtractor guiGraphics, Font font, String text, int x, int y, int color, boolean shadow)
    {
        guiGraphics.text(font, text, x, y, color, shadow);
    }

    public static void text(GuiGraphicsExtractor guiGraphics, Font font, String text, int x, int y, int color)
    {
        guiGraphics.text(font, text, x, y, color, true);
    }


    //
    //                               ,--. ,--. ,--.
    // ,---.   ,---. ,--.--.  ,---.  |  | |  | `--' ,--,--,   ,---.
    //(  .-'  | .--' |  .--' | .-. | |  | |  | ,--. |      \ | .-. |
    //.-'  `) \ `--. |  |    ' '-' ' |  | |  | |  | |  ||  | ' '-' '
    //`----'   `---' `--'     `---'  `--' `--' `--' `--''--' .`-  /
    //  ,--.                        ,--.                     `---'
    //,-'  '-.  ,---.  ,--.  ,--. ,-'  '-.
    //'-.  .-' | .-. :  \  `'  /  '-.  .-'
    //  |  |   \   --.  /  /.  \    |  |
    //  `--'    `----' '--'  '--'   `--'
    //
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
            guiGraphics.text(font, text, x, y, color, shadow);
            guiGraphics.disableScissor();
        }
        else
        {
            int i1 = Mth.clamp(minX, minX + i / 2, maxX - i / 2);
            guiGraphics.text(font, text.getVisualOrderText(), i1 - font.width(text.getVisualOrderText()) / 2, y, color, shadow);
        }
    }

    public static void centeredText(GuiGraphicsExtractor guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow)
    {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        guiGraphics.text(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, shadow);
    }

    public static void centeredText(GuiGraphicsExtractor guiGraphics, Font font, String text, int x, int y, int color, boolean shadow)
    {
        FormattedCharSequence formattedcharsequence = Component.literal(text).getVisualOrderText();
        guiGraphics.text(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, shadow);
    }

    public static void outline(GuiGraphicsExtractor guiGraphics, int x, int y, int sizeX, int sizeY, int color)
    {
        guiGraphics.outline(x, y, sizeX, sizeY, color);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, int x, int y, int sizeX, int sizeY, int color)
    {
        guiGraphics.fill(x, y, x + sizeX, y + sizeY, color);
    }

    public static void displayRecipe(ItemStack stack)
    {
        if (ModList.get().isLoaded("emi"))
            EmiCompat.displayRecipes(stack);
        else if (ModList.get().isLoaded("jei"))
            JeiCompat.displayRecipes(stack);
    }

    public static void renderItem(GuiGraphicsExtractor guiGraphics, ItemStack stack, float rotY, float rotX, float rotZ, int xOffset, int yOffset, float scale)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;

        TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();

        Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState, stack,
                ItemDisplayContext.FIXED, player.level(), player, 0);

        guiGraphics.submitPictureInPictureRenderState(new ItemStackPictureInPictureRenderer.RenderState(
                renderState, rotY, rotX, rotZ,
                xOffset, yOffset, 16 * scale));
    }

}
