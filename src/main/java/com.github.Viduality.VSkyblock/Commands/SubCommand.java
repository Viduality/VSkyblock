package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;


public interface SubCommand {
    void execute(DatabaseCache databaseCache);
}
