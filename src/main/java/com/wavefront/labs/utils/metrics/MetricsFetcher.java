package com.wavefront.labs.utils.metrics;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MetricsFetcher {

	private final int FETCH_SIZE = 50;

	private BufferedWriter writer;
	private String baseURL;
	private String token;
	private int totalMetrics = 0;
	private Gson gson;
	private OkHttpClient httpClient;

	public void start(String[] args) {

		long startTime = System.currentTimeMillis();

		System.out.println("Starting Metrics Fetcher...");

		baseURL = args[0];
		token = args[1];

		String metricName = "";
		if (args.length > 2) {
			metricName = args[2];
		}
		String fileName = "metrics.txt";
		if (args.length > 3) {
			fileName = args[3];
		}

		gson = new Gson();
		httpClient = new OkHttpClient();
		try {
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			fetchMetrics(metricName, "");
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = System.currentTimeMillis() - startTime;

		System.out.println();
		System.out.println("Finished Metrics Fetcher!");
		System.out.println("Metrics: " + totalMetrics);
		System.out.println("Time: " + elapsed);
	}

	private void fetchMetrics(String query, String lastFound) throws IOException {

		List<String> metrics = getMetrics(baseURL + "/chart/metrics/all?trie=true&q=" + query + "&p=" + lastFound + "&l=" + FETCH_SIZE);
		if (metrics != null) {
			for (String metric : metrics) {
				if (metric.endsWith(".")) {
					fetchMetrics(metric, "");
				} else {
					writer.write(metric);
					writer.newLine();
					totalMetrics++;

					if (totalMetrics % 50 == 0) {
						System.out.println();
						System.out.printf("%6d", totalMetrics);
					}
				}
			}

			if (metrics.size() == FETCH_SIZE) {
				fetchMetrics(query, metrics.get(metrics.size() - 2));
			}
		}
	}

	private List<String> getMetrics(String url) {
		int attempts = 0;
		while (attempts < 5) {
			try {
				attempts++;

				Request request = new Request.Builder()
						.addHeader("X-AUTH-TOKEN", token)
						.url(url)
						.get()
						.build();

				Response response = httpClient.newCall(request).execute();
				return (List<String>) gson.fromJson(response.body().string(), Map.class).get("metrics");

			} catch (Exception e) {
				System.out.println("ERROR: getHttpResponseStream: " + url);
				System.out.println("Attempt #: " + attempts);
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		MetricsFetcher metricsFetcher = new MetricsFetcher();
		metricsFetcher.start(args);
	}

}
