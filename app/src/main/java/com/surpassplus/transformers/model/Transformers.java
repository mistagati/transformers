package com.surpassplus.transformers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Transformers {

    @SerializedName("transformers")
    @Expose
    private ArrayList<Transformer> transformers;

    public ArrayList<Transformer> getTransformers() {
        return transformers;
    }
}
