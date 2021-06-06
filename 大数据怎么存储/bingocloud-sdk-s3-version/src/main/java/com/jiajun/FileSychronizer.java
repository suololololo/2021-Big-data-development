package com.jiajun;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.util.StringUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import redis.clients.jedis.Jedis;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;

import com.amazonaws.services.s3.transfer.Upload;

/**
 * @Author: jiajun
 * @Date: 2021-06-01 19:07
 */
public class FileSychronizer {
    public String dir;
    private String bucketName;
    private final String accessKey;
    private final String secretKey;
    private final String serviceEndpoint = "http://10.16.0.1:81";
    private final String signingRegion = "";
    private final BasicAWSCredentials credentials;
    private final ClientConfiguration ccfg;
    private final EndpointConfiguration endpoint;
    private final AmazonS3 s3;
    private final long PARTSIZE = 5 << 20;
    private final long BIGFILELENGTH = 20 << 20;
    private ExecutorService executorService = new ThreadPoolExecutor(4, 8, 2000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());
    public final String SUFFIX = "/";

    public FileSychronizer(String dir, String bucketName, String accessKey, String secretKey) {
        if (StringUtils.isNullOrEmpty(dir) || StringUtils.isNullOrEmpty(bucketName) || StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey)) {
            System.out.println("输入有误");
            System.exit(1);
        }
        if (!(dir.endsWith("/")||dir.endsWith("\\"))) {
            dir += "/";
        }
        this.dir = dir.replaceAll(Matcher.quoteReplacement(File.separator), SUFFIX);
        this.bucketName = bucketName;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        //新建client
        credentials = new BasicAWSCredentials(accessKey, secretKey);
        ccfg = new ClientConfiguration().withUseExpectContinue(true);
        ccfg.setSocketTimeout(60000);
        endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint)
                .withPathStyleAccessEnabled(true)
                .build();
        if (!s3.doesBucketExistV2(bucketName)) {
            System.out.println("bucket is not exist");
            System.exit(1);
        }
    }


    private void multiPartUpload(String keyName) {
//        String keyName = Paths.get(dir).getFileName().toString();
        ArrayList<PartETag> partETags = new ArrayList<>();
        File file = new File(dir + keyName);
        if (!file.exists()) {
            return;
        }
        long contentLength = file.length();
        String upLoadId = null;
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);

        Cache cache = JedisCache.getCache(keyName);
        if (cache == null || cache.getUploadId() == null) {
            upLoadId = s3.initiateMultipartUpload(initRequest).getUploadId();
            if (cache == null) {
                cache = new Cache();
            }
            cache.setUploadId(upLoadId);

        } else {
            upLoadId = cache.getUploadId();
            partETags = cache.getPartETags();
        }
        List<Integer> successfulPart = listSuccessfulPart(keyName, upLoadId);
        for (Integer integer : successfulPart) {
            System.out.println("已上传" + integer);
        }
        int i = 0;
        //上传分片
        long partSize = PARTSIZE;
        AtomicLong filePosition = new AtomicLong(0);
        AtomicInteger partNumber = new AtomicInteger(1);
        CountDownLatch countDownLatch = new CountDownLatch((int) Math.ceil(contentLength * 1.0 / partSize));
        System.out.println((int) Math.ceil(contentLength * 1.0 / partSize));
        try {
            while (filePosition.get() < contentLength) {
                partSize = Math.min(partSize, contentLength - filePosition.get());
                if (successfulPart.isEmpty()) {
                    executorService.execute(new MultiPart(partSize, keyName, upLoadId, file, partNumber.getAndIncrement(),
                            (int) filePosition.getAndAdd(partSize), countDownLatch, partETags, false, cache));
                    continue;
                }
                if (i < successfulPart.size()) {
                    if (partNumber.get() == successfulPart.get(i)) {
                        i++;
                        executorService.execute(new MultiPart(partSize, keyName, upLoadId, file, partNumber.getAndIncrement(),
                                (int) filePosition.getAndAdd(partSize), countDownLatch, partETags, true, cache));
                    } else {
                        executorService.execute(new MultiPart(partSize, keyName, upLoadId, file, partNumber.getAndIncrement(),
                                (int) filePosition.getAndAdd(partSize), countDownLatch, partETags, false, cache));
                    }
                } else {
                    executorService.execute(new MultiPart(partSize, keyName, upLoadId, file, partNumber.getAndIncrement(),
                            (int) filePosition.getAndAdd(partSize), countDownLatch, partETags, false, cache));
                }


            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completing upload");
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName, upLoadId, partETags);
        s3.completeMultipartUpload(compRequest);
        JedisCache.removeCache(keyName);
//        RedisManager.remove(keyName);
//        RedisManager.writeObject();
        System.out.println("Done!");

    }

