package com.sporthub.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sporthub.data.model.CartItem;

import java.util.List;

@Dao
public interface CartDao {
    @Insert
    long insert(CartItem cartItem);
    
    @Update
    void update(CartItem cartItem);
    
    @Delete
    void delete(CartItem cartItem);
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    LiveData<List<CartItem>> getCartItems(String userId);
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    CartItem getCartItemByProductId(String userId, String productId);
    
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCart(String userId);
    
    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE userId = :userId")
    LiveData<Double> getTotalPrice(String userId);
    
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    LiveData<Integer> getCartItemCount(String userId);
}
