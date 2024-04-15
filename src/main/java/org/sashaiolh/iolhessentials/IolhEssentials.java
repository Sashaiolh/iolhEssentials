package org.sashaiolh.iolhessentials;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.sashaiolh.iolhessentials.Commands.Aliases.AliasCommand;
import org.sashaiolh.iolhessentials.Commands.Aliases.AliasRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IolhEssentials.MODID)
public class IolhEssentials
{
    public static final String MODID = "iolhessentials";

    public IolhEssentials() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
    }


    @SubscribeEvent
    public void onCommandRegistered(RegisterCommandsEvent event) {
        event.getDispatcher().register(AliasCommand.register());
        AliasRegistry.registerAliases(event.getDispatcher());
    }

}