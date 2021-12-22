package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.mapper.ReviewMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.CreateReviewRequest;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private ProductService mockProductService;
    @Mock
    private UserService mockUserService;
    @Mock
    private ReviewMapper mockReviewMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper mockProductMapper;

    private ReviewService reviewServiceUnderTest;

    @BeforeEach
    void setUp() {
        reviewServiceUnderTest = new ReviewService(mockReviewRepository, mockProductService, mockUserService, mockReviewMapper, productRepository, mockProductMapper);
    }

    @Test
    void testCreateReview() {

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        //product.setCreatedDate(LocalDate.now());
        product.setCreatedBy("createdBy");

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        CreateReviewRequest request = new CreateReviewRequest();
        request.setIntent("header");
        //request.setContent("body");
        request.setRating(4);
        request.setProductId(product.getId());

        when(mockUserService.isLoggedIn()).thenReturn(true);

        when(mockProductService.getProduct(1L)).thenReturn(product);

        //when(mockReviewRepository.findTopByProductAndUserOrderByIdDesc(any(Product.class), any(User.class)))
        //        .thenReturn(Optional.of(new Review()));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        when(mockReviewRepository.save(new Review())).thenReturn(new Review());

        when(mockReviewMapper.map(any(CreateReviewRequest.class), any(Product.class), any(User.class))).thenReturn(new Review());

        reviewServiceUnderTest.createReview(request);

    }


    @Test
    void testFindMatches() {

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("name");
        productResponse.setPrice(5.0);
        productResponse.setDescription("description");
        productResponse.setImagePath("imagePath");
        productResponse.setUnitInStock(90);
        //productResponse.setCreatedDate(LocalDate.now());

        //Set<ProductResponse> expectedResult = new HashSet<>(Collections.singletonList(productResponse));

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        //product.setCreatedDate(LocalDate.now());
        product.setCreatedBy("createdBy");

        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setIntent("intent");
        review.setContent("content");;
        review.setUser(user);
        review.setProduct(product);

        List<Review> reviewList = Collections.singletonList(review);
        when(mockReviewRepository.findAll()).thenReturn(reviewList);

        when(mockUserService.getUser(1L)).thenReturn(user);
        when(mockUserService.getCurrentUser()).thenReturn(user);

        when(mockReviewRepository.findReviewsByUser_Id(user.getId())).thenReturn(reviewList);

        LinkedHashSet<ProductResponse> productResponses = new LinkedHashSet<>(Collections.singletonList(productResponse));
        //when(mockProductMapper.entitiesToDTOs(new HashSet<>(Collections.singletonList(product)))).thenReturn(productResponses);

        Set<ProductResponse> result = reviewServiceUnderTest.findMatches();
    }


    @Test
    void testGetReviewId() {

        Review review = new Review();
        review.setId(1L);
        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        when(mockReviewMapper.mapToReviewResponse(review)).thenReturn(new ReviewResponse());


        final ReviewResponse result = reviewServiceUnderTest.getReviewId(1L);
    }

    @Test
    void testDeleteReview() {
        Review review = new Review();
        review.setId(1L);
        reviewServiceUnderTest.deleteReview(1L);

        verify(mockReviewRepository).deleteById(1L);
    }

    @Test
    void testGetReview() {
        final Review expectedResult = new Review();
        expectedResult.setId(1L);

        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(expectedResult));

        final Review result = reviewServiceUnderTest.getReview(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetReviews() {

        GetReviewsRequest request = new GetReviewsRequest();
        request.setProductId(1L);
        request.setRating(5);

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
       // product.setCreatedDate(LocalDate.now());
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);
        review.setRating(5);
        Page<Review> reviews = new PageImpl<>(Collections.singletonList(review));
        when(mockReviewRepository.findReviewsByProductIdAndRatingOrderByIdDesc
                (eq(product.getId()), eq(5), any(Pageable.class))).thenReturn(reviews);

        //when(mockReviewRepository.findReviewsByProductId(eq(product.getId()), any(Pageable.class))).thenReturn(reviews);

        when(mockReviewMapper.entitiesToEntityDTOs(Collections.singletonList(review)))
                .thenReturn(Collections.singletonList(new ReviewResponse()));

        final Page<ReviewResponse> result = reviewServiceUnderTest.getReviews(request, PageRequest.of(0, 20));

    }





}
