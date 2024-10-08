package com.awsbatch.config;

import org.springframework.batch.item.ItemProcessor;

import com.awsbatch.model.Product;


public class CustomItemProcessor implements ItemProcessor<Product, Product> {

	@Override
    public Product process(Product item) throws Exception {
        String discountStr = item.getDiscount();
        String priceStr = item.getPrice();

        if (discountStr != null && priceStr != null) {
            discountStr = discountStr.trim();
            priceStr = priceStr.trim();
            
            try {
                int discountPer = Integer.parseInt(discountStr);
                double originalPrice = Double.parseDouble(priceStr);
                double discount = (discountPer / 100.0) * originalPrice; 
                double finalPrice = originalPrice - discount;
                item.setDiscountedPrice(String.valueOf(finalPrice));
            } catch (NumberFormatException e) {
                System.err.println("Invalid discount or price value: " + discountStr + ", " + priceStr);
            }
        } else {
            System.err.println("Discount or price is null for product: " + item);
        }
        
        return item;
    }
}
