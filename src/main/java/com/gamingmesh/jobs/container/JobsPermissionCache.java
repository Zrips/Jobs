package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;

import com.gamingmesh.jobs.JobsPermissionInfo;

public class JobsPermissionCache {
    Map<String, JobsPermissionInfo> permissionsCache = new HashMap<>();

    public Map<String, JobsPermissionInfo> getPermissionsCache() {
        return permissionsCache;
    }

    public JobsPermissionInfo getPermissionsCache(String perm) {
        return permissionsCache.computeIfAbsent(perm, k -> new JobsPermissionInfo());
    }

    public void addToPermissionsCache(String permission, JobsPermissionInfo permInfo) {
        permissionsCache.put(permission, permInfo);
    }

}
