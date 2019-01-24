LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -lm -llog

LOCAL_CFLAGS := \
    -D__GXX_EXPERIMENTAL_CXX0X__ \
    -fvisibility=hidden \
    -ffunction-sections \
    -fdata-sections \
	-Os

LOCAL_CPPFLAGS := \
    -std=gnu++0x \
    -fvisibility=hidden \
    -ffunction-sections \
    -fdata-sections \
    -Os
				 
LOCAL_LDFLAGS := -Wl,--gc-sections

LOCAL_C_INCLUDES := $(LOCAL_PATH)/tex/src $(LOCAL_PATH)/port

# Traverse all the directory and subdirectory
define walk
    $(wildcard $(1)) $(foreach e, $(wildcard $(1)/*), $(call walk, $(e)))
endef

# Find all the file recursively under directory jni/
ALLFILES = $(call walk, $(LOCAL_PATH))
SRC_FILE_LIST = $(filter %.cpp, $(ALLFILES))

$(info [$(TARGET_ARCH_ABI)] source files are:)
$(foreach f, $(SRC_FILE_LIST), $(info $(f)))

LOCAL_MODULE    := tex
LOCAL_SRC_FILES := $(SRC_FILE_LIST:$(LOCAL_PATH)/%=%)

include $(BUILD_SHARED_LIBRARY)
