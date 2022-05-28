package com.example.facerecognitionattendance.model;

public class Model {

    final ModelInfo FACENET = new ModelInfo(
            "FaceNet",
            "facenet.tflite",
            0.4f,
            10f,
            128,
            160
    );
    final ModelInfo FACENET_512 = new ModelInfo(
            "FaceNet-512",
            "facenet_512.tflite",
            0.3f,
            23.56f,
            512,
            160
    );
    final ModelInfo FACENET_QUANTIZED = new ModelInfo(
            "FaceNet Quantized" ,
            "facenet_int_quantized.tflite" ,
            0.4f ,
            10f ,
            128 ,
            160
    );
    final ModelInfo FACENET_512_QUANTIZED = new ModelInfo(
            "FaceNet-512 Quantized" ,
            "facenet_512_int_quantized.tflite" ,
            0.3f ,
            23.56f ,
            512 ,
            160
    );
}
