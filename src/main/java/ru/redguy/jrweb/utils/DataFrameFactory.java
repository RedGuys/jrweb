package ru.redguy.jrweb.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DataFrameFactory {
    private List<DataFrame> dataFramesBuffer = new ArrayList<>();
    private DataFrame currentCunstructingDataFrame = null;
    private final BitStream stream = new BitStream();
    private final LinkedBlockingQueue<List<DataFrame>> readyDataFrames = new LinkedBlockingQueue<>();
    public Runnable onNewDataFrame = () -> {};
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(0,1, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());

    public void addByte(byte b) {
        stream.addByte(b);
        //run process in new thread to prevent blocking
        executor.execute(() -> {
            if(currentCunstructingDataFrame == null) {
                currentCunstructingDataFrame = new DataFrame(stream);
            }
            currentCunstructingDataFrame.process();
            if (currentCunstructingDataFrame.isReady()) {
                dataFramesBuffer.add(currentCunstructingDataFrame);
                currentCunstructingDataFrame = null;
                if (dataFramesBuffer.get(dataFramesBuffer.size() - 1).isFin()) {
                    readyDataFrames.add(dataFramesBuffer);
                    dataFramesBuffer = new ArrayList<>();
                    onNewDataFrame.run();
                }
            }
        });
    }

    public List<DataFrame> pollParsedDataFrame() {
        return readyDataFrames.poll();
    }
}
