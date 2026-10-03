package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.cart.CartItemDto;
import com.springboot.online_store.dtos.cart.UpdateCartItemDto;
import com.springboot.online_store.entities.Cart;
import com.springboot.online_store.entities.CartItem;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.exceptions.custom.InsufficientStockException;
import com.springboot.online_store.mappers.CartItemMapper;
import com.springboot.online_store.repositories.CartItemRepository;
import com.springboot.online_store.repositories.CartRepository;
import com.springboot.online_store.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemMapper mapper;

    @InjectMocks
    private CartItemService cartItemService;

    private CartItemDto cartItemDto(){
        return CartItemDto.builder()
                .productId(1L)
                .quantity(1)
                .build();
    }

    private CartItemDto cartItemDtoWithLargeQuantity(){
        return CartItemDto.builder()
                .productId(1L)
                .quantity(100)
                .build();
    }

    private Product productBuilder(){
        return new Product(
                "name",
                (BigDecimal.valueOf(100)),
                10,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Category()
        );
    }

    private UpdateCartItemDto updateCartItemDto;

    private UpdateCartItemDto updateCartItemDtoWithLargeQuantity;

    @BeforeEach
    void setUp() {
        updateCartItemDto =
                new UpdateCartItemDto(1);
        updateCartItemDtoWithLargeQuantity =
                new UpdateCartItemDto(100);
    }

    @Nested
    class CreateCartItem {
        @Test
        void createCartItemShouldWork() {
            var cart = mock(Cart.class);
            var cartItem = mock(CartItem.class);
            var infoDto = cartItemDto();
            var product = productBuilder();
            var cartItemDto = cartItemDto();

            Long id = 1L;
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);

            when(productRepository.findById(id))
                    .thenReturn(Optional.ofNullable(product));
            when(cartRepository.findById(id))
                    .thenReturn(Optional.ofNullable(cart));
            when(cartItemRepository.save(any(CartItem.class)))
                    .thenReturn(cartItem);
            when(mapper.toCartItemDto(any(CartItem.class)))
                    .thenReturn(infoDto);

            //WHEN
            var result = cartItemService.createCartItem(cartItemDto);

            //THEN
            assertThat(result).isEqualTo(infoDto);
            verify(productRepository)
                    .findById(id);
            verify(cartRepository)
                    .findById(id);
            verify(cartItemRepository)
                    .save(captor.capture());
            verify(mapper)
                    .toCartItemDto(any(CartItem.class));

            var capturedCategory = captor.getValue();
            assertThat(capturedCategory.getQuantity()).isEqualTo(cartItemDto.quantity());
            assertThat(capturedCategory.getProduct()).isSameAs(product);
        }

        @Test
        void createCartItemShouldThrowInsufficientStockException() {
            var cart = mock(Cart.class);
            var product = productBuilder();
            var cartItemDtoWithLargeQuantity = cartItemDtoWithLargeQuantity();
            Long id = 1L;

            when(productRepository.findById(id))
                    .thenReturn(Optional.ofNullable(product));
            when(cartRepository.findById(id))
                    .thenReturn(Optional.ofNullable(cart));

            //WHEN

            assertThrows(InsufficientStockException.class,
            () -> cartItemService.createCartItem(cartItemDtoWithLargeQuantity));

            //THEN
            verify(productRepository)
                    .findById(id);
            verify(cartRepository)
                    .findById(id);
            verify(cartItemRepository, never())
                    .save(any(CartItem.class));
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenProductNotFound(){
            Long id = 1L;
            var cartItemDto = cartItemDto();

            when(productRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> cartItemService.createCartItem(cartItemDto));

            verify(productRepository).findById(id);
            verify(cartItemRepository, never()).save(any(CartItem.class));
        }
    }

    @Nested
    class GetCartItems{
        @Test
        void getCartItemsShouldWork() {
            var infoDto = mock(CartItemDto.class);
            var cartItem = mock(CartItem.class);
            int pageSize = 10;
            int pageNumber = 0;
            var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            var infoDtoList = List.of(infoDto);
            var cartItemList = List.of(cartItem);

            when(cartItemRepository.findAll(eq(pageable)))
                    .thenReturn(new PageImpl<>(cartItemList));
            when(mapper.toCartItemDtoList(cartItemList))
                    .thenReturn(infoDtoList);

            var result = cartItemService.getCartItems(pageSize, pageNumber);

            assertThat(result).isEqualTo(infoDtoList);
            verify(cartItemRepository).findAll(eq(pageable));
            verify(mapper).toCartItemDtoList(cartItemList);
        }

        @ParameterizedTest
        @MethodSource("provideFailedPageParams")
        void getCartItemsShouldWorkWithoutPageParams(Integer pageSize, Integer pageNumber) {
            var infoDto = mock(CartItemDto.class);
            var cartItem = mock(CartItem.class);
            var pageable = Pageable.ofSize(BusinessConstants.DEFAULT_PAGE_SIZE)
                    .withPage(BusinessConstants.DEFAULT_PAGE_NUMBER);
            var infoDtoList = List.of(infoDto);
            var cartItemList = List.of(cartItem);

            when(cartItemRepository.findAll(eq(pageable)))
                    .thenReturn(new PageImpl<>(cartItemList));
            when(mapper.toCartItemDtoList(cartItemList))
                    .thenReturn(infoDtoList);

            var result = cartItemService.getCartItems(pageSize, pageNumber);

            assertThat(result).isEqualTo(infoDtoList);
            verify(cartItemRepository).findAll(eq(pageable));
            verify(mapper).toCartItemDtoList(cartItemList);
        }

        private static Stream<Arguments> provideFailedPageParams(){
            return Stream.of(
                    Arguments.of(null , null),
                    Arguments.of(-2, -2)
            );
        }
    }

    @Nested
    class DeleteCartItem {
        @Test
        void deleteCartItemShouldWork() {
            Long id = 1L;
            var cartItem = mock(CartItem.class);

            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.of(cartItem));

            cartItemService.deleteCartItem(id);

            verify(cartItemRepository)
                    .delete(cartItem);
            verify(cartItemRepository)
                    .findById(id);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWithCartItem(){
            Long id = 1L;
            var cartItem = mock(CartItem.class);

            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> cartItemService.deleteCartItem(id)
            );

            verify(cartItemRepository, never())
                    .delete(cartItem);
            verify(cartItemRepository)
                    .findById(id);
        }
    }

    @Nested
    class UpdateCartItem {
        @Test
        void updateCartItemShouldWork() {
            var cartItem = mock(CartItem.class);
            var infoDto = cartItemDto();
            var product = productBuilder();

            Long id = 1L;

            when(cartItem.getProduct())
                    .thenReturn(product);
            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.of(cartItem));
            when(mapper.toCartItemDto(any(CartItem.class)))
                    .thenReturn(infoDto);

            var result = cartItemService.updateCartItem(
                    id, updateCartItemDto
            );

            assertThat(result).isEqualTo(infoDto);

            verify(cartItem).getProduct();
            verify(cartItem).setQuantity(updateCartItemDto.quantity());
            verify(cartItemRepository).findById(id);
            verify(mapper).toCartItemDto(any(CartItem.class));
        }

        @Test
        void updateCartItemShouldThrowInsufficientStockException() {
            var cartItem = mock(CartItem.class);
            var product = mock(Product.class);
            Long id = 1L;

            when(cartItem.getProduct())
                    .thenReturn(product);
            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.ofNullable(cartItem));

            assertThrows(InsufficientStockException.class,
                    () -> cartItemService.updateCartItem(id, updateCartItemDtoWithLargeQuantity));

            verify(cartItemRepository)
                    .findById(id);
            verify(cartItem)
                    .getProduct();
        }

        @Test
        void shouldThrowEntityNotFoundException(){
            Long id = 1L;

            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> cartItemService.updateCartItem(id, updateCartItemDto));

            verify(cartItemRepository).findById(id);
        }
    }

    @Nested
    class GetCartItemById {
        @Test
        void getCartItemShouldWork() {
            Long id = 1L;
            var cartItem = mock(CartItem.class);
            var infoDto = mock(CartItemDto.class);

            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.of(cartItem));
            when(mapper.toCartItemDto(cartItem))
                    .thenReturn(infoDto);

            var result = cartItemService.getCartItemById(id);

            assertThat(result).isEqualTo(infoDto);
            verify(cartItemRepository)
                    .findById(id);
            verify(mapper)
                    .toCartItemDto(cartItem);
        }
        @Test
        void shouldThrowEntityNotFoundExceptionWithCartItem(){
            //GIVEN
            Long id = 1L;
            when(cartItemRepository.findById(id))
                    .thenReturn(Optional.empty());
            //WHEN & THEN
            assertThrows(
                    EntityNotFoundException.class,
                    () -> cartItemService.getCartItemById(id)
            );

            verify(cartItemRepository)
                    .findById(id);
        }
    }
}