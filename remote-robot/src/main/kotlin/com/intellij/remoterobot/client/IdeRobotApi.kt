// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.client

import com.intellij.remoterobot.data.ObjectContainer
import com.intellij.remoterobot.data.js.ExecuteScriptRequest
import retrofit2.Call
import retrofit2.http.*

interface IdeRobotApi {
    @POST("/component")
    fun findByLambda(@Body lambda: ObjectContainer): Call<FindComponentsResponse>

    @POST("/{containerId}/component")
    fun findByLambda(@Path("containerId") containerId: String, @Body lambda: ObjectContainer): Call<FindComponentsResponse>

    @POST("/components")
    fun findAllByLambda(@Body lambda: ObjectContainer): Call<FindComponentsResponse>

    @POST("/{containerId}/components")
    fun findAllByLambda(@Path("containerId") containerId: String, @Body lambda: ObjectContainer): Call<FindComponentsResponse>

    @POST("/xpath/component")
    fun findByXpath(@Body xpath: FindByXpathRequest): Call<FindComponentsResponse>

    @POST("/xpath/{containerId}/component")
    fun findByXpath(@Path("containerId") containerId: String, @Body xpath: FindByXpathRequest): Call<FindComponentsResponse>

    @POST("/xpath/components")
    fun findAllByXpath(@Body xpath: FindByXpathRequest): Call<FindComponentsResponse>

    @POST("/xpath/{containerId}/components")
    fun findAllByXpath(@Path("containerId") containerId: String, @Body xpath: FindByXpathRequest): Call<FindComponentsResponse>

    @POST("/{componentId}/parentOfComponent")
    fun findParentOf(@Path("componentId") componentId: String, @Body lambda: ObjectContainer): Call<FindComponentsResponse>

    @POST("/execute")
    fun execute(@Body lambda: ObjectContainer): Call<ExecuteResponse>

    @POST("/{componentId}/execute")
    fun execute(@Path("componentId") componentId: String, @Body lambda: ObjectContainer): Call<ExecuteResponse>

    @POST("/js/execute")
    fun execute(@Body request: ExecuteScriptRequest): Call<ExecuteResponse>

    @POST("/{componentId}/js/execute")
    fun execute(@Path("componentId") componentId: String, @Body request: ExecuteScriptRequest): Call<ExecuteResponse>

    @POST("/retrieveAny")
    fun retrieve(@Body lambda: ObjectContainer): Call<RetrieveResponse>

    @POST("/{componentId}/retrieveAny")
    fun retrieve(@Path("componentId") componentId: String, @Body lambda: ObjectContainer): Call<RetrieveResponse>

    @POST("/js/retrieveAny")
    fun retrieve(@Body request: ExecuteScriptRequest): Call<RetrieveResponse>

    @POST("/{componentId}/js/retrieveAny")
    fun retrieve(@Path("componentId") componentId: String, @Body request: ExecuteScriptRequest): Call<RetrieveResponse>

    @POST("/{componentId}/data")
    fun retrieveComponentData(@Path("componentId") componentId: String): Call<ComponentDataResponse>

    @GET("/screenshot")
    fun retrieve(): Call<RetrieveResponse>

    @GET("/{componentId}/screenshot")
    fun retrieve(
        @Path("componentId") componentId: String,
        @Query("isPaintingMode") isPaintingMode: Boolean = false
    ): Call<RetrieveResponse>
}