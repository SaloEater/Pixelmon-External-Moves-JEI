package com.saloeater.pixelmonjei;

import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "pixelmonjei")
public class Events {
    @SubscribeEvent
    public static void renderTooltip(RenderTooltipEvent event) {
        int a = 1;
    }
}
