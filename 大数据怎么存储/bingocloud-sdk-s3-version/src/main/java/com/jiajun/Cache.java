package com.jiajun;

import com.amazonaws.services.s3.model.PartETag;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jiajun
 * @Date: 2021-06-02 14:42
 */
public class Cache implements Serializable{
    private ArrayList<PartETag> partETags;

    private String uploadId;

    public ArrayList<PartETag> getPartETags() {
        return partETags;
    }

    public void setPartETags(ArrayList<PartETag> partETags) {
        this.partETags = partETags;
    }
//
//    public int getPartNumber() {
//        return partNumber;
//    }
//
//    public void setPartNumber(int partNumber) {
//        this.partNumber = partNumber;
//    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String  uploadId) {
        this.uploadId = uploadId;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "partETags=" + partETags +
//                ", partNumber=" + partNumber +
                ", uploadId='" + uploadId + '\'' +
                '}';
    }
}
