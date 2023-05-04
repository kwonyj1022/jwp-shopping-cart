package cart.controller;

import cart.dto.product.ProductDto;
import cart.dto.product.ProductResponseDto;
import cart.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class JwpCartController {

    private final ProductService productService;

    public JwpCartController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<ProductDto> productDtos = productService.findAll();
        List<ProductResponseDto> response = productDtos.stream()
                .map(ProductResponseDto::fromDto)
                .collect(toList());
        model.addAttribute("products", response);
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<ProductDto> productDtos = productService.findAll();
        List<ProductResponseDto> response = productDtos.stream()
                .map(ProductResponseDto::fromDto)
                .collect(toList());
        model.addAttribute("products", response);
        return "admin";
    }
}
