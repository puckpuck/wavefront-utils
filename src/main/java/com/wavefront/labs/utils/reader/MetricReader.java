package com.wavefront.labs.utils.reader;

import com.wavefront.labs.sender.MetricPoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MetricReader {

	private File file;
	private ArrayList<MetricPoint> metricPoints;

	public MetricReader(String filename) {
		file = new File(filename);
	}

	public MetricReader(File file) {
		this.file = file;
	}

	public void read() throws IOException {

		metricPoints = new ArrayList();

		try (Stream<String> lines = Files.readAllLines(file.toPath()).stream()) {
			lines.forEach(this::processLine);
		}
	}

	public List<MetricPoint> getMetricPoints() {
		return metricPoints;
	}

	private void processLine(String line) {


		String[] parts = line.split(" ");
		MetricPoint metricPoint = new MetricPoint();

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

		metricPoints.add(metricPoint);

	}

	private String[] stripQuotes(String[] vals) {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = stripQuotes(vals[i]);
		}
		return vals;
	}

	private String stripQuotes(String val) {
		if (val.startsWith("\"")) {
			return val.substring(1, val.length() - 1);
		} else {
			return val;
		}
	}
}
