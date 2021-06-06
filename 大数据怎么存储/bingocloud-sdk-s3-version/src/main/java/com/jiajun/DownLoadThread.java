package com.jiajun;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: jiajun
 * @Date: 2021-06-03 11:55
 */
public class DownLoadThread implements Runnable {
    private GetObjectRequest downLoadRequest;
    private AmazonS3 s3;
    private File file;
    private int partNumber;
    private CountDownLatch countDownLatch;
    private S3ObjectInputStream s3ObjectInputStream;
    private FileOutputStream fos;
    public DownLoadThread(GetObjectRequest getObjectRequest, AmazonS3 s3, File file, int partNumber, CountDownLatch countDownLatch, FileOutputStream fos) {
        this.downLoadRequest = getObjectRequest;
        this.s3 = s3;
        this.file = file;
        this.partNumber = partNumber;
        this.countDownLatch = countDownLatch;
        this.fos = fos;
    }

    @Override
    public void run() {
        try {
            S3Object s3Object = s3.getObject(downLoadRequest);
            System.out.format("Downloading part %d\n", partNumber);
            s3ObjectInputStream = s3Object.getObjectContent();
//            fos = new FileOutputStream(file);
            byte[] readBuffer = new byte[64 * 1024];
            int readLen = 0;
            while ((readLen = s3ObjectInputStream.read(readBuffer)) > 0) {
                fos.write(readBuffer, 0, readLen);
            }
            countDownLatch.countDown();
            System.out.println(partNumber + "Done");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {

            if (s3ObjectInputStream != null) {
                try {
                    s3ObjectInputStream.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
