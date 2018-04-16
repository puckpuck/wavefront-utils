package com.wavefront.labs.utils.query;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryResult {
		private ArrayList<TimeseriesResult> timeseries;
		private HashMap stats;
		private int granularity;
		private String query;
		private String name;

		public ArrayList<TimeseriesResult> getTimeseries() {
			return timeseries;
		}

		public void setTimeseries(ArrayList<TimeseriesResult> timeseries) {
			this.timeseries = timeseries;
		}

		public HashMap getStats() {
			return stats;
		}

		public void setStats(HashMap stats) {
			this.stats = stats;
		}

		public int getGranularity() {
			return granularity;
		}

		public void setGranularity(int granularity) {
			this.granularity = granularity;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}