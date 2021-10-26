package com.github.Viduality.VSkyblock.Utilitys;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class IslandCacheHandler {

    public static HashMap<UUID, String> playerislands = new HashMap<>();

    public static Cache<UUID, Integer> restartmap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Integer> leavemap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, UUID> invitemap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Integer> requestvisit = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, UUID> isgencooldown = CacheBuilder.newBuilder()
            .expireAfterWrite(getIsGenCooldown(), TimeUnit.MINUTES)
            .build();

    public static Cache<UUID, UUID> isjoincooldown = CacheBuilder.newBuilder()
            .expireAfterWrite(getIsJoinCooldown(), TimeUnit.MINUTES)
            .build();

    public static Map<String, BukkitTask> emptyloadedislands = new HashMap<>();

    public static Map<String, Location> islandhomes = new HashMap<>();

    public static int getIsGenCooldown() {
        if (isInt(ConfigShorts.getDefConfig().getString("IslandGenerateCooldown"))) {
            return ConfigShorts.getDefConfig().getInt("IslandGenerateCooldown");
        } else {
            return 5;
        }
    }

    public static int getIsJoinCooldown() {
        if (isInt(ConfigShorts.getDefConfig().getString("IslandJoinCooldown"))) {
            return ConfigShorts.getDefConfig().getInt("IslandJoinCooldown");
        } else {
            return 10;
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
