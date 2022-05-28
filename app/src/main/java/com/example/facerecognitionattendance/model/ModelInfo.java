package com.example.facerecognitionattendance.model;

public class ModelInfo {
    public String name;
    public String assetsFilename;
    public float cosineThreshold;
    public float l2Threshold;
    public int outputDims;
    public int inputDims;

    public ModelInfo (String name, String assetsFilename, float cosineThreshold, float l2Threshold, int outputDims, int inputDims) {
        this.name = name;
        this.assetsFilename = assetsFilename;
        this.cosineThreshold = cosineThreshold;
        this.l2Threshold = l2Threshold;
        this.outputDims = outputDims;
        this.inputDims = inputDims;
    }
}
