package com.example.demo.controllers;

import com.example.demo.common.Message;
import com.example.demo.models.Review;
import com.example.demo.services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class reviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<String> addReview(@RequestParam(value="ISBN") String isbn,
                                    @RequestParam(value="comment") String comment,
                                    @RequestParam(value="rate") Integer rate,
                                    HttpServletRequest request){

        String phoneNumber = (String)request.getAttribute("PhoneNumber");
        Message message = reviewService.addReview(isbn, phoneNumber, comment, rate);
        return ResponseEntity.ok(message.getMessage());
    }

    @GetMapping("/{isbn}")
    public List<Review> getReviews(@PathVariable(value="isbn") String isbn){
        return reviewService.getReviews(isbn);
    }
}
