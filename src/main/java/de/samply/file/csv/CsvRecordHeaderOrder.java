package de.samply.file.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvRecordHeaderOrder {

  private Map<String, Integer> headerOrderMap = new HashMap<>();
  private List<String> headersInOrder;

  public void addHeader(String header, int order) {
    headerOrderMap.put(header, order);
  }

  public Integer getOrder(String header) {
    return headerOrderMap.get(header);
  }

  public List<String> getHeadersInOrder() {

    if (headersInOrder == null) {
      headersInOrder = fetchHeadersInOrder();
    }

    return headersInOrder;
  }

  private List<String> fetchHeadersInOrder() {

    List<String> headersList = new ArrayList<>();

    List<HeaderOrder> headerOrders = fetchHeaderOrders();
    Collections.sort(headerOrders);

    headerOrders.forEach(headerOrder -> headersList.add(headerOrder.getHeader()));

    return headersList;

  }

  private List<HeaderOrder> fetchHeaderOrders() {

    List<HeaderOrder> headerOrders = new ArrayList<>();

    headerOrderMap.entrySet().forEach(entry -> headerOrders.add(new HeaderOrder(entry.getKey(),
        entry.getValue())));

    return headerOrders;

  }

  private class HeaderOrder implements Comparable<HeaderOrder> {

    private String header;
    private Integer order;

    public HeaderOrder(String header, Integer order) {
      this.header = header;
      this.order = order;
    }

    @Override
    public int compareTo(HeaderOrder o) {
      return Integer.compare(this.order, o.order);
    }

    public String getHeader() {
      return header;
    }

  }

}
