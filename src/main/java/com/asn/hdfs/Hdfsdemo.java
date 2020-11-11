package com.asn.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class Hdfsdemo{

    private FileSystem fs;

    @Before
    public void before() throws URISyntaxException, IOException, InterruptedException {
        fs = FileSystem.get(new URI("hdfs://flink1:9000"), new Configuration(), "root");
        System.out.println("before......");
    }

    @Test
    public void put() throws URISyntaxException, IOException, InterruptedException {
        //设置配置文件，覆盖hdfs-site.xml的配置
        Configuration configuration = new Configuration();
        configuration.setInt("dfs.replication",1);
        configuration.setInt("dfs.blocksize",1048576);
        fs = FileSystem.get(new URI("hdfs://flink1:9000"), configuration, "root");
        fs.copyFromLocalFile(new Path("d:/1.txt"),new Path("/1.txt"));
    }

    @Test
    public void delete() throws IOException {
        fs.delete(new Path("/1.txt"),false);
    }
    @Test
    public void mkdirs() throws IOException {
        fs.mkdirs(new Path("/asn"));
    }
    @Test
    public void ls() throws IOException {
        //FileStatus包含文件夹信息
        FileStatus[] fileStatuses = fs.listStatus(new Path("/"));
        for (FileStatus fileStatus:fileStatuses){
            if (fileStatus.isFile()){
                System.out.println("这是一个文件！");
            }else if (fileStatus.isDirectory()){
                System.out.println("这是一个文件夹！");
            }
            System.out.println(fileStatus.getPath());
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getOwner());
            System.out.println(fileStatus.getReplication());
        }
    }
    @Test
    public void listFiles() throws IOException {
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("/"), true);
        while (files.hasNext()){
            //LocatedFileStatus只包含文件信息，块信息
            LocatedFileStatus next = files.next();
            System.out.println("块信息....");
            BlockLocation[] blockLocations = next.getBlockLocations();
            for (BlockLocation blockLocation:blockLocations){
                String[] hosts = blockLocation.getHosts();
                for (String host:hosts){
                    System.out.println(host);
                }
            }
        }
    }

    @After
    public void after() throws IOException {
        fs.close();
    }
}
