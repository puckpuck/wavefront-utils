package com.wavefront.labs.utils.query;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeseriesResult {

	private ArrayList<ArrayList<Double>> data;
	private String host;
	private String label;
	private HashMap<String, String> tags;

	public ArrayList<ArrayList<Double>> getData() {
		return data;
	}

	public void setData(ArrayList<ArrayList<Double>> data) {
		this.data = data;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public HashMap<String, String> getTags() {
		return tags;
	}

	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}
}
