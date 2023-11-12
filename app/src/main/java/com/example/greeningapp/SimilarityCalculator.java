package com.example.greeningapp;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
//import org.tensorflow.lite.TensorBuffer;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimilarityCalculator {
    private static final String MODEL_PATH = "Gtext_model.tflite";
    private static final int EMBEDDING_SIZE = 8; // 모델의 임베딩 크기 (Embedding layer의 출력 크기)

    private final Interpreter interpreter;

    public SimilarityCalculator(Context context) throws IOException {
        // 모델 파일 로드
        MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH);
        interpreter = new Interpreter(modelBuffer);

//        //추가
//        try {
//            MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH);
//            interpreter = new Interpreter(modelBuffer);
//        } catch (IOException e) {
//            Log.e("SimilarityCalculator", "Error loading the TensorFlow Lite model: " + e);
//            throw e; // 예외를 상위로 전파하여 앱이 적절하게 처리하도록 함
//        }
    }


    // 실제 유사도를 계산하는 메서드
    public float calculateSimilarity(String referenceText, String reviewText) {
        // 텍스트를 임베딩하여 모델에 입력
        float[][] referenceEmbedding = preprocessInput(referenceText);
        float[][] reviewEmbedding = preprocessInput(reviewText);

        // 모델에 입력하고 출력 획득
//        float[] referenceOutput = new float[1]; // 모델의 출력 크기에 맞게 조절 //잠시 주석
//        float[] reviewOutput = new float[1]; // 모델의 출력 크기에 맞게 조절
        float[][] referenceOutput = new float[1][EMBEDDING_SIZE];
        float[][] reviewOutput = new float[1][EMBEDDING_SIZE];

        interpreter.run(referenceEmbedding, referenceOutput);
        interpreter.run(reviewEmbedding, reviewOutput);

        // 두 텍스트의 임베딩 간의 유사도 계산
        //float similarity = calculateCosineSimilarity(referenceOutput, reviewOutput); //잠시 주석
        float similarity = calculateCosineSimilarity(referenceOutput[0], reviewOutput[0]);

        return similarity;
    }

//    //추가
//    public float calculateSimilarity(String referenceText, String reviewText) {
//        float[][] referenceEmbedding = preprocessInput(referenceText);
//        float[][] reviewEmbedding = preprocessInput(reviewText);
//
//        // 모델에 입력하고 출력 획득
//        float[][] referenceOutput = new float[1][1];
//        float[][] reviewOutput = new float[1][1];
//
//        interpreter.runForMultipleInputsOutputs(new Object[]{referenceEmbedding}, getOutputMap(referenceOutput));
//        interpreter.runForMultipleInputsOutputs(new Object[]{reviewEmbedding}, getOutputMap(reviewOutput));
//
//        // 두 텍스트의 임베딩 간의 유사도 계산
//        float similarity = calculateCosineSimilarity(referenceOutput[0], reviewOutput[0]);
//
//        return similarity;
//    }
//
//    //추가
//    // 추가 메서드: 모델의 출력을 가져오기 위한 Map을 생성
//    private Map<Integer, Object> getOutputMap(float[][] outputArray) {
//        Map<Integer, Object> outputMap = new HashMap<>();
//        outputMap.put(0, outputArray);
//        return outputMap;
//    }


    // 텍스트를 임베딩하여 모델에 입력할 형태로 전처리
    private float[][] preprocessInput(String text) {
        // 적절한 전처리 로직을 적용하여 텍스트를 모델 입력 형태로 변환
        // 여기에서는 간단하게 Tokenizer를 사용하지 않고 텍스트의 길이를 4로 제한하는 방식을 사용
        float[][] input = new float[1][4];

        // 예시로 단어 개수를 세고 각 단어를 임베딩하는 코드 //추가
        //String[] words = text.split("\\s+");
        //float[][] input = new float[1][words.length * EMBEDDING_SIZE];

        //추가
        // 여기에서는 각 단어를 임베딩하여 input 배열에 할당
//        for (int i = 0; i < words.length; i++) {
//            float[] wordEmbedding = getWordEmbedding(words[i]);
//            System.arraycopy(wordEmbedding, 0, input[0], i * EMBEDDING_SIZE, EMBEDDING_SIZE);
//        }
        // 텍스트를 적절한 형태로 변환하여 input 배열에 할당

        return input;
    }

//    // 추가 메서드: 각 단어의 임베딩을 가져오기
//    private float[] getWordEmbedding(String word) {
//        // 여기에서는 각 단어를 임베딩하는 로직을 구현
//        // 예를 들어, 단어를 워드 임베딩 모델에 입력하고 결과를 반환하는 방식으로 구현 가능
//
//        // 더미로 구현한 예시
//        float[] dummyEmbedding = new float[EMBEDDING_SIZE];
//        Arrays.fill(dummyEmbedding, 0.5f); // 더미 값으로 채워진 임베딩
//
//        return dummyEmbedding;
//    }


    // 코사인 유사도 계산
    private float calculateCosineSimilarity(float[] vector1, float[] vector2) {
        // 두 벡터의 코사인 유사도 계산
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }

        // 분모가 0인 경우 대비
        if (norm1 != 0 && norm2 != 0) {
            return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
        } else {
            return 0;
        }
    }

    public void close() {
        interpreter.close();
    }
}



