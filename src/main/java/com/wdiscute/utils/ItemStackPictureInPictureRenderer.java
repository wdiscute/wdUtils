package com.wdiscute.utils;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

//thank you XFactHD on Neoforge's Discord
public final class ItemStackPictureInPictureRenderer extends PictureInPictureRenderer<ItemStackPictureInPictureRenderer.RenderState>
{
    private Object lastModelIdentity = null;
    private float lastRotY = 0;
    private float lastRotX = 0;
    private float lastRotZ = 0;
    private float lastScale = 0;
    private float lastX1 = 0;
    private float lastY1 = 0;

    @Override
    protected void renderToTexture(RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector)
    {
        TrackingItemStackRenderState renderState = state.renderState;

        renderState.setOversizedInGui(true);

        poseStack.scale(1, -1, -1);

        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotZ));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.rotX));

        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        renderState.submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        lastModelIdentity = renderState.getModelIdentity();
        lastRotY = state.rotY;
        lastRotZ = state.rotZ;
        lastRotX = state.rotX;
        lastScale = state.scale;
        lastX1 = state.x1;
        lastY1 = state.y1;
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2F;
    }

    @Override
    protected boolean textureIsReadyToBlit(RenderState state)
    {
        if (state.rotY != lastRotY
            || state.rotZ != lastRotZ
            || state.rotX != lastRotX
            || state.x1 != lastX1
            || state.y1 != lastY1
            || state.scale != lastScale
        )
            return false;

        TrackingItemStackRenderState renderState = state.renderState;
        return !renderState.isAnimated() && renderState.getModelIdentity().equals(lastModelIdentity);
    }

    @Override
    protected String getTextureLabel()
    {
        return "ItemStack PIP Renderer";
    }

    @Override
    public Class<RenderState> getRenderStateClass()
    {
        return RenderState.class;
    }

    public record RenderState(
            TrackingItemStackRenderState renderState,
            float rotY,
            float rotX,
            float rotZ,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            ScreenRectangle bounds,
            ScreenRectangle scissorArea
    ) implements PictureInPictureRenderState
    {
        public RenderState(
                TrackingItemStackRenderState renderState,
                float rotY,
                float rotX,
                float rotZ,
                int xOffset,
                int yOffset,
                float scale
        )
        {
            this(renderState, rotY, rotX, rotZ,
                    0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth() + xOffset,
                    Minecraft.getInstance().getWindow().getGuiScaledHeight() + yOffset,
                    scale,
                    new ScreenRectangle(0, 0, 0, 0),
                    new ScreenRectangle(0, 0, 11111, 1111)
            );
        }
    }
}
