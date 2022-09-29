package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;
import com.comp301.a08shopping.exceptions.OutOfStockException;
import com.comp301.a08shopping.exceptions.ProductNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreImpl implements Store {
  private final String name;
  private final List<StoreObserver> storeObserverList;
  private final HashMap<Product, Integer> inventoryHash;
  private final HashMap<Product, Double> discountHash;
  // In addition to these three fields, you'll also need to store inventory information about each
  // product
  // (i.e., how many copies of each product is in stock at the store), and sale information about
  // each product
  // (i.e. the discount percentage if the product is on sale).

  public StoreImpl(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name invalid");
    }
    this.name = name;
    this.storeObserverList = new ArrayList<>();
    this.inventoryHash = new HashMap<>();
    this.discountHash = new HashMap<>();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void addObserver(StoreObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Null observer");
    }
    this.storeObserverList.add(observer);
  }

  @Override
  public void removeObserver(StoreObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Null observer");
    }
    this.storeObserverList.remove(observer);
  }

  @Override
  public List<Product> getProducts() {
    /** Gets (a copy of) the list of all products offered by the store */
    List<Product> newList = new ArrayList<>();
    newList.addAll(inventoryHash.keySet());
    return newList;
  }

  @Override
  public Product createProduct(String name, double basePrice, int inventory) {
    /**
     * Creates a new product in the store. Throws an IllegalArgumentException for invalid values of
     * name, basePrice, and inventory
     */
    if (name == null || basePrice < 0 || inventory < 0) {
      throw new IllegalArgumentException("Invalid Argument.");
    }
    ProductImpl product = new ProductImpl(name, basePrice);
    inventoryHash.put(product, inventory);
    discountHash.put(product, 0.0);
    return product;
  }

  @Override
  public ReceiptItem purchaseProduct(Product product) {
    /**
     * Updates a product's inventory to reflect that one copy of the product was purchased (i.e.
     * decrements the product's inventory integer value). Emits the appropriate StoreEvent or
     * StoreEvents that describe the action. Throws a ProductNotFoundException if the specified
     * product is not sold by the store, an IllegalArgumentException if the product is null, or an
     * OutOfStockException if the product is out of stock. Returns a ReceiptItem object representing
     * the sale
     */
    if (product == null) {
      throw new IllegalArgumentException("The product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product not found.");
    }
    if (!getIsInStock(product)) {
      throw new OutOfStockException("Out of stock.");
    }
    PurchaseEvent purchase = new PurchaseEvent(product, this);
    notify(purchase);
    inventoryHash.replace(product, inventoryHash.get(product) - 1);
    ReceiptItem receipt = new ReceiptItemImpl(product.getName(), getSalePrice(product), getName());
    if (!getIsInStock(product)) {
      notify(new OutOfStockEvent(product, this));
    }
    return receipt;
  }

  @Override
  public void restockProduct(Product product, int numItems) {
    /**
     * Adds the specified number to the store's inventory for a particular product. Emits the
     * appropriate StoreEvent if the product was previously out of stock. Throws a
     * ProductNotFoundException if the specified product is not sold by the store, an
     * IllegalArgumentException if the product is null, or an IllegalArgumentException if numItems
     * is negative
     */
    if (product == null || numItems < 0) {
      throw new IllegalArgumentException("Invalid parameter");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    if (!getIsInStock(product)) {
      notify(new BackInStockEvent(product, this));
    }
    inventoryHash.replace(product, getProductInventory(product) + numItems);
  }

  @Override
  public void startSale(Product product, double percentOff) {
    /**
     * Starts a sale for a particular product by updating or setting the product's percentOff value
     * and emitting the appropriate StoreEvent. Throws a ProductNotFoundException if the specified
     * product is not sold by the store, an IllegalArgumentException if the product is null, or a
     * IllegalArgumentException if percentOff is not between 0.0 and 1.0
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    if (percentOff > 1.0 || percentOff < 0.0) {
      throw new IllegalArgumentException("Discount not allowed.");
    }
    discountHash.replace(product, percentOff);
    notify(new SaleStartEvent(product, this));
  }

  @Override
  public void endSale(Product product) {
    /**
     * Ends a sale for a particular product by removing or resetting the product's percentOff value.
     * Emits the appropriate StoreEvent. Throws a ProductNotFoundException if the specified product
     * is not sold by the store, or an IllegalArgumentException if the product is null
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    discountHash.replace(product, 0.0);
    notify(new SaleEndEvent(product, this));
  }

  @Override
  public int getProductInventory(Product product) {
    /**
     * Gets the number of copies in stock in the store's inventory for a particular product. Throws
     * a ProductNotFoundException if the product is not sold by the store, or an
     * IllegalArgumentException if the product is null
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return inventoryHash.get(product);
  }

  @Override
  public boolean getIsInStock(Product product) {
    /**
     * Determines whether a particular product is in stock in the store. Returns true if the product
     * is in stock, or false if it is not. Throws a ProductNotFoundException if the product is not
     * sold by the store, or an IllegalArgumentException if the product is null
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return getProductInventory(product) > 0;
  }

  @Override
  public double getSalePrice(Product product) {
    /**
     * Gets the store's sale price for a particular product in dollars, rounded to the nearest cent.
     * The sale price for a product is equal to the product's base price x (1.0 - percentOff), where
     * percentOff is the sale amount specified by startSale() for the product. Make sure to round
     * the resulting value to the nearest cent. Throws a ProductNotFoundException if the product is
     * not sold by the store, or an IllegalArgumentException if the product is null
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    double afterDiscount = 1.0 - discountHash.get(product);
    return product.getBasePrice() * afterDiscount;
  }

  @Override
  public boolean getIsOnSale(Product product) {
    /**
     * Determines whether a particular product is on sale at the store. Returns true if the product
     * is on sale, or false if it is not. A product is on sale if its sale price is less than its
     * base price. Throws a ProductNotFoundException if the product is not sold by the store, or an
     * IllegalArgumentException if the product is null
     */
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return getSalePrice(product) < product.getBasePrice();
  }

  public void notify(StoreEvent e) {
    for (StoreObserver o : storeObserverList) {
      o.update(e);
    }
  }
}
