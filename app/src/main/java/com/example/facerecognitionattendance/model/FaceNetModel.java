package com.example.facerecognitionattendance.model;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import android.content.Context;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public class FaceNetModel {

    private Interpreter interpreter;
    private ImageProcessor imageTensorProcessor;

    public FaceNetModel(Context context,
                        ModelInfo model,
                        Boolean useGpu,
                        Boolean useXNNPack) {

        imageTensorProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(model.inputDims, model.inputDims, ResizeOp.ResizeMethod.BILINEAR)).add(new StandardizeOp())
                .build();

        // Initialize interpreter with GPU delegate
        Interpreter.Options options = new Interpreter.Options();
        CompatibilityList compatList = new CompatibilityList();

        if (compatList.isDelegateSupportedOnThisDevice()) {
            // if the device has a supported GPU, add the GPU delegate
            GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
            GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
            options.addDelegate(gpuDelegate);
        } else {
            // if the GPU is not supported, run on 4 threads
            options.setNumThreads(4);
        }

        options.setUseXNNPACK(useXNNPack);
        options.setUseNNAPI(true);


        interpreter = new Interpreter(new File(model.assetsFilename), options);
    }

    class StandardizeOp implements TensorOperator {

        @Override
        public TensorBuffer apply(TensorBuffer tensorBuffer) {
            float[] pixels = tensorBuffer.getFloatArray();
            float mean = average(pixels);
            Collection collection = (Collection) (new ArrayList(pixels.length));
            float[] num = pixels;

            for (int i = 0; i < pixels.length; ++i) {
                float temp = num[i];
                float diff = temp - mean;
                byte var = 2;
                Float flt = (float) Math.pow((double) diff, (double) var);
                collection.add(flt);
            }

            float num2 = CollectionsKt.sumOfFloat((Iterable) ((List) collection)) / (float) pixels.length;
            float std = (float)sqrt(num2);
            num2 = (float)pixels.length;
            num2 = 1.0F / (float)sqrt((double)num2);
            std = max(std, num2);
            int i = 0;

            for(int j = pixels.length; i < j; ++i) {
                pixels[i] = (pixels[i] - mean) / std;
            }

            TensorBuffer tensorBuffer1 = TensorBufferFloat.createFixedSize(tensorBuffer.getShape(), DataType.FLOAT32);
            TensorBuffer output = tensorBuffer1;
            output.loadArray(pixels);
            return output;
        }

        public float average(float[] num) {
            float sum = 0;

            for (int i = 0; i < num.length; i++) {
                sum += num[i];
            }
            return sum / num.length;
        }
    }

}
