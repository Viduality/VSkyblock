package com.github.Viduality.VSkyblock.Commands;/*
 * VSkyblock
 * Copyright (c) 2021 Max Lee aka Phoenix616 (max@themoep.de)
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

import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ExecutionInfo {
    private final CommandSender sender;
    private final PlayerInfo playerInfo;

    private String arg = null;

    private OfflinePlayer targetPlayer = null;

    public ExecutionInfo(CommandSender sender, PlayerInfo playerInfo) {
        this.sender = sender;
        this.playerInfo = playerInfo;
    }

    public CommandSender getSender() {
        return sender;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public OfflinePlayer getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(OfflinePlayer targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
