# Wavefront utils

A small collection of utilities I have for working with Wavefront metrics.

- DataQuery
- MetricReader
- MetricsFetcher

Depends on [Wavefront Sender](https://github.com/puckpuck.wavefront-sender)

### DataQuery

Allows you to execute any Wavefront Query Language expression and return a list of
MetricPoint objects (from Wavefront Sender).
```java
// initialize with Wavefront URL and Token
DataQuery dataQuery = new DataQuery(url, token);

// Execute your query (start/end time are seconds since epoch)
List<MetricPoint> metrics = dataQuery.execute(query, start, end); 
```

When used in conjunction with Wavefront Sender you can manipulate the data before sending
back to Wavefront as new metrics, backup the data to file, or send them to another
Wavefront proxy (pointing to a different Wavefront instance).
```java
DataQuery dataQuery = new DataQuery(url, token);
List<MetricPoint> metrics = dataQuery.execute(query, start, end);

Wavefront wavefront = new Wavefront(File);
metrics.each(wavefront::sendMetric);
``` 


### MetricReader

_Work in Progress_

Used to parse a file or Wavefront formatted metrics into a list of MetricPoint objects.

Parsing rules are ok, and metrics should well formed. If the file was created using Wavefront 
Sender they can be parsed by this.


### MetricsFetcher

_Work in Progress_

Used to get a list of all metrics available in Wavefront. Uses the metrics browser API.