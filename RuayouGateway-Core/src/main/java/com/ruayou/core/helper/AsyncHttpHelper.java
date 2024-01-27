package com.ruayou.core.helper;

import org.asynchttpclient.*;

import java.util.concurrent.CompletableFuture;



public class AsyncHttpHelper {
	private static final AsyncHttpHelper INSTANCE = new AsyncHttpHelper();
	private AsyncHttpClient asyncHttpClient;
	public static AsyncHttpHelper getInstance() {
		return INSTANCE;
	}
	public void initialized(AsyncHttpClient asyncHttpClient) {
		this.asyncHttpClient = asyncHttpClient;
	}

	public CompletableFuture<Response> executeRequest(Request request) {
		ListenableFuture<Response> future = asyncHttpClient.executeRequest(request);
		return future.toCompletableFuture();
	}

	public <T> CompletableFuture<T> executeRequest(Request request, AsyncHandler<T> handler) {
		ListenableFuture<T> future = asyncHttpClient.executeRequest(request, handler);
		return future.toCompletableFuture();
	}

}
