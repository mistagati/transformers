package com.surpassplus.transformers.webservice;


import com.surpassplus.transformers.model.Transformer;
import com.surpassplus.transformers.model.Transformers;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TransformerAPIInterface {

    @GET("/transformers")
    Call<Transformers> getTransformers();

    @POST("/transformers")
    Call<Transformer> createTransformer(@Body Transformer transformer);

    @PUT("/transformers")
    Call<Transformer> updateTransformer(@Body Transformer transformer);

    @DELETE("/transformers/{id}")
    Call<ResponseBody> deleteTransformer(@Path("id") String id);

}
