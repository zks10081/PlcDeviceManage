package com.dm.device.utils;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.dm.common.opc.OpcProperties;
import com.dm.common.utils.DataUtil;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.device.mapper.DmDeviceAttributeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

@Slf4j
public class DataSyncManager {

    // 缓冲队列
    public static final BlockingQueue<DmDeviceAttribute> syncQueue = new LinkedBlockingQueue<>(50000);

    // 线程池：核心 2 个线程，最大 4 个线程，队列满时由调用线程执行
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 4, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    // 单次最大批量拉取条数
    private static final int BATCH_SIZE = 500;

    public static void startBatchProcessor() {
        Thread dispatcherThread = new Thread(() -> {
            System.out.println("批量数据库同步调度器已启动...");
            List<DmDeviceAttribute> batchList = new ArrayList<>(BATCH_SIZE);

            while (true) {
                try {
                    // 1. 阻塞等待第一条数据到来
                    DmDeviceAttribute firstData = syncQueue.take();
                    batchList.add(firstData);

                    // 2. 尝试从队列中继续捞取，凑够 BATCH_SIZE 条（或队列当前为空则停止）
                    // drainTo 会一次性把队列里的数据转移到 list 中，效率极高
                    syncQueue.drainTo(batchList, BATCH_SIZE - 1);

                    // 3. 将捞取到的这一批数据，丢给线程池去异步执行写库
                    List<DmDeviceAttribute> finalBatch = new ArrayList<>(batchList);
                    executor.execute(() -> {
                        try {
                            updatePlcData(finalBatch);
                        } catch (Exception e) {
                            System.err.println("线程池执行批处理任务失败: " + e.getMessage());
                        }
                    });

                    // 4. 清空 list，准备下一批
                    batchList.clear();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }

    public static void updatePlcData(List<DmDeviceAttribute> list){
        // 每次批处理 500 条
        int batchSize = 500;

        // 核心魔法：SqlHelper 会开启真正的 JDBC 批量模式
        // 注意：executeBatch 第二个参数要求 org.apache.ibatis.logging.Log（MyBatis），
        // 而不是 Lombok @Slf4j 生成的 org.slf4j.Logger
        SqlHelper.executeBatch(
                DmDeviceAttribute.class,
                LogFactory.getLog(DataSyncManager.class),
                list,
                batchSize,
                (BiConsumer<SqlSession, DmDeviceAttribute>) (sqlSession, entity) -> {
                    // 这里的 sqlSession 已经是 ExecutorType.BATCH 模式
                    // 你可以调用 Mapper 的自定义方法（比如根据 address 更新）
                    DmDeviceAttributeMapper mapper = sqlSession.getMapper(DmDeviceAttributeMapper.class);
                    mapper.updatePlcDataByAddress(entity.getAddress(), entity.getPlcData());
                }
        );

        if(OpcProperties.islog) {
            log.info("OPCUA订阅推送 - {} - 本次推送：{}条数据", DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()), list.size());
        }
    }
}
