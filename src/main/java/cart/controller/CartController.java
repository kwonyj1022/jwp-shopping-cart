package cart.controller;

import cart.auth.AuthorizationPrincipal;
import cart.domain.member.Member;
import cart.dto.cart.CartItemDto;
import cart.dto.cart.CartItemResponseDto;
import cart.dto.member.MemberDto;
import cart.service.AuthService;
import cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/cart/items")
public class CartController {

    private final AuthService authService;
    private final CartService cartService;

    public CartController(AuthService authService, CartService cartService) {
        this.authService = authService;
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> items(@AuthorizationPrincipal Member member) {
        MemberDto memberDto = authService.findMemberByEmail(member.getEmail());
        List<CartItemDto> cartItemDtos = cartService.findAllUserItems(memberDto);
        List<CartItemResponseDto> response = cartItemDtos.stream()
                .map(CartItemResponseDto::fromDto)
                .collect(toList());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CartItemResponseDto> addItem(@PathVariable("id") Long productId, @AuthorizationPrincipal Member member) {
        MemberDto memberDto = authService.findMemberByEmail(member.getEmail());
        CartItemDto dto = cartService.addItem(memberDto, productId);
        CartItemResponseDto response = CartItemResponseDto.fromDto(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/cart/items/" + response.getProductId()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        cartService.deleteById(id);
        return ResponseEntity.ok()
                .build();
    }
}
