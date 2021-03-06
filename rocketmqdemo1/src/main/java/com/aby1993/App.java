package com.aby1993;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.util.List;

/**
 * 基础demo
 */
public class App {
    public static void main(String[] args) {

        sendMsg();
    }

    public static void sendMsg() {

        // 获取消息生产者
        DefaultMQProducer producer = Producer.getDefaultMQProducer();

        try {
            for (int i = 0; i < 2000; i++) {
                Message msg = new Message(
                        "TopicTest1",                   // topic
                        "TagA",                         // tag
                        "OrderID00" + i,                  // key
                        ("Hello MetaQ" + i).getBytes());  // body
                SendResult sendResult = producer.send(msg);
                //logger.info("sendResult:{}", sendResult);
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producer.shutdown();
    }

    public static void receiveMsg() {

        // 获取消息生产者
        DefaultMQPushConsumer consumer = Consumer.getDefaultMQPushConsumer();

        // 订阅主体
        try {
            //                         consumer.subscribe("TopicTest1","tag1||tag2||tag3");
            consumer.subscribe("TopicTest1", "*");
            consumer.setMessageModel(MessageModel.BROADCASTING);
            consumer.registerMessageListener(new MessageListenerConcurrently() {

                /**
                 * * 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                 */
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                    System.out.println("currentThreadName:" + Thread.currentThread().getName() + " and Receive New Messages:" + msgs);

                    MessageExt msg = msgs.get(0);

                    if (msg.getTopic().equals("TopicTest1")) {
                        // 执行TopicTest1的消费逻辑
                        if (msg.getTags() != null && msg.getTags().equals("TagA")) {
                            // 执行TagA的消费
                            System.out.println("MsgBody:" + new String(msg.getBody()));
                        } else if (msg.getTags() != null
                                && msg.getTags().equals("TagC")) {
                            // 执行TagC的消费
                        } else if (msg.getTags() != null
                                && msg.getTags().equals("TagD")) {
                            // 执行TagD的消费
                        }
                    } else if (msg.getTopic().equals("TopicTest2")) {
                        // 执行TopicTest2的消费逻辑
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();

            System.out.println("Consumer Started.");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
