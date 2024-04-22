package org.sashaiolh.iolhessentials;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.sashaiolh.iolhessentials.Commands.Aliases.AliasCommand;
import org.sashaiolh.iolhessentials.Commands.Aliases.AliasRegistry;
import org.sashaiolh.iolhessentials.Commands.Helpop.HelpopCommand;
import org.sashaiolh.iolhessentials.Commands.Helpop.HelpopConfigManager;
import org.sashaiolh.iolhessentials.SocialSpy.SocialSpyUsersManager;
import org.sashaiolh.iolhessentials.Commands.ReloadCommand;
import org.sashaiolh.iolhessentials.SocialSpy.SocialSpyCommand;
import org.sashaiolh.iolhessentials.SocialSpy.SpyCommandsConfigManager;


@Mod(IolhEssentials.MODID)
public class IolhEssentials
{
    public static final String MODID = "iolhessentials";
    public static ConfigManager configManager;
    public static HelpopConfigManager helpopPexConfigManager;

    public IolhEssentials() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
    }


    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    public static void registerConfigs(){
        configManager = new ConfigManager("config/"+IolhEssentials.MODID+"/IolhEssentials.cfg");

        helpopPexConfigManager = new HelpopConfigManager("config/"+IolhEssentials.MODID+"/pexColors.cfg");

        SpyCommandsConfigManager.init();

        SocialSpyUsersManager.init();

    }


    @SubscribeEvent
    public void onCommandRegistered(RegisterCommandsEvent event) {
        registerConfigs();
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        HelpopCommand.register(dispatcher);
        ReloadCommand.register(dispatcher);
        SocialSpyCommand.register(dispatcher);

        event.getDispatcher().register(AliasCommand.register());
        AliasRegistry.registerAliases(event.getDispatcher());
    }

}