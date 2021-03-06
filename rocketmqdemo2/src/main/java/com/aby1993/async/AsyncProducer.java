package com.aby1993.async;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @version v1.0
 * @ProjectName: RocketMq
 * @ClassName: BatchProducer
 * @Description:发送异步消息
 * @Author: Aby1993
 * @Date: 2020/5/8 9:14
 */
public class AsyncProducer {

    public static void main(String[] args) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        //创建生产者 根据对应的消费者组进行区分
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");

        //设置namesrv的地址
        producer.setNamesrvAddr("192.168.211.129:9876");

        //启动producer实例
        producer.start();

        long sysTime = System.currentTimeMillis();
        //构建消息
        Message msg = new Message("TopicTest1", "TagB", "OrderID00" + sysTime, ("Hello MetaQ" + sysTime).getBytes());

        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功结果：sendResult:" + sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送失败结果：throwable:" + throwable);
            }
        });

        producer.shutdown();
    }
}
