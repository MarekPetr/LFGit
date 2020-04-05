LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := libtermux-bootstrap
LOCAL_SRC_FILES := bootstrap-zip.S bootstrap.c
include $(BUILD_SHARED_LIBRARY)