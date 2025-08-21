#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

#define LOG_TAG "CryptoJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Placeholder: In production, use a real crypto library (e.g., OpenSSL, libsodium)
// Here, we just reverse the string for demonstration.

std::string simple_encrypt(const std::string& input) {
    std::string out = input;
    std::reverse(out.begin(), out.end());
    return out;
}
std::string simple_decrypt(const std::string& input) {
    std::string out = input;
    std::reverse(out.begin(), out.end());
    return out;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_androidcryptojniapp_MainActivity_encryptNative(
        JNIEnv* env, jobject /* this */, jstring algorithm, jstring plainText) {
    const char* algo = env->GetStringUTFChars(algorithm, nullptr);
    const char* plain = env->GetStringUTFChars(plainText, nullptr);
    std::string result = simple_encrypt(plain); // Replace with real crypto
    env->ReleaseStringUTFChars(algorithm, algo);
    env->ReleaseStringUTFChars(plainText, plain);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_androidcryptojniapp_MainActivity_decryptNative(
        JNIEnv* env, jobject /* this */, jstring algorithm, jstring cipherText) {
    const char* algo = env->GetStringUTFChars(algorithm, nullptr);
    const char* cipher = env->GetStringUTFChars(cipherText, nullptr);
    std::string result = simple_decrypt(cipher); // Replace with real crypto
    env->ReleaseStringUTFChars(algorithm, algo);
    env->ReleaseStringUTFChars(cipherText, cipher);
    return env->NewStringUTF(result.c_str());
}
