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
     * �ļ��½�
     *
     * @param file
     */
    @Override
    public void onFileCreate(File file) {

        fileSychronizer.upload(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));

        System.out.println("�½�:" + file.getAbsolutePath());
    }

    /**
     * �ļ��޸�
     *
     * @param file
     */
    @Override
    public void onFileChange(File file) {
        fileSychronizer.upload(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("�޸�:" + file.getAbsolutePath());
    }

    /**
     * �ļ�ɾ��
     *
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        fileSychronizer.delete(file.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("ɾ��" + file.getAbsolutePath());
    }

    /**
     * Ŀ¼�½�
     *
     * @param directory
     */
    @Override
    public void onDirectoryCreate(File directory) {
        fileSychronizer.upload(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("�½�:" + directory.getAbsolutePath());

    }

    /**
     * Ŀ¼�޸�
     *
     * @param directory
     */
    @Override
    public void onDirectoryChange(File directory) {
        fileSychronizer.upload(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("�޸�:" + directory.getAbsolutePath());
    }

    /**
     * Ŀ¼ɾ��
     *
     * @param directory
     */
    @Override
    public void onDirectoryDelete(File directory) {

        fileSychronizer.deleteFolder(directory.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), fileSychronizer.SUFFIX).replace(fileSychronizer.dir, ""));
        System.out.println("ɾ��:" + directory.getAbsolutePath());
    }
}