//    public void multiPartDownLoad(String keyName, Date date) {
//        String filePath = dir + keyName;
//        File file = new File(filePath);
//        S3Object s3Object = null;
//        S3ObjectInputStream s3ObjectInputStream = null;
//        FileOutputStream fos = null;
//        ObjectMetadata objectMetadata = s3.getObjectMetadata(bucketName, keyName);
//        long contentLength = objectMetadata.getContentLength();
//        try {
//            long filePostion = 0;
//            long partSize = PARTSIZE;
//            CountDownLatch countDownLatch = new CountDownLatch((int) Math.ceil(contentLength * 1.0 / partSize));
//            AtomicInteger partNumber = new AtomicInteger(1);
//            fos = new FileOutputStream(file);
//            while (filePostion < contentLength) {
//                partSize = Math.min(partSize, contentLength - filePostion);
//                GetObjectRequest downLoadRequest = new GetObjectRequest(bucketName, keyName);
//                if (date != null) {
//                    downLoadRequest.setModifiedSinceConstraint(date);
//                }
//                downLoadRequest.setRange(filePostion, filePostion + partSize);
//                executorService.execute(new DownLoadThread(downLoadRequest, s3, file, partNumber.getAndIncrement(), countDownLatch, fos));
//                filePostion += partSize + 1;
//            }
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        } finally {
//            if (fos !=null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                }
//            }
//        }
//    }

    private void multiPartDownLoad(String keyName, Date date) {
        String filePath = dir + keyName;
        File file = new File(filePath);
        S3Object s3Object = null;
        S3ObjectInputStream s3ObjectInputStream = null;
        FileOutputStream fos = null;

        try {
            ObjectMetadata objectMetadata = s3.getObjectMetadata(bucketName, keyName);
            long contentLength = objectMetadata.getContentLength();
            fos = new FileOutputStream(file);
            long filePostion = 0;
            long parSize = PARTSIZE;
            GetObjectRequest downLoadRequest = new GetObjectRequest(bucketName, keyName);
            for (int i = 1; filePostion < contentLength; i++) {
                parSize = Math.min(parSize, contentLength - filePostion);
                downLoadRequest.setRange(filePostion, filePostion + parSize);
                if (date != null) {
                    downLoadRequest.setModifiedSinceConstraint(date);
                }
                s3Object = s3.getObject(downLoadRequest);
                System.out.format("Downloading part %d\n", i);
                filePostion += parSize + 1;
                s3ObjectInputStream = s3Object.getObjectContent();
                byte[] read_buf = new byte[64 * 1024];
                int read_len = 0;
                while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (s3ObjectInputStream!= null) {
               try {
                   s3ObjectInputStream.close();
               } catch (IOException e) {

               }
            }
            if (fos != null) {
                try {
                    fos.close();
                }catch (IOException e) {

                }

            }
        }
    }


    private class MultiPart implements Runnable {
        private long partSize;
        private String keyName;
        private String upLoadId;
        private File file;
        private int partNumber;
        private int filePosition;
        private CountDownLatch countDownLatch;
        private ArrayList<PartETag> partETags;
        private boolean isUploaded;
        private Cache cache;

        MultiPart(long partSize, String keyName, String upLoadId, File file, int partNumber, int filePosition,
                  CountDownLatch countDownLatch, ArrayList<PartETag> partETags, boolean isUploaded, Cache cache) {
            this.partSize = partSize;
            this.keyName = keyName;
            this.upLoadId = upLoadId;
            this.file = file;
            this.partNumber = partNumber;
            this.filePosition = filePosition;
            this.countDownLatch = countDownLatch;
            this.partETags = partETags;
            this.isUploaded = isUploaded;
            this.cache = cache;
        }

        @Override
        public void run() {
            // Create request to upload a part.
            if (!isUploaded) {
                System.out.println("Uploading part " + partNumber);
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(upLoadId)
                        .withPartNumber(partNumber)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                synchronized (partETags) {
                    partETags.add(s3.uploadPart(uploadRequest).getPartETag());
                    cache.setPartETags(partETags);
                }
            }
            JedisCache.setCache(keyName, cache);
            countDownLatch.countDown();
            System.out.println("Uploading part " + partNumber + "Done!");

        }
    }

    /**
     * 列出已成功上传的分片
     *
     * @param fileName 文件名 作为key
     * @param fileId   即文件uploadId
     * @return
     */
    private List<Integer> listSuccessfulPart(String fileName, String fileId) {
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, fileName, fileId);
        PartListing partListing = s3.listParts(listPartsRequest);
        List<PartSummary> parts = partListing.getParts();
        List<Integer> uploadSuccessIndex = new ArrayList<>();
        for (PartSummary partSummary : parts) {
            uploadSuccessIndex.add(partSummary.getPartNumber());
        }
        return uploadSuccessIndex;
    }

    /**
     * 上传文件
     *
     * @param fileName
     */
    public void upload(String fileName) {
        fileName = fileName.replaceAll(Matcher.quoteReplacement(File.separator), "/");
        String filePath = dir + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            long contentLength = file.length();
            if (contentLength > BIGFILELENGTH) {
                multiPartUpload(fileName);
            } else {
                for (int i = 0; i < 2; i++) {
                    try {
                        s3.putObject(bucketName, fileName, file);
                        break;
                    } catch (AmazonServiceException e) {
                        if (e.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                            s3.createBucket(bucketName);
                            continue;
                        }
                        System.out.println(e.toString());
                    }
                }
            }
        } else {
            createFolder(fileName);
        }

    }

    /**
     * 下载文件
     *
     * @param fileName
     */
    private void downLoad(String fileName, Date date) {
        S3ObjectInputStream s3ObjectInputStream = null;
        FileOutputStream fileOutputStream = null;
        String filePath = dir + fileName;
        File file = new File(filePath);
        if (!file.isDirectory()) {
            try {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
                if (date != null) {
                    getObjectRequest.setModifiedSinceConstraint(date);
                }
                S3Object s3Object = s3.getObject(getObjectRequest);
                if (s3Object == null) {
                    return;
                }
                ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
                if (objectMetadata.getInstanceLength() >= BIGFILELENGTH) {
                    multiPartDownLoad(fileName, date);
                } else {
                    System.out.println("downloading");
                    s3ObjectInputStream = s3Object.getObjectContent();
                    fileOutputStream = new FileOutputStream(new File(filePath));
                    byte[] readBuffer = new byte[64 * 1024];
                    int read_len = 0;
                    while ((read_len = s3ObjectInputStream.read(readBuffer)) > 0) {
                        fileOutputStream.write(readBuffer, 0, read_len);
                    }
                }
            } catch (AmazonServiceException e) {
                System.err.println(e.toString());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (s3ObjectInputStream != null) {
                    try {
                        s3ObjectInputStream.close();
                    } catch (IOException e) {
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }


    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    public void delete(String fileName) {
        if (!s3.doesBucketExistV2(bucketName)) {
            return;
        }
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objectListing = s3.listObjects(listObjectsRequest);
        String strKey = null;
        System.out.println(fileName);
        fileName = fileName.replaceAll(Matcher.quoteReplacement(File.separator), "/");
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            strKey = objectSummary.getKey();

            if (strKey.equals(fileName)) {
                s3.deleteObject(bucketName, fileName);
                break;
            }
        }

    }

    /**
     * 父目录及其子目录下的是否也要递归同步
     *
     * @return
     */
    private List<String> listBucketFile() {
        if (!s3.doesBucketExistV2(bucketName)) {
            return null;
        }
        List<String> fileList = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objectListing = s3.listObjects(listObjectsRequest);
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            fileList.add(objectSummary.getKey());


        }
        return fileList;
    }


    /**
     * 主程序启动
     * 1.扫描bucket中的文件 把文件同步到本地
     * <p>
     * 文件冲突(以哪边的为准)：
     * 两个相同的文件
     * 1.同步过程中，bucket中的文件被删了，
     * 2.bucket中新加了文件 事件监听模型 -- 一旦发现本地添加，修改，删除文件，则通知删除s3的文件
     */
    public void start() {
        List<String> pathList = listBucketFile();
        List<String> fileList = new ArrayList<>();
        Queue<File> directoryQueue = new LinkedList<>();
        List<String> dirFile = new ArrayList<>();
        List<String> dirList = new ArrayList<>();
        // 列出s3 中的文件夹和文件
        for (int i = pathList.size() - 1; i >= 0; i--) {
//            File file = new File(dir + pathList.get(i));
            String temp = pathList.get(i);
            if (!temp.endsWith(SUFFIX)) {
                fileList.add(temp);
                pathList.remove(i);
            }
        }
        // 列出本地目录的文件夹 和文件
        File file = new File(dir);
        directoryQueue.offer(file);
        File[] files = file.listFiles();
        while (!directoryQueue.isEmpty()) {
            File temp = directoryQueue.poll();
            if (!temp.isDirectory()) {
                dirFile.add(temp.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), SUFFIX).replace(dir, ""));
            } else {
                if (!temp.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), SUFFIX).equals(dir)) {
                    dirList.add(temp.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), SUFFIX).replace(dir, ""));
                }

                for (File f : temp.listFiles()) {
                    if (!f.isDirectory()) {
                        dirFile.add(f.getAbsolutePath().replaceAll(Matcher.quoteReplacement(File.separator), SUFFIX).replace(dir, ""));
                    } else {
                        directoryQueue.offer(f);
                    }
                }
            }

        }
        // 将s3文件夹同步到本地
        for (String dir : pathList) {
            File createDir = new File(this.dir + dir);
            if (!createDir.exists()) {
                createDir.mkdir();
            }
        }
        // 将s3 的文件同步到本地
        for (String f : fileList) {
            File localFile = new File(this.dir + f);
            if (!localFile.exists()) {
                downLoad(f, null);
            } else {
                //出现了文件冲突 以本地的为准 根据修改时间进行决定 如果本地的修改时间较新则以本地的为准  那s3的要更新吗
                Date date = new Date(file.lastModified());
                downLoad(f, date);

            }
        }
        // 将本地文件夹同步到 s3中
        int i = 0;
        for (String dir : dirList) {
            if (i == 0) {
                i++;
                continue;
            }
            File createDir = new File(this.dir + dir);
            boolean isExist = false;
            for (String s3Dir : pathList) {
                if (s3Dir.equals(dir)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                createFolder(dir);
            }
        }
        // 将文件同步到s3中
        for (String f : dirFile) {
            boolean isExist = false;
            for (String fileName : fileList) {
                if (fileName.equals(f)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                upload(f);
            }
        }


    }


    public void listen() {
        long interval = TimeUnit.SECONDS.toMillis(1);
        FileAlterationObserver observer = new FileAlterationObserver(new File(dir));
        observer.addListener(new FileListener(this));
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        try {
            monitor.start();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    private void createFolder(String folderName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, inputStream, metadata);
        s3.putObject(putObjectRequest);
    }

    public void deleteFolder(String folderName) {
        folderName = folderName.replaceAll(Matcher.quoteReplacement(File.separator), "/");
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, folderName + SUFFIX);
        s3.deleteObject(deleteObjectRequest);

    }
}
