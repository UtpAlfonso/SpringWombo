package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.ReviewRequest;
import com.jugueteria.api.dto.response.ReviewResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @PostMapping("/product/{productId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(usuario, productId, request));
    }
     @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.findAll());
    }
}