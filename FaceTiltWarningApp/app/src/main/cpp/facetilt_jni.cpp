#include <jni.h>
#include <cmath>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_facetiltwarningapp_MainActivity_isFaceTilted(JNIEnv* env, jobject /* this */, jfloat yaw, jfloat roll) {
    // Yaw: left/right, Roll: tilt
    float absYaw = std::fabs(yaw);
    float absRoll = std::fabs(roll);
    return (absYaw > 30.0f || absRoll > 30.0f) ? JNI_TRUE : JNI_FALSE;
}
