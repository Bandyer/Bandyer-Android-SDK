package com.bandyer.demo_android_sdk.utils;

import leakcanary.LeakCanary;

/**
 * Enable or Disable leakCanary
 * @author kristiyan
 */
public class LeakCanaryManager {

    public static void enableLeakCanary(boolean enabled) {
        // be aware there is the same class in release flavor!!
        // required to be able to compile release app as this dependecy is declared in debug only
        LeakCanary.Config leakCanaryConfig = LeakCanary.INSTANCE.getConfig();
        LeakCanary.Config newLeakCanaryConfig = leakCanaryConfig.copy(
                enabled,
                leakCanaryConfig.getDumpHeapWhenDebugging(),
                leakCanaryConfig.getRetainedVisibleThreshold(),
                leakCanaryConfig.getReferenceMatchers(),
                leakCanaryConfig.getObjectInspectors(),
                leakCanaryConfig.getOnHeapAnalyzedListener(),
                leakCanaryConfig.getComputeRetainedHeapSize(),
                leakCanaryConfig.getMaxStoredHeapDumps(),
                leakCanaryConfig.getRequestWriteExternalStoragePermission(),
                leakCanaryConfig.getUseExperimentalLeakFinders()
        );
        LeakCanary.INSTANCE.setConfig(newLeakCanaryConfig);
    }
}
