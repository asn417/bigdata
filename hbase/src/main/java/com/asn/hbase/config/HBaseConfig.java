package com.asn.hbase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: wangsen
 * @Date: 2021/1/17 18:53
 * @Description:
 **/
@Component
@Configuration
public class HBaseConfig {
    @Value("${hadoop.home.dir}")
    String hadoop_home_dir;
    @Value("${hbase.zookeeper.quorum}")
    String hbase_zk_quorm;
    @Value("${hbase.master}")
    String hbase_master;
    @Value("${hbase.zookeeper.property.clientPort}")
    String zk_clientport;
    @Value("${hbase.rpc.timeout}")
    int hbase_rpc_timeout;
    @Value("${hbase.client.retries.number}")
    int client_retries_number;
    @Value("${hbase.client.operation.timeout}")
    int client_operation_timeout;
    @Value("${hbase.client.scanner.timeout.period}")
    int scanner_timeout_period;

    public String getHadoop_home_dir() {
        return hadoop_home_dir;
    }

    public void setHadoop_home_dir(String hadoop_home_dir) {
        this.hadoop_home_dir = hadoop_home_dir;
    }

    public String getHbase_zk_quorm() {
        return hbase_zk_quorm;
    }

    public void setHbase_zk_quorm(String hbase_zk_quorm) {
        this.hbase_zk_quorm = hbase_zk_quorm;
    }

    public String getHbase_master() {
        return hbase_master;
    }

    public void setHbase_master(String hbase_master) {
        this.hbase_master = hbase_master;
    }

    public String getZk_clientport() {
        return zk_clientport;
    }

    public void setZk_clientport(String zk_clientport) {
        this.zk_clientport = zk_clientport;
    }

    public int getHbase_rpc_timeout() {
        return hbase_rpc_timeout;
    }

    public void setHbase_rpc_timeout(int hbase_rpc_timeout) {
        this.hbase_rpc_timeout = hbase_rpc_timeout;
    }

    public int getClient_retries_number() {
        return client_retries_number;
    }

    public void setClient_retries_number(int client_retries_number) {
        this.client_retries_number = client_retries_number;
    }

    public int getClient_operation_timeout() {
        return client_operation_timeout;
    }

    public void setClient_operation_timeout(int client_operation_timeout) {
        this.client_operation_timeout = client_operation_timeout;
    }

    public int getScanner_timeout_period() {
        return scanner_timeout_period;
    }

    public void setScanner_timeout_period(int scanner_timeout_period) {
        this.scanner_timeout_period = scanner_timeout_period;
    }
}
