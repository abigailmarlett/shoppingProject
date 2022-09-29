package com.comp301.a08shopping.events;

import com.comp301.a08shopping.Product;
import com.comp301.a08shopping.Store;

public class PurchaseEvent implements StoreEvent {
  private final Product product;
  private final Store store;

  public PurchaseEvent(Product product, Store store) {
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
  // Represents the event where someone purchases a copy of a product from a store.
}
