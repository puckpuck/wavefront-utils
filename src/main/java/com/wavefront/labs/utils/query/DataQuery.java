package com.wavefront.labs.utils.query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wavefront.labs.sender.MetricPoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DataQuery {

	private String baseURL;
	private String token;

	public DataQuery(String url, String token) {
		this.baseURL = url;
		this.token = token;
	}


	public List<MetricPoint> executeQuery(String query, long start, long end) {

		QueryResult queryResult = getQueryResults(query, start, end);

		ArrayList<MetricPoint> metricPoints = new ArrayList();

		if (queryResult != null && queryResult.getTimeseries() != null) {
			for (TimeseriesResult ts : queryResult.getTimeseries()) {
				for (ArrayList<Double> data : ts.getData()) {
					MetricPoint metricPoint = new MetricPoint();
					metricPoint.setMetric(ts.getLabel());
					metricPoint.setValue(data.get(1));
					metricPoint.setTimestamp(data.get(0).longValue());
					metricPoint.setSource(ts.getHost());
					metricPoint.setTags(ts.getTags());
					metricPoints.add(metricPoint);
				}
			}
		}

		return metricPoints;
	}

	private QueryResult getQueryResults(String query, long start, long end) {
		try {

			

			String encodedQuery = new URI(null, null, query, null).getRawPath().replaceAll("\\+", "%2B");

			String url = baseURL + "/api/v2/chart/api";
			url += "?n=DataQuery";
			url += "&q=" + encodedQuery;
			url += "&s=" + start;
			if (end != 0) {
				url += "&e=" + end;
			}
			url += "&g=s";
			url += "&i=false";
			url += "&autoEvents=false";
			url += "&summarization=MEAN";
			url += "&listMode=false";
			url += "&strict=true";
			if (isObsoleteTime(start)) {
				url += "&includeObsoleteMetrics=true";
			}
			url += "&sorted=false";

			OkHttpClient httpClient = new OkHttpClient();
			Request request = new Request.Builder()
					.addHeader("X-AUTH-TOKEN", token)
					.url(url)
					.get()
					.build();
			try (Response response = httpClient.newCall(request).execute()) {
				Gson gson = new Gson();
				return gson.fromJson(response.body().string(), QueryResult.class);
			} catch (IOException | NullPointerException | JsonSyntaxException e) {
				e.printStackTrace();
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return null;
	}


	private boolean isObsoleteTime(long time) {
		if (time == 0) {
			return false;
		}

		long obsoleteSpan = 28 * 24 * 60 * 60;
		long current = System.currentTimeMillis() / 1000;
		return (time + obsoleteSpan < current);
	}
}
