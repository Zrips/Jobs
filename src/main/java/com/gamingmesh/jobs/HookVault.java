package com.gamingmesh.jobs;

import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.plugin.RegisteredServiceProvider;

public abstract class HookVault<T> {
    private static Boolean vaultEnable = null;
    protected Class<T> providerClass;
    protected RegisteredServiceProvider<T> provider;

    protected HookVault(Class<T> providerClass) {
        this.providerClass = providerClass;
        if (!isVaultEnable())return;
        this.provider = Jobs.getInstance().getServer().getServicesManager().getRegistration(this.providerClass);
        if (this.provider != null) {
            logProviderConnected();
            runIfProviderIsFound();
        } else {
            logProviderNotFound();
        }
    }

    public static boolean isVaultEnable() {
        if (vaultEnable == null) {
            setIsVaultEnable();
            if (!vaultEnable)
                logIfVaultIsNotEnable();
        }
        return vaultEnable;
    }

    private static void setIsVaultEnable() {
        vaultEnable = Jobs.getInstance().getServer().getPluginManager().isPluginEnabled("Vault");
    }

    public static void logIfVaultIsNotEnable() {
        if (vaultEnable)return;
        Jobs.getPluginLogger().severe("==================== " + Jobs.getInstance().getName() + " ====================");
        Jobs.getPluginLogger().severe("Vault is required by this plugin for economy support!");
        Jobs.getPluginLogger().severe("Please install them first!");
        Jobs.getPluginLogger().severe("You can find the latest versions here:");
        Jobs.getPluginLogger().severe("https://www.spigotmc.org/resources/34315/");
        Jobs.getPluginLogger().severe("==============================================");
    }

    public void logProviderNotFound() {
        if (this.provider == null && isVaultEnable()) {
            Jobs.getPluginLogger().severe("==================== " + Jobs.getInstance().getDescription().getName() + " ====================");
            Jobs.getPluginLogger().severe("Vault detected but " + this.providerClass.getSimpleName() + " plugin still missing!");
            Jobs.getPluginLogger().severe("Please install Vault supporting " + this.providerClass.getSimpleName() + " plugin!");
            Jobs.getPluginLogger().severe("==============================================");
        }
    }

    protected void logProviderConnected() {
        CMIMessages.consoleMessage("&e[" + Jobs.getInstance().getName() + "] Successfully linked with Vault. (" + provider.getPlugin().getName() + ")");
    }

    abstract void runIfProviderIsFound();
}
