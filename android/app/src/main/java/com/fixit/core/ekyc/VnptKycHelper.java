package com.fixit.core.ekyc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class VnptKycHelper {
    private static final String TAG = "VnptKycHelper";

    private static final String ACTIVITY_CLASS = "com.vnptit.idg.sdk.activity.VnptIdentityActivity";
    private static final String INTENT_CONSTANTS_CLASS = "com.vnptit.idg.sdk.utils.KeyIntentConstants";
    private static final String RESULT_CONSTANTS_CLASS = "com.vnptit.idg.sdk.utils.KeyResultConstants";
    private static final String ENUM_CLASS = "com.vnptit.idg.sdk.utils.SDKEnum";

    private VnptKycHelper() {}

    /**
     * Kiểm tra xem VNPT eKYC SDK có khả dụng trong ứng dụng không.
     */
    public static boolean isSdkAvailable() {
        try {
            Class.forName(ACTIVITY_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "VNPT eKYC SDK is not available in classpath.");
            return false;
        }
    }

    /**
     * Khởi tạo Intent để chạy VNPT eKYC.
     */
    public static Intent createKycIntent(Context context, String tokenId, String tokenKey, String apiUrl) {
        if (!isSdkAvailable()) {
            return null;
        }

        try {
            Class<?> activityClazz = Class.forName(ACTIVITY_CLASS);
            Intent intent = new Intent(context, activityClazz);

            // Gán các tham số cấu hình qua Reflection
            putExtraIfConstantExists(intent, "ACCESS_TOKEN", tokenId); // Hoặc Token Key tùy cấu hình
            putExtraIfConstantExists(intent, "TOKEN_ID", tokenId);
            putExtraIfConstantExists(intent, "TOKEN_KEY", tokenKey);
            putExtraIfConstantExists(intent, "API_URL", apiUrl);

            // Document type: IDENTITY_CARD
            Object docTypeVal = getEnumValue(ENUM_CLASS + "$DocumentTypeEnum", "IDENTITY_CARD");
            if (docTypeVal != null) {
                putExtraIfConstantExists(intent, "DOCUMENT_TYPE", docTypeVal);
            }

            // Version SDK: Tìm kiếm ADVANCED, PRO_OVAL hoặc tương đương
            Object versionVal = getEnumValue(ENUM_CLASS + "$VersionSDKEnum", "PRO_OVAL");
            if (versionVal == null) {
                versionVal = getEnumValue(ENUM_CLASS + "$VersionSDKEnum", "ADVANCED");
            }
            if (versionVal != null) {
                putExtraIfConstantExists(intent, "VERSION_SDK", versionVal);
            }

            // Cấu hình hiển thị và kiểm tra Liveness
            putExtraIfConstantExists(intent, "SHOW_RESULT", false);
            putExtraIfConstantExists(intent, "IS_SHOW_TUTORIAL", true);
            putExtraIfConstantExists(intent, "IS_ENABLE_SCAN_QR", true);
            putExtraIfConstantExists(intent, "CHALLENGE_CODE", "INNOVATIONCENTER");
            putExtraIfConstantExists(intent, "RESOURCE_CUSTOMER", "VNPT");
            putExtraIfConstantExists(intent, "UNIT_CUSTOMER", "test1");
            
            // Các cấu hình bảo mật / kiểm tra thật giả khác nếu có
            putExtraIfConstantExists(intent, "IS_CHECK_LIVENESS_FACE", true);
            putExtraIfConstantExists(intent, "IS_CHECK_LIVENESS_CARD", true);
            putExtraIfConstantExists(intent, "IS_CHECK_MASK_FACE", true);
            putExtraIfConstantExists(intent, "IS_COMPARE", true);

            return intent;
        } catch (Exception e) {
            Log.e(TAG, "Error configuring VNPT eKYC Intent via reflection", e);
            return null;
        }
    }

    /**
     * Đọc kết quả trả về từ Intent của VNPT SDK.
     */
    public static VnptKycResult parseResult(Intent data) {
        if (data == null) {
            return null;
        }

        String infoResult = getStringExtraByConstant(data, "INFO_RESULT");
        String compareResult = getStringExtraByConstant(data, "COMPARE_RESULT");
        String frontImagePath = getStringExtraByConstant(data, "FRONT_IMAGE");
        String backImagePath = getStringExtraByConstant(data, "REAR_IMAGE"); // hoặc BACK_IMAGE
        if (backImagePath == null) {
            backImagePath = getStringExtraByConstant(data, "BACK_IMAGE");
        }
        String selfieImagePath = getStringExtraByConstant(data, "PORTRAIT_IMAGE");

        return new VnptKycResult(infoResult, compareResult, frontImagePath, backImagePath, selfieImagePath);
    }

    private static void putExtraIfConstantExists(Intent intent, String fieldName, Object value) {
        try {
            Class<?> clazz = Class.forName(INTENT_CONSTANTS_CLASS);
            Field field = clazz.getField(fieldName);
            String key = (String) field.get(null);
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            } else if (value instanceof Integer) {
                intent.putExtra(key, (Integer) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(key, (Boolean) value);
            } else if (value != null) {
                intent.putExtra(key, value.toString());
            }
        } catch (Exception e) {
            // Log warning or ignore if SDK version is different
            Log.d(TAG, "Optional field not found in KeyIntentConstants: " + fieldName);
        }
    }

    private static String getStringExtraByConstant(Intent intent, String fieldName) {
        try {
            Class<?> clazz = Class.forName(RESULT_CONSTANTS_CLASS);
            Field field = clazz.getField(fieldName);
            String key = (String) field.get(null);
            return intent.getStringExtra(key);
        } catch (Exception e) {
            Log.d(TAG, "Field not found in KeyResultConstants: " + fieldName);
            return null;
        }
    }

    private static Object getEnumValue(String enumClassName, String constantName) {
        try {
            Class<?> clazz = Class.forName(enumClassName);
            if (clazz.isEnum()) {
                for (Object constant : clazz.getEnumConstants()) {
                    Enum<?> e = (Enum<?>) constant;
                    if (e.name().equalsIgnoreCase(constantName)) {
                        try {
                            Method getValueMethod = clazz.getMethod("getValue");
                            return getValueMethod.invoke(constant);
                        } catch (NoSuchMethodException ex) {
                            return constant;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Enum or constant not found: " + enumClassName + "#" + constantName);
        }
        return null;
    }

    public static class VnptKycResult {
        private final String infoResult;
        private final String compareResult;
        private final String frontImagePath;
        private final String backImagePath;
        private final String selfieImagePath;

        public VnptKycResult(String infoResult, String compareResult, String frontImagePath, String backImagePath, String selfieImagePath) {
            this.infoResult = infoResult;
            this.compareResult = compareResult;
            this.frontImagePath = frontImagePath;
            this.backImagePath = backImagePath;
            this.selfieImagePath = selfieImagePath;
        }

        public String getInfoResult() {
            return infoResult;
        }

        public String getCompareResult() {
            return compareResult;
        }

        public String getFrontImagePath() {
            return frontImagePath;
        }

        public String getBackImagePath() {
            return backImagePath;
        }

        public String getSelfieImagePath() {
            return selfieImagePath;
        }

        @Override
        public String toString() {
            return "VnptKycResult{" +
                    "infoResult='" + (infoResult != null ? "[JSON]" : "null") + '\'' +
                    ", compareResult='" + (compareResult != null ? "[JSON]" : "null") + '\'' +
                    ", frontImagePath='" + frontImagePath + '\'' +
                    ", backImagePath='" + backImagePath + '\'' +
                    ", selfieImagePath='" + selfieImagePath + '\'' +
                    '}';
        }
    }
}
