package com.gamingmesh.jobs;

import net.milkbowl.vault.permission.Permission;

public class HookPermissionTask extends HookVault<Permission> {

    public HookPermissionTask(Class<Permission> providerClass) {
        super(providerClass);
    }

    @Override
    void runIfProviderIsFound() {
        Jobs.setVaultPermission(this.provider.getProvider());
    }
}
