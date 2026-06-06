package com.fixit.feature.upload.domain.model;

public class UploadQueueRequest {
    private final LocalUploadFile localFile;
    private final String targetType;
    private final String targetEntityId;
    private final String groupId;
    private final String slotKey;
    private final String extraPayloadJson;

    public UploadQueueRequest(
            LocalUploadFile localFile,
            String targetType,
            String targetEntityId,
            String groupId,
            String slotKey,
            String extraPayloadJson
    ) {
        this.localFile = localFile;
        this.targetType = targetType;
        this.targetEntityId = targetEntityId;
        this.groupId = groupId;
        this.slotKey = slotKey;
        this.extraPayloadJson = extraPayloadJson;
    }

    public static UploadQueueRequest of(LocalUploadFile localFile) {
        return new UploadQueueRequest(localFile, null, null, null, null, null);
    }

    public LocalUploadFile getLocalFile() {
        return localFile;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetEntityId() {
        return targetEntityId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public String getExtraPayloadJson() {
        return extraPayloadJson;
    }
}
