package com.flashsale.service;

import com.flashsale.common.BizException;
import com.flashsale.entity.PointsLog;
import com.flashsale.entity.User;
import com.flashsale.entity.UserInventory;
import com.flashsale.mapper.PointsLogMapper;
import com.flashsale.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private PointsLogMapper pointsLogMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ConfigService configService;

    @Spy
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void listProductShouldMoveInventoryToSellerAndReturnFeePoints() {
        UserInventory inventory = new UserInventory();
        inventory.setId(10L);
        inventory.setUserId(1L);
        inventory.setStatus(1);
        inventory.setPrice(new BigDecimal("100.00"));

        User user = new User();
        user.setId(1L);
        user.setPoints(new BigDecimal("5.00"));

        doReturn(inventory).when(inventoryService).getById(10L);
        doReturn(true).when(inventoryService).updateById(inventory);
        when(configService.getListingFeeRate()).thenReturn(new BigDecimal("0.10"));
        when(userMapper.selectById(1L)).thenReturn(user);

        inventoryService.listProduct(1L, 10L);

        assertEquals(2, inventory.getStatus());
        assertEquals(0, user.getPoints().compareTo(new BigDecimal("15.00")));
        verify(inventoryService).updateById(inventory);
        verify(userMapper).updateById(argThat(updated ->
                updated.getId().equals(1L)
                        && updated.getPoints().compareTo(new BigDecimal("15.00")) == 0));
        verify(pointsLogMapper).insert(argThat((PointsLog log) ->
                log.getUserId().equals(1L)
                        && log.getType().equals(3)
                        && log.getChangeAmount().compareTo(new BigDecimal("10.00")) == 0));
    }

    @Test
    void listProductShouldRejectInventoryOwnedByOtherUser() {
        UserInventory inventory = new UserInventory();
        inventory.setId(10L);
        inventory.setUserId(2L);
        inventory.setStatus(1);

        doReturn(inventory).when(inventoryService).getById(10L);

        assertThrows(BizException.class, () -> inventoryService.listProduct(1L, 10L));

        verify(inventoryService, never()).updateById(any(UserInventory.class));
    }

    @Test
    void buyListedProductShouldRejectSelfPurchase() {
        UserInventory inventory = new UserInventory();
        inventory.setId(20L);
        inventory.setUserId(1L);
        inventory.setStatus(2);

        doReturn(inventory).when(inventoryService).getById(20L);

        assertThrows(BizException.class, () -> inventoryService.buyListedProduct(1L, 20L));

        verify(userMapper, never()).selectById(any());
        verify(inventoryService, never()).updateById(any(UserInventory.class));
    }

    @Test
    void buyListedProductShouldTransferInventoryToBuyer() {
        UserInventory inventory = new UserInventory();
        inventory.setId(21L);
        inventory.setUserId(9L);
        inventory.setStatus(2);
        inventory.setSource(1);

        User buyer = new User();
        buyer.setId(1L);
        buyer.setStatus(0);

        doReturn(inventory).when(inventoryService).getById(21L);
        doReturn(true).when(inventoryService).updateById(inventory);
        when(userMapper.selectById(1L)).thenReturn(buyer);

        inventoryService.buyListedProduct(1L, 21L);

        assertEquals(1L, inventory.getUserId());
        assertEquals(1, inventory.getStatus());
        assertEquals(2, inventory.getSource());
        verify(inventoryService).updateById(inventory);
    }
}
