package com.orange.clara.cloud.dbdump;

import java.io.File;

/**
 * Copyright (C) 2015 Arthur Halet
 * <p/>
 * This software is distributed under the terms and conditions of the 'MIT'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'http://opensource.org/licenses/MIT'.
 * <p/>
 * Author: Arthur Halet
 * Date: 21/05/2015
 */
public class MysqlDatabaseDumper extends AbstractDatabaseDumper implements DatabaseDumper {
    public MysqlDatabaseDumper(File binaryDump, File binaryRestore) {
        super(binaryDump, binaryRestore);
    }

    @Override
    public Boolean handles(String type) {
        return type.equals("mysql");
    }


    @Override
    public String[] getDumpCommandLine(String inputPath) {
        return String.format("%s --routines --host=%s --port=%s --user=%s --password=%s %s --result-file=%s",
                this.binaryDump.getAbsolutePath(),
                this.databaseRef.getHost(),
                this.databaseRef.getPort(),
                this.databaseRef.getUser(),
                this.databaseRef.getPassword(),
                this.databaseRef.getDatabaseName(),
                inputPath
        ).split(" ");
    }

    @Override
    public String[] getRestoreCommandLine(String outputPath) {
        return String.format("%s --host=%s --port=%s --user=%s --password=%s %s -e \"source %s;\"",
                this.binaryRestore.getAbsolutePath(),
                this.databaseRef.getHost(),
                this.databaseRef.getPort(),
                this.databaseRef.getUser(),
                this.databaseRef.getPassword(),
                this.databaseRef.getDatabaseName(),
                outputPath
        ).split(" ");
    }
}
