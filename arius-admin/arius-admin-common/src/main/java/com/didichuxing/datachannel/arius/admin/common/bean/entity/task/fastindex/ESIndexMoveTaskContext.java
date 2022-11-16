package com.didichuxing.datachannel.arius.admin.common.bean.entity.task.fastindex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 */
@NoArgsConstructor
@Data
public class ESIndexMoveTaskContext {
    private SourceDTO source;
    private SinkerDTO sinker;
    private ReaderDTO reader;

    @AllArgsConstructor
    @Data
    public static class SourceDTO {
        private String sourceIndex;
        private String sourceClusterAddress;
        private String sourceClusterUserName;
        private String sourceClusterPassword;
        private String sourceIndexType;
        private Boolean ignoreHealth;
    }

    @AllArgsConstructor
    @Data
    public static class SinkerDTO {
        private String targetIndex;
        private String targetClusterAddress;
        private String targetClusterUserName;
        private String targetClusterPassword;
        private String targetIndexType;
    }

    @AllArgsConstructor
    @Data
    public static class ReaderDTO {
        private Boolean ignoreVersion;
        private Boolean ignoreId;
        private Integer readFileRateLimit;
    }
}
