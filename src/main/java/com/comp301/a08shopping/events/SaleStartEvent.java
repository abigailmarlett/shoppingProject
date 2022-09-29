package com.comp301.a08shopping.events;

import com.comp301.a08shopping.Product;
import com.comp301.a08shopping.Store;

public class SaleStartEvent implements StoreEvent {
  private final Product product;
  private final Store store;

  public SaleStartEvent(Product product, Store store) {
    if (product == null || store == null) {
      throw new IllegalArgumentException("Argument Invalid");
    }
    this.product = product;
    this.store = store;
  }

  @Override
  public Product getProduct() {
    return this.product;
  }

  @Override
  public Store getStore() {
    return this.store;
  }
  // Represents the event where a store announces a sale for a particular product.
}
