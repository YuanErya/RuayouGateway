package com.ruayou.common.config;

import lombok.Data;

@Data
public class DisruptorConfig {
    private String defaultBufferType = "default";
    private String parallelBufferType = "parallel";

    private int bufferSize =1024 * 16;

    private int processThread = Runtime.getRuntime().availableProcessors();

    private String waitStrategy = "blocking";
}
