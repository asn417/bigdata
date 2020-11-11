package com.asn.mr.flow;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Author: wangsen
 * @Date: 2020/11/8 15:07
 * @Description:
 **/
public class FlowReducer extends Reducer<Text,FlowBean,Text,FlowBean> {
    private FlowBean sumFlow = new FlowBean();

    @Override
    public void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        long sumUpFlow = 0;
        long sumDownFlow = 0;
        for (FlowBean value:values){
            sumUpFlow += value.getUpFlow();
            sumDownFlow += value.getDownFlow();
        }
        sumFlow.set(sumUpFlow,sumDownFlow);
        context.write(key,sumFlow);
    }

}
