package com.comp301.a08shopping;

public class ProductImpl implements Product {
  // Create a class called ProductImpl that implements the Product interface.
  // At the bare minimum, your ProductImpl class must encapsulate a private string field to
  // represent the product's name, and a private double field to represent the product's base price.
  private final String name;
  private final double basePrice;
  private final double discount;

  public ProductImpl(String name, double basePrice) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    if (basePrice == 0.00) {
      throw new IllegalArgumentException("Base price cannot be 0.");
    }
    this.name = name;
    this.basePrice = basePrice;
    this.discount = 0.00;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public double getBasePrice() {
    return this.basePrice;
  }
}
