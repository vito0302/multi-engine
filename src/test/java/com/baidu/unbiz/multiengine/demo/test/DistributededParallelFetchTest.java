package com.baidu.unbiz.multiengine.demo.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.common.DisTaskPair;
import com.baidu.unbiz.multiengine.task.DistributedParallelExePool;
import com.baidu.unbiz.multiengine.transport.HostConf;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;
import com.baidu.unbiz.multiengine.vo.QueryParam;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.thread.MultiResult;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DistributededParallelFetchTest {

    @Resource(name = "distributedParallelExePool")
    private DistributedParallelExePool parallelExePool;

    @Before
    public void init() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();
    }

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void testDistributedParallelRunDisTask() {
        QueryParam qp = new QueryParam();
        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp));

        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)),
                        new TaskPair("voidParamFetcher", null),
                        new DisTaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new DisTaskPair("deviceBigDataStatFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");
        List<DeviceViewItem> vstat = ctx.getResult("voidParamFetcher");
        List<DeviceViewItem> bstat = ctx.getResult("deviceBigDataStatFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        Assert.notEmpty(vstat);
        Assert.notEmpty(bstat);

        System.out.println(stat);
        System.out.println(uv);
        System.out.println(vstat);
        System.out.println(bstat);
    }

    private void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}