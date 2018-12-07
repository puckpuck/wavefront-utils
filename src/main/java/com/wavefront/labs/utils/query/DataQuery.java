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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataQuery {

	private String baseURL;
	private String token;

	public DataQuery(String url, String token) {
		this.baseURL = url;
		this.token = token;
	}


	public List<MetricPoint> executeQuery(String query, long start, long end) {
		return executeQuery(query, start, end, null);
	}

	public List<MetricPoint> executeQuery(String query, long start, long end, Map<String, String> queryArgs) {

		QueryResult queryResult = getQueryResults(query, start, end, queryArgs);

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

	private QueryResult getQueryResults(String query, long start, long end, Map<String, String> queryArgs) {
		try {

			String encodedQuery = new URI(null, null, query, null).getRawPath().replaceAll("\\+", "%2B");

			HashMap<String, String> args = new HashMap();
			args.put("n", "DataQuery");
			args.put("g", "s");
			args.put("i", "false");
			args.put("autoEvents", "false");
			args.put("summarization", "MEAN");
			args.put("listMode", "false");
			args.put("strict", "true");
			args.put("sorted", "false");

			if (queryArgs != null) {
				args.putAll(queryArgs);
			}

			args.put("q", encodedQuery);
			args.put("s", "" + start);
			if (end != 0) {
				args.put("e", "" + end);
			}
			if (isObsoleteTime(start) && (queryArgs == null || !queryArgs.containsKey("includeObsoleteMetrics"))) {
				args.put("includeObsoleteMetrics", "true");
			}

			StringBuilder url = new StringBuilder(baseURL + "/api/v2/chart/api?");
			args.forEach((k, v) -> {
				url.append(k + "=" + v + "&");
			});

			OkHttpClient httpClient = new OkHttpClient();
			Request request = new Request.Builder()
					.addHeader("Authorization", "Bearer " + token)
					.url(url.toString())
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

		boolean isSeconds = (time < 1e10);

		long obsoleteSpan = 28L * 86400 * (isSeconds ? 1 : 1000);
		long current = System.currentTimeMillis() / (isSeconds ? 1000 : 1);
		return (time + obsoleteSpan < current);
	}
}
