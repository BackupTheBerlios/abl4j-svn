#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
/*
 * Class:     org_schwiebert_random_NativeRandom
 * Method:    random
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_schwiebert_abl4j_random_NativeRandom_next(JNIEnv *env, jobject ojb){
	return rand();
}

/*
 * Class:     org_schwiebert_random_NativeRandom
 * Method:    seed
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_schwiebert_abl4j_random_NativeRandom_setSeed(JNIEnv *env, jobject obj, jlong seed){
	srand(seed);
}
