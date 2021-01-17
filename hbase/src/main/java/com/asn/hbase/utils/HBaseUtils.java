package com.asn.hbase.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.asn.hbase.config.HBaseConfig;
import com.asn.utils.ObjectUtils;
import com.google.common.collect.Iterables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @Author: wangsen
 * @Date: 2021/1/17 18:48
 * @Description:
 **/
public class HBaseUtils {

    private static Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    private static Connection connection;
    private static Configuration configuration;
    private static HBaseUtils hBaseUtils;
    private static Properties properties;
    private static Admin admin;

    /**
     * @Author: wangsen
     * @Description: 私有无参构造方法
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    private HBaseUtils() {
    }
    /**
     * @Author: wangsen
     * @Description: 唯一实例，线程安全，保证连接池唯一
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static HBaseUtils getInstance(HBaseConfig hBaseConfig) {
        if (hBaseUtils == null) {
            synchronized (HBaseUtils.class) {
                if (hBaseUtils == null) {
                    hBaseUtils = new HBaseUtils();
                    hBaseUtils.init(hBaseConfig);
                }
            }
        }
        return hBaseUtils;
    }

    /**
     * @Author: wangsen
     * @Description: 创建连接池并初始化环境配置
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public void init(HBaseConfig hBaseConfig) {
        properties = System.getProperties();
        // 实例化HBase配置类
        if (configuration == null) {
            configuration = HBaseConfiguration.create();
        }
        try {
            // 加载本地hadoop二进制包，换成你解压的地址
            properties.setProperty("hadoop.home.dir", StrUtil.isEmpty(hBaseConfig.getHadoop_home_dir())?"xxx":hBaseConfig.getHadoop_home_dir());
            // zookeeper集群的URL配置信息
            //configuration.set("hbase.zookeeper.quorum", "flink1,flink2,flink3");
            configuration.set("hbase.zookeeper.quorum", hBaseConfig.getHbase_zk_quorm());
            // HBase的Master
            //configuration.set("hbase.master", "flink1:16000");
            configuration.set("hbase.master", hBaseConfig.getHbase_master());
            // 客户端连接zookeeper端口
            //configuration.set("hbase.zookeeper.property.clientPort", "2181");
            configuration.set("hbase.zookeeper.property.clientPort", hBaseConfig.getZk_clientport());
            // HBase RPC请求超时时间，默认60s(60000)
            configuration.setInt("hbase.rpc.timeout", hBaseConfig.getHbase_rpc_timeout() == 0?60000:hBaseConfig.getHbase_rpc_timeout());
            // 客户端重试最大次数，默认10
            configuration.setInt("hbase.client.retries.number", hBaseConfig.getClient_retries_number() == 0?10:hBaseConfig.getClient_retries_number());
            // 客户端发起一次操作数据请求直至得到响应之间的总超时时间，可能包含多个RPC请求，默认为2min
            configuration.setInt("hbase.client.operation.timeout", hBaseConfig.getClient_operation_timeout() == 0?20000:hBaseConfig.getClient_operation_timeout());
            // 客户端发起一次scan操作的rpc调用至得到响应之间的总超时时间
            configuration.setInt("hbase.client.scanner.timeout.period", hBaseConfig.getScanner_timeout_period() == 0?200000:hBaseConfig.getScanner_timeout_period());
            // 获取hbase连接对象*/
            if (connection == null || connection.isClosed()) {
                connection = ConnectionFactory.createConnection(configuration);
                admin = connection.getAdmin();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @Author: wangsen
     * @Description: 关闭连接池
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @Author: wangsen
     * @Description: 创建表
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static void createTable(String tableName, String[] columnFamily) throws IOException{
        TableName name = TableName.valueOf(tableName);
        //如果存在则删除
        if (admin.tableExists(name)) {
            admin.disableTable(name);
            admin.deleteTable(name);
            logger.error("create table error! this table {} already exists!", name);
        } else {
            TableDescriptorBuilder tableDescriptor = TableDescriptorBuilder.newBuilder(name);
            for (String cf : columnFamily) {
                tableDescriptor.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build());
            }
            admin.createTable(tableDescriptor.build());//创建表
            logger.info("create table:{} success!",name);
        }
    }
    /**
     * @Author: wangsen
     * @Description: 创建表(根据行键范围及分区个数自动创建分区表)
     * @Date: 2020/3/10
     * @Param: [tableName, columnFamily, startKey, endKey, numRegions]
     * @Return: void
     **/
    public static void createTable(String tableName, String[] columnFamily, String startKey,String endKey,int numRegions) throws IOException{
        TableName name = TableName.valueOf(tableName);
        //如果存在则删除
        if (admin.tableExists(name)) {
            admin.disableTable(name);
            admin.deleteTable(name);
            logger.error("create table error! this table {} already exists!", name);
        } else {
            TableDescriptorBuilder tableDescriptor = TableDescriptorBuilder.newBuilder(name);
            for (String cf : columnFamily) {
                tableDescriptor.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build());
            }
            admin.createTable(tableDescriptor.build(),Bytes.toBytes(startKey),Bytes.toBytes(endKey),numRegions);//创建表
            logger.info("create table:{} success!",name);
        }
    }
    /***
     * @Author: wangsen
     * @Description: 设置带超时时间的表(单位 秒)，默认所有的列族使用同一个ttl(超时时间，指的是列族会在超时时间到时清空数据，不会删除表)
     * @Date: 2020/10/16
     * @Param: [tableName, columnFamily, ttl]
     * @Return: void
     **/
    public static void createTableWithTTL(String tableName,String[] columnFamily,int ttl) throws Exception{
        TableName name = TableName.valueOf(tableName);
        if (!admin.tableExists(TableName.valueOf(tableName))){
            TableDescriptorBuilder tableDescriptor = TableDescriptorBuilder.newBuilder(name);
            for (String cf : columnFamily) {
                //tableDescriptor.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).setMinVersions(0).setTimeToLive(ttl).build());
                tableDescriptor.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).setTimeToLive(ttl).build());
            }
            TableDescriptor build = tableDescriptor.build();
            admin.createTable(build);
        }
    }
    /**
     * @Author: wangsen
     * @Description: 判断表是否存在，不存在则创建
     * @Date: 2020/12/13
     * @Param: [tableName, splitKeys, columnFamilies]
     * @Return: void
     **/
    public static <T> void checkAndCreateTable(String tableName, List<T> splitKeys, List<String> columnFamilies) throws IOException {
        if (!isTableExist(tableName)){
            byte[][] keys = getSplitKeys(splitKeys);
            HBaseUtils.createTableBySplitKeys(tableName,columnFamilies,keys,false);
        }
    }
    /**
     * @Author: wangsen
     * @Description: 创建表(根据自定义的分区键创建分区表,根据isAsync决定同步创建还是异步创建)
     * @Date: 2020/3/10
     * @Param: [tableName, columnFamily, splitKeys, isAsync]
     * @Return: void
     **/
    public static void createTableBySplitKeys(String tableName, List<String> columnFamily,byte[][] splitKeys,boolean isAsync) throws IOException {
        if (StrUtil.isEmpty(tableName) || columnFamily == null
                || columnFamily.size() < 0) {
            logger.info("===Parameters tableName|columnFamily should not be null,Please check!===");
            return;
        }
        if (isTableExist(tableName)) {
            logger.info(tableName+": 表已存在！");
            return;
        }
        TableDescriptorBuilder tdesc=TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        for(String s: columnFamily){
            ColumnFamilyDescriptor cfd=ColumnFamilyDescriptorBuilder.of(s);
            tdesc.setColumnFamily(cfd);
        }
        TableDescriptor desc=tdesc.build();
        if (isAsync)
            admin.createTableAsync(desc,splitKeys);
        else
            admin.createTable(desc,splitKeys);
        logger.info("===Create Table " + tableName
                + " Success!columnFamily:" + columnFamily.toString()
                + "===");
    }
    /**
     * @Author: wangsen
     * @Description: 生成分区键
     * @Date: 2020/3/10
     * @Param: [keys]
     * @Return: byte[][]
     **/
    public static <T> byte[][] getSplitKeys(List<T> keys) {
        byte[][] splitKeys = new byte[keys.size()][];
        TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);//升序排序
        for(T key:keys)
            rows.add(ObjectUtils.toByteArray(key));
        Iterator<byte[]> rowKeyIter = rows.iterator();
        int i=0;
        while (rowKeyIter.hasNext()) {
            byte[] tempRow = rowKeyIter.next();
            rowKeyIter.remove();
            splitKeys[i] = tempRow;
            i++;
        }
        return splitKeys;
    }
    /**
     * @Author: wangsen
     * @Description: 插入单条数据，map方式
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static void insertCols(String tablename, String rowkey, String family, Map<String, String> cloumns)
            throws IOException {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tablename));
            Put put = new Put(rowkey.getBytes());
            for (Map.Entry<String, String> entry : cloumns.entrySet()) {
                put.addColumn(family.getBytes(), entry.getKey().getBytes(), entry.getValue().getBytes());
            }
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
        }
    }
    /**
     * @Author: wangsen
     * @Description: 插入单条数据，数组方式
     * @Date: 2020/1/13
     * @Param:
     * @Return:
     **/
    public static void insertCols(String tablename, String rowkey, String columnFamily, String[] columns,
                                  String[] values) throws IOException {
        if (values==null || values.length==0){
            return;
        }
        Table table = connection.getTable(TableName.valueOf(tablename));
        Put put = new Put(Bytes.toBytes(rowkey));
        for (int i = 0; i < columns.length; i++) {
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
        }
        table.put(put);
        table.close();
    }
    /**
     * @Author: wangsen
     * @Description: 批量插入全部数据
     * @Date: 2020/1/16
     * @Param: values的key为rowkey，值为column和对应的value
     * @Return:
     **/
    public static void batchInsert(String tableName,String columnFamily,Map<String,Map<String,String>> values) throws IOException {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            List<Put> puts = new ArrayList<>();
            for (Map.Entry<String,Map<String,String>> entry:values.entrySet()){
                Put put = new Put(Bytes.toBytes(entry.getKey()));
                for (Map.Entry<String,String> subEntry:entry.getValue().entrySet()){
                    put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(subEntry.getKey()),Bytes.toBytes(subEntry.getValue()));
                }
                puts.add(put);
            }
            table.put(puts);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            table.close();
        }
    }
    /***
     * @Author: wangsen
     * @Description: 对全部数据进行分批插入，避免table一次插入过量数据
     * @Date: 2020/3/6
     * @Param: [tableName, columnFamily, values, batchSize]
     * @Return: void
     **/
    public static void batchInsert(String tableName,String columnFamily,Map<String,Map<String,String>> values,int batchSize) throws IOException {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            //Iterables.partition(values.entrySet(), batchSize)这个方法会把数据复制到新的对象，造成内存浪费，后面可以优化
            for(List<Map.Entry<String, Map<String, String>>> entries : Iterables.partition(values.entrySet(), batchSize)){
                List<Put> puts = new ArrayList<>();
                for (Map.Entry<String,Map<String,String>> entry : entries){
                    Put put = new Put(Bytes.toBytes(entry.getKey()));
                    for (Map.Entry<String,String> subEntry:entry.getValue().entrySet()){
                        put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(subEntry.getKey()),Bytes.toBytes(subEntry.getValue()));
                    }
                    puts.add(put);
                }
                table.put(puts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            table.close();
        }
    }
    /***
     * @Author: wangsen
     * @Description: 批量写入
     * @Date: 2020/12/13
     * @Param: [tableName, columnFamily, values, batchSize]
     * @Return: void
     **/
    public static <T> void batchInsert(String tableName,String columnFamily,List<T> values,int batchSize) throws IOException {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            for(List<T> subValues : Iterables.partition(values, batchSize)){
                for (T value:subValues){
                    Map<String, Object> map = BeanUtil.beanToMap(value);
                    List<Put> puts = new ArrayList<>();
                    Put put = new Put(Bytes.toBytes((String) map.get("rowkey")));
                    put.setDurability(Durability.SKIP_WAL); // 不写WAL日志
                    for (Map.Entry<String,Object> entry : map.entrySet()){
                        if (entry.getKey() != "rowkey"){
                            put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(entry.getKey()),ObjectUtils.toByteArray(entry.getValue()));
                        }
                        puts.add(put);
                    }
                    table.put(puts);
                }
            }
        } finally {
            try {
                if (table != null){
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * @Author: wangsen
     * @Description: 获取单条数据
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static Result getRow(String tableName, String row) throws IOException {
        Table table = null;
        Result result = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(row.getBytes());
            result = table.get(get);
        } finally {
            table.close();
        }
        return result;
    }
    /***
     * @Author: wangsen
     * @Description: 获取指定rowkey的指定列的值
     * @Date: 2020/10/27
     * @Param: [tableName, rowkey, cf, cn]
     * @Return: org.apache.hadoop.hbase.client.Result
     **/
    public static Result getCell(String tableName,String rowkey,String cf,String cn) throws IOException {
        Table table = null;
        Result result = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowkey.getBytes());
            get.addColumn(cf.getBytes(),cn.getBytes());
            result = table.get(get);
        } finally {
            table.close();
        }
        return result;
    }
    /**
     * @Author: wangsen
     * @Description: 查询多行信息
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static Result[] getRows(String tablename, List<byte[]> rows) throws IOException {
        Table table = null;
        List<Get> gets = null;
        Result[] results = null;
        try {
            table = connection.getTable(TableName.valueOf(tablename));
            gets = new ArrayList<Get>();
            for (byte[] row : rows) {
                if (row != null) {
                    gets.add(new Get(row));
                }
            }
            if (gets.size() > 0) {
                results = table.get(gets);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
        }
        return results;
    }
    /**
     * @Author: wangsen
     * @Description: 获取整表数据
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static ResultScanner scanTable(String tablename) throws IOException {
        Table table = null;
        ResultScanner results = null;
        try {
            table = connection.getTable(TableName.valueOf(tablename));
            Scan scan = new Scan();
            scan.setCaching(1000);
            results = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
        }
        return results;
    }

    /**
     * @Author: wangsen
     * @Description: PageFilter分页过滤只有用在单个region的表中才能保证数据条数的准确性
     * @Date: 2020/12/31
     * @Param: [tableName, currentPage, pageSize]
     * @Return: org.apache.hadoop.hbase.client.ResultScanner
     **/
    public static ResultScanner queryDataByPage(String tableName, int currentPage, int pageSize, FilterList filterList) {
        Table table;
        byte[] startRow;
        byte[] stopRow;
        ResultScanner resultScanner = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));

            int from = (currentPage - 1) * pageSize;
            startRow = getStartRow(table,from,filterList);
            int to = currentPage * pageSize;
            stopRow = getStopRow(table,to,filterList);

            Scan scan = new Scan();
            if (filterList != null){
                scan.setFilter(filterList);
            }
            if (startRow != null){
                scan.withStartRow(startRow,false);
            }
            if (stopRow != null){
                scan.withStopRow(stopRow,true);
            }
            resultScanner = table.getScanner(scan);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return resultScanner;
    }

    private static byte[] getStartRow(Table table,int from,FilterList filterList){
        byte[] startRow = null;
        Scan scan = new Scan();
        scan.setLimit(from);
        if (filterList != null) {
            FilterList copyFilterList = copyFilterList(filterList);
            copyFilterList.addFilter(new FirstKeyOnlyFilter());
            scan.setFilter(copyFilterList);
        }
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        Iterator<Result> iterator = scanner.iterator();
        Result result = null;
        while (iterator.hasNext()){
            result = iterator.next();
        }
        if (result != null){
            startRow = result.getRow();
        }
        return startRow;
    }
    private static byte[] getStopRow(Table table,int to,FilterList filterList){
        byte[] stopRow = null;
        Scan scan = new Scan();
        scan.setLimit(to);

        if (filterList != null) {
            FilterList copyFilterList = copyFilterList(filterList);
            copyFilterList.addFilter(new FirstKeyOnlyFilter());
            scan.setFilter(copyFilterList);
        }

        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        Iterator<Result> iterator = scanner.iterator();
        Result result = null;
        while (iterator.hasNext()){
            result = iterator.next();
        }
        if (result != null){
            stopRow = result.getRow();
        }
        return stopRow;
    }

    private static FilterList copyFilterList(FilterList filterList) {
        FilterList newFilterList = null;
        if (filterList != null){
            newFilterList = new FilterList(filterList);
        }
        return newFilterList;
    }

    /**
     * @Author: wangsen
     * @Description: 删除指定列的数据
     * @Date: 2020/1/12
     * @Param:
     * @Return:
     **/
    public static void deleteCol(String tablename, String rowkey, String family, String column) throws IOException {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tablename));
            Delete del = new Delete(rowkey.getBytes());
            del.addColumns(family.getBytes(), column.getBytes());
            table.delete(del);
        } finally {
            table.close();
        }
    }
    /**
     * @Author: wangsen
     * @Description: 删除rowkey对应的整行数据
     * @Date: 2020/2/29
     * @Param: [tablename, rowkey]
     * @Return: void
     **/
    public static void deleteRow(String tablename, String rowkey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tablename));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        table.delete(delete);
        table.close();
    }
    /**
     * @Author: wangsen
     * @Description: 判断表是否存在
     * @Date: 2020/1/13
     * @Param:
     * @Return:
     **/
    public static boolean isTableExist(String tableName) throws IOException {
        return admin.tableExists(TableName.valueOf(tableName));
    }
    /**
     * @Author: wangsen
     * @Description: 判断列族是否存在
     * @Date: 2020/1/13
     * @Param:
     * @Return:
     **/
    public static boolean isColumnFamilyExist(String tableName,String columnFamily) throws IOException {
        if(isTableExist(tableName)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            TableDescriptor tableDescriptor = table.getDescriptor();
            ColumnFamilyDescriptor descriptor = tableDescriptor.getColumnFamily(Bytes.toBytes(columnFamily));
            return descriptor==null?false:true;
        }else {
            return false;
        }
    }
    /**
     * @Author: wangsen
     * @Description: 根据表名获取regionInfo
     * @Date: 2020/1/13
     * @Param:
     * @Return:
     **/
    public static RegionInfo getRegionInfo(String tableName){
        RegionInfoBuilder regionInfoBuilder = RegionInfoBuilder.newBuilder(TableName.valueOf(tableName));
        RegionInfo regionInfo = regionInfoBuilder.build();
        String regionname = Bytes.toString(regionInfo.getRegionName());
        String strkey = Bytes.toString(regionInfo.getStartKey());
        String endkey = Bytes.toString(regionInfo.getEndKey());
        logger.info("RegionName:"+regionname+" ,START:"+strkey+" ,END:"+endkey);
        return regionInfo;
    }

    /**
     * 创建命名空间
     * @param namespace
     */
    public static void createNamespace(String namespace) {
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
        try {
            admin.createNamespace(namespaceDescriptor);
        } catch (NamespaceExistException e) {
            logger.info(namespace+": 命名空间已存在！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除命名空间
     * @param namespace
     * @param force
     */
    public static void dropNamespace(String namespace, boolean force) {
        try {
            if (force) {
                TableName[] tableNames = admin.listTableNamesByNamespace(namespace);
                for (TableName name : tableNames) {
                    admin.disableTable(name);
                    admin.deleteTable(name);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            admin.deleteNamespace(namespace);
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }
    }
    /**
     * 删除表
     * @param tableName
     */
    public static void dropTable(String tableName) throws IOException {
        if (!isTableExist(tableName)){
            logger.info(tableName+": 表不存在");
            return;
        }
        admin.disableTable(TableName.valueOf(tableName));
        admin.deleteTable(TableName.valueOf(tableName));
    }
    /**
     * 插入单条列数据
     * @param tableName
     * @param rowkey
     * @param cf
     * @param cn
     * @param value
     * @throws IOException
     */
    public static <T> void putData(String tableName,String rowkey,String cf,String cn,T value) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(cn), ObjectUtils.toByteArray(value));
        table.put(put);
        table.close();
    }
    /**
     * 获取单条数据
     * @param tableName
     * @param rowkey
     */
    public static List<Cell> getDataByKey(String tableName, String rowkey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowkey));
        Result result = table.get(get);
        List<Cell> listCells = result.listCells();
        table.close();
        return listCells;
    }
    /**
     * 根据rowkey范围扫描过滤
     * @param tableName
     * @param startRow
     * @param stopRow
     */
    public static ResultScanner scanTableByRowkeyScope(String tableName,String startRow,String stopRow) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan(Bytes.toBytes(startRow),Bytes.toBytes(stopRow));
        ResultScanner resultScanner = table.getScanner(scan);
        int rowCount = 0;
        for (Result result:resultScanner){
            for (Cell cell : result.rawCells()) {
                logger.info("--------------------------------rowkey:"+Bytes.toString(CellUtil.cloneRow(cell))+",cf:"+Bytes.toString(CellUtil.cloneFamily(cell))
                        +",cn:"+Bytes.toString(CellUtil.cloneQualifier(cell))
                        +",value:"+Bytes.toString(CellUtil.cloneValue(cell)));
            }
            rowCount++;
        }
        logger.info("--------------------------------rowcount: "+rowCount+" -------------------------------------");
        table.close();
        return resultScanner;
    }
    /**
     * 根据rowKey过滤数据，rowKey可以使用正则表达式
     * 返回rowKey和Cells的键值对
     * @param tableName
     * @param rowkey
     * @param operator
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByRowKeyRegex(String tableName, String rowkey, CompareOperator operator) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        //使用正则
        RowFilter filter = new RowFilter(operator,new RegexStringComparator(rowkey));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        table.close();
        return map;
    }
    /**
     * @Author: wangsen
     * @Description: 包含子串匹配(判断一个子串是否存在于rowkey中，并且不区分大小写)
     * @Date: 2020/2/29
     * @Param: [tableName, rowkey, operator]
     * @Return: org.apache.hadoop.hbase.client.ResultScanner
     **/
    public static ResultScanner filterByRowKeySub(String tableName, String rowkey, CompareOperator operator) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        RowFilter filter = new RowFilter(operator,new SubstringComparator(rowkey));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        table.close();
        return scanner;
    }
    /**
     * 使用二进制比较器BinaryComparator，提高效率(只能是完整的rowkey)
     * @param tableName
     * @param rowkey
     * @param operator
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByRowKeyBinary(String tableName, String rowkey, CompareOperator operator) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        RowFilter filter = new RowFilter(operator,new BinaryComparator(Bytes.toBytes(rowkey)));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        table.close();
        return map;
    }
    /**
     * 根据列族，列名，列值（支持正则）查找数据
     * 返回值：如果查询到值，会返回所有匹配的rowKey下的各列族、列名的所有数据（即使查询的时候这些列族和列名并不匹配）
     * @param tableName
     * @param columnFamily
     * @param columnName
     * @param value
     * @param operator
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByValueRegex(String tableName,String columnFamily,String columnName,
                                                            String value,CompareOperator operator) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        //ValueFilter比SingleColumnValueFilter和ColumnValueFilter的性能好
        ValueFilter filter = new ValueFilter(operator,
                new RegexStringComparator(value));
        //正则匹配
//        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
//                Bytes.toBytes(columnName),operator,new RegexStringComparator(value));

        //完全匹配
//        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
//                Bytes.toBytes(columnName),operator,Bytes.toBytes(value));

        //SingleColumnValueExcludeFilter排除列值

        //要过滤的列必须存在，如果不存在，那么这些列不存在的数据也会返回。如果不想让这些数据返回,设置setFilterIfMissing为true
//        filter.setFilterIfMissing(true);
        scan.setFilter(filter);

        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        return map;
    }
    /***
     * @Author: wangsen
     * @Description: 根据FilterList查询
     * @Date: 2020/3/11
     * @Param: [tableName, filterList]
     * @Return: java.util.Map<java.lang.String,java.util.List<org.apache.hadoop.hbase.Cell>>
     **/
    public static Map<String,List<Cell>> filterByFilterList(String tableName,FilterList filterList) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();
        scan.setFilter(filterList);

        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            System.out.println("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        return map;
    }

    /**
     * 根据传入的value值精准匹配
     * @param tableName
     * @param columnFamily
     * @param columnName
     * @param value
     * @param operator
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByValueBytes(String tableName,String columnFamily,String columnName,
                                                            String value,CompareOperator operator) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        //完全匹配
        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName),operator,Bytes.toBytes(value));
        //要过滤的列必须存在，如果不存在，那么这些列不存在的数据也会返回。如果不想让这些数据返回,设置setFilterIfMissing为true
        filter.setFilterIfMissing(true);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        return map;
    }
    /**
     * 根据列名前缀过滤数据
     * @param tableName
     * @param prefix
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByColumnPrefix(String tableName,String prefix) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));

        //列名前缀匹配
        ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes(prefix));

        //QualifierFilter 用于列名多样性匹配过滤
//        QualifierFilter filter = new QualifierFilter(CompareOperator.EQUAL,new SubstringComparator(prefix));

        //多个列名前缀匹配
//        MultipleColumnPrefixFilter multiFilter = new MultipleColumnPrefixFilter(new byte[][]{});

        Scan scan = new Scan();
        scan.setFilter(filter);

        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        return map;
    }
    /**
     * 过滤器集合的使用。
     * 根据列名范围以及列名前缀过滤数据
     * @param tableName
     * @param colPrefix
     * @param minCol
     * @param maxCol
     * @return
     * @throws IOException
     */
    public static Map<String,List<Cell>> filterByPrefixAndRange(String tableName,String colPrefix,
                                                                String minCol,String maxCol) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));

        //列名前缀匹配
        ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes(colPrefix));

        //列名范围扫描，上下限范围包括
        ColumnRangeFilter rangeFilter = new ColumnRangeFilter(Bytes.toBytes(minCol),true,
                Bytes.toBytes(maxCol),true);

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(filter);
        filterList.addFilter(rangeFilter);

        Scan scan = new Scan();
        scan.setFilter(filterList);

        ResultScanner scanner = table.getScanner(scan);
        Map<String,List<Cell>> map = new HashMap<>();
        for(Result result:scanner){
            map.put(Bytes.toString(result.getRow()),result.listCells());
            logger.info("-----------------rowkey: "+Bytes.toString(result.getRow()));
        }
        return map;
    }
    /**
     * 根据rowKey删除所有行数据
     * @param tableName
     * @param rowkey
     * @throws IOException
     */
    public static void deleteByKey(String tableName,String rowkey) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        table.delete(delete);
        table.close();
    }
    /**
     * 根据rowKey和列族删除所有行数据
     * @param tableName
     * @param rowkey
     * @param columnFamily
     * @throws IOException
     */
    public static void deleteByKeyAndFamily(String tableName,String rowkey,String columnFamily) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        delete.addFamily(Bytes.toBytes(columnFamily));
        table.delete(delete);
        table.close();
    }
    /**
     * 删除数据
     * @param tableName
     * @param rowkey
     * @param columnFamily
     * @param columnName
     * @throws IOException
     */
    public static void deleteByKeyAndFC(String tableName,String rowkey,String columnFamily,String columnName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        delete.addColumns(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName));
        table.delete(delete);
        table.close();
    }
    /**
     * 根据rowKey、列族删除多个列的数据
     * @param tableName
     * @param rowkey
     * @param columnFamily
     * @param columnNames
     * @throws IOException
     */
    public static void deleteByKeyAndFCList(String tableName,String rowkey, String columnFamily,List<String> columnNames) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        for(String columnName:columnNames){
            delete.addColumns(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName));
        }
        table.delete(delete);
        table.close();
    }
    /**
     * @Author: wangsen
     * @Description: 统计全部行数
     * @Date: 2020/2/11
     * @Param:
     * @Return:
     **/
    public static long rowCountByScanFilter(String tableName) throws IOException {
        long rowCount = 0;
        Table table = null;
        ResultScanner rs = null;
        try {
            TableName name=TableName.valueOf(tableName);
            table = connection.getTable(name);
            Scan scan = new Scan();
            //FirstKeyOnlyFilter只会取得每行数据的第一个kv，提高count速度
            scan.setFilter(new FirstKeyOnlyFilter());
            rs = table.getScanner(scan);
            for (Result result : rs) {
                rowCount += result.size();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            rs.close();
            table.close();
        }
        return rowCount;
    }
    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        HBaseUtils.connection = connection;
    }

    /**
     * @Author wangsen
     * @Description //缓存到hbase
     * @Date 15:20 2020/10/19
     * @Param [tableName, rowkey, value]
     * @return void
     **/
    public static <T> void setCacheToHbase(String tableName,String rowkey,T value) throws Exception {
        if (isTableExist(tableName)){
            putData(tableName,rowkey,"cf","cn",value);
        }else {
            String[] cf = {"cf"};
            createTableWithTTL(tableName,cf,43200);//默认缓存12小时
            putData(tableName,rowkey,"cf","cn",value);
        }
    }

    /**
     * @Author wangsen
     * @Description //从hbase读取缓存
     * @Date 15:21 2020/10/19
     * @Param [tableName, rowkey]
     * @return java.lang.String
     **/
    public static String getCacheFromHbase(String tableName,String rowkey) throws IOException {
        String result = null;
        if (isTableExist(tableName)){
            List<Cell> cellList = getDataByKey(tableName, rowkey);
            if (cellList != null && cellList.size()>0){
                result = Bytes.toString(CellUtil.cloneValue(cellList.get(0)));
            }
        }
        return result;
    }
}