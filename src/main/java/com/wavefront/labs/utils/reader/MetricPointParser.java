package com.wavefront.labs.utils.reader;

import com.wavefront.labs.sender.MetricPoint;

public class MetricPointParser {

	public static MetricPoint parseLine(String line) {
		MetricPoint metricPoint = new MetricPoint();

		String[] parts = line.split(" ");

		metricPoint.setMetric(parts[0]);
		metricPoint.setValue(Double.valueOf(parts[1]));
		metricPoint.setTimestamp(Long.valueOf(parts[2]));

		for (int i = 3; i < parts.length; i++) {
			if (!parts[i].equals("")) {
				String[] tagParts = stripQuotes(parts[i].split("="));
				if (tagParts[0].equals("source") || tagParts[0].equals("host")) {
					metricPoint.setSource(tagParts[1]);
				} else {
					metricPoint.addTag(tagParts[0], tagParts[1]);
				}
			}
		}

		return metricPoint;
	}

	private static String[] stripQuotes(String[] vals) {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = stripQuotes(vals[i]);
		}
		return vals;
	}

	private static String stripQuotes(String val) {
		if (val.startsWith("\"")) {
			return val.substring(1, val.length() - 1);
		} else {
			return val;
		}
	}
}
