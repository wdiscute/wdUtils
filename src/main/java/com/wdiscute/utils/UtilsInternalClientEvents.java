package com.wdiscute.utils;

import com.wdiscute.utils.screen.ItemStackPictureInPictureRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public class UtilsInternalClientEvents
{
    @SubscribeEvent
    private static void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event)
    {
        event.register(ItemStackPictureInPictureRenderer.RenderState.class, ItemStackPictureInPictureRenderer::new);
    }

}
