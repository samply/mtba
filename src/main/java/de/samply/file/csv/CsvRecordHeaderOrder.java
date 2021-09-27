package de.samply.file.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CsvRecordHeaderOrder {

  private Map<String, Integer> headerOrderMap = new HashMap<>();
  private List<String> headersInOrder;

  public void addHeader(String header, int order) {
    headerOrderMap.put(header, order);
  }

  public void addHeaderAtLastPosition(String header) {
    Integer maxHeaderOrder = getMaxHeaderOrder();
    addHeader(header, maxHeaderOrder + 1);
  }

  /**
   * Get predefined order of header.
   *
   * @param header header.
   * @return header order.
   */
  public Integer getOrder(String header) {
    return headerOrderMap.get(header);
  }

  /**
   * Get list of headers in order.
   *
   * @return list of headers in order.
   */
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

  private Integer getMaxHeaderOrder() {
    int maxOrder = 0;
    for (Integer order : headerOrderMap.values()) {
      if (order != null && order > maxOrder) {
        maxOrder = order;
      }
    }

    return maxOrder;
  }

  /**
   * Remove headers.
   *
   * @param headers headers to be removed.
   */
  public void removeHeaders(Set<String> headers) {
    if (headers != null && headers.size() > 0) {
      headers.stream().forEach(header -> headerOrderMap.remove(header));
      headersInOrder = fetchHeadersInOrder();
    }
  }

  /**
   * Rename headers.
   *
   * @param oldHeaderToNewHeaderMap map old header - new header.
   */
  public void renameHeaders(Map<String, String> oldHeaderToNewHeaderMap) {

    boolean isChanged = false;

    for (Entry<String, String> entry : oldHeaderToNewHeaderMap.entrySet()) {

      String oldHeader = entry.getKey();
      String newHeader = entry.getValue();
      Integer order = headerOrderMap.get(oldHeader);
      if (order != null) {

        headerOrderMap.remove(oldHeader);
        headerOrderMap.put(newHeader, order);
        isChanged = true;

      }

    }

    if (isChanged) {
      headersInOrder = fetchHeadersInOrder();
    }

  }

}
