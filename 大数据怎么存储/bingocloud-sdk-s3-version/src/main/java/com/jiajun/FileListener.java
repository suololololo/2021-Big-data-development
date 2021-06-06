package com.jiajun;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Matcher;


/**
 * @Author: jiajun
 * @Date: 2021-06-02 16:30
 */
public class FileListener extends FileAlterationListenerAdaptor {

    private FileSychronizer fileSychronizer;

    public FileListener(FileSychronizer fileSychronizer) {
        this.fileSychronizer = fileSychronizer;
    }

    /**
     * 文件新建
     *
     * @param file
     */
    @Override
    public void onFileCreate(File file) {

        fileSychronizer.upload(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));

        System.out.println("新建:" + file.getAbsolutePath());
    }

    /**
     * 文件修改
     *
     * @param file
     */
    @Override
    public void onFileChange(File file) {
        fileSychronizer.upload(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("修改:" + file.getAbsolutePath());
    }

    /**
     * 文件删除
     *
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        fileSychronizer.delete(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("删除" + file.getAbsolutePath());
    }

    /**
     * 目录新建
     *
     * @param directory
     */
    @Override
    public void onDirectoryCreate(File directory) {
        fileSychronizer.upload(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("新建:" + directory.getAbsolutePath());

    }

    /**
     * 目录修改
     *
     * @param directory
     */
    @Override
    public void onDirectoryChange(File directory) {
        fileSychronizer.upload(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("修改:" + directory.getAbsolutePath());
    }

    /**
     * 目录删除
     *
     * @param directory
     */
    @Override
    public void onDirectoryDelete(File directory) {

        fileSychronizer.deleteFolder(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("删除:" + directory.getAbsolutePath());
    }
}
