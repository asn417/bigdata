import com.asn.hbase.config.HBaseConfig;
import com.asn.hbase.utils.HBaseUtil;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class Test {
    @Before
    public void init(){
        HBaseConfig config = new HBaseConfig();
        config.setHbase_master("flink3:16000");
        config.setHbase_zk_quorm("flink1,flink2,flink3");
        config.setZk_clientport("2181");
        HBaseUtil.getInstance(config);
    }
    @org.junit.Test
    public void testTTL(){
        HBaseUtil.scanTable("mydb:test");
        System.out.println("xxxxxxxx");
    }
}
