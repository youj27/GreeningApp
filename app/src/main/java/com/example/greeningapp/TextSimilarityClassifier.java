package com.example.greeningapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class TextSimilarityClassifier {

    private static final String MODEL_PATH = "Gtext_model.tflite";
    private Interpreter tflite;

//    public TextSimilarityClassifier(MappedByteBuffer modelBuffer) {
//        tflite = new Interpreter(modelBuffer);
//    }

    public TextSimilarityClassifier(Context context) throws IOException {
        tflite = new Interpreter(loadModelFile(context));
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //잠시 주석
    public float predictSimilarity(float[][] input) {
        float[][] output = new float[1][1];
        tflite.run(input, output);
        return output[0][0];
    }

//    public float predictSimilarity(float[][] input) {
//        float[][] output = new float[1][1];
//        tflite.runForMultipleInputsOutputs(new Object[]{input}, output);
//        return output[0][0];
//    }

//    //추가
//    public float predictSimilarity(float[][] input) {
//        float[][] output = new float[1][1];
//        Map<Integer, Object> outputMap = new HashMap<>();
//        outputMap.put(0, output);
//        tflite.runForMultipleInputsOutputs(new Object[]{input}, outputMap);
//        return output[0][0];
//    }



    public void close() {
        tflite.close();
    }
}