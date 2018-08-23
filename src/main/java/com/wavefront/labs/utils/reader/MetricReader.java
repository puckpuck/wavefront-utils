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
		MetricPoint metricPoint = MetricPointParser.parseLine(line);
		metricPoints.add(metricPoint);
	}

}
