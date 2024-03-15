package com.example.demo.services;

import com.example.demo.common.Message;
import com.example.demo.common.MessageStatus;
import com.example.demo.models.Review;
import com.example.demo.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.demo.common.FormatChecker.*;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    public Message addReview(String isbn, String phoneNumber, String comment, Integer rate) {

        if (isbnChecker(isbn) && phoneNumberChecker(phoneNumber) && rate > 0 && rate < 6)
            return reviewRepository.addReview(isbn, phoneNumber, comment, rate);

        return new Message(MessageStatus.fail, MessageStatus.fail.toString());
    }

    public List<Review> getReviews(String isbn) {
        return reviewRepository.getReviews(isbn);
    }
}
