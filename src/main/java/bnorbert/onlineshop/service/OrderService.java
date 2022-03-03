package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.OrderBuilder;
import bnorbert.onlineshop.mapper.OrderMapper;
import bnorbert.onlineshop.repository.*;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import bnorbert.onlineshop.transfer.order.OrdersResponses;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrdersWordsRepository ordersWordsRepository;
    private final EntityManager entityManager;
    private final CartMapper cartMapper;

    public OrderService(OrderRepository orderRepository, CartService cartService, CartRepository cartRepository,
                        UserService userService, OrderMapper orderMapper,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository, OrdersWordsRepository ordersWordsRepository, EntityManager entityManager, CartMapper cartMapper) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.ordersWordsRepository = ordersWordsRepository;
        this.entityManager = entityManager;
        this.cartMapper = cartMapper;
    }

    public List<CartResponse> getGodViewOverCarts(String categoryName, double lowerBound, double upperBound) {
        log.info("Retrieving carts");
        SearchSession searchSession = Search.session(entityManager);
        List<Cart> hits = searchSession.search(Cart.class)
                .where( f -> f.bool()
                        .must( f.nested()
                                .objectField("lineItems")
                                .nest(f.bool()
                                        .must(f.range()
                                                .field( "lineItems.subTotal")
                                                .between(lowerBound,  upperBound))
                                        //.between(100.0 ,  10000.0))
                                        .must(f.match()
                                                .field( "lineItems.product")
                                                .matching( categoryName))
                                ))
                ).fetchHits( 40 );

        return cartMapper.entitiesToEntityDTOs(hits);
    }


    public void createOrder(CreateAddressRequest request) {
        log.info("Creating order : {}", request);

        Cart cart = cartRepository
                .findByUser_Id( userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ( "There is no cart for user " + userService.getCurrentUser().getId() ));

        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        Order order = OrderBuilder.createOrder( o -> {
            o.forUser(userService.getCurrentUser());
            o.toAddress(request);
        });

        getOrderDetails(cartItemList, order);

        order.setGrandTotal(cart.getSum());
        orderRepository.save(order);
        cartService.clearCart(cart);

        copyDetails(cartItemList, order);
    }


    private void getOrderDetails(List<CartItem> cartItemList, Order order) {
        log.info("Moving inventory ");

        cartItemList.forEach( cartItem -> {
            order.addCartItem(cartItem);
            Product product = cartItem.getProduct();
            updateStock(cartItem, product);

            productRepository.save(product);
            cartItemRepository.save(cartItem);
        });

    }

    private void updateStock(CartItem cartItem, Product product) {
        log.info("Updating product stock");
        product.setUnitInStock( product.getUnitInStock() - cartItem.getQty());
        if(product.getUnitInStock() < 0 ){
            product.setUnitInStock(0);
        }
        else if(product.getUnitInStock() < 1 ){
            product.setIsAvailable( false );
        }
    }

    private void copyDetails(List<CartItem> cartItemList, Order order) {
        log.info("Splitter ");

        cartItemList.forEach(cartItem -> {

            Product product = cartItem.getProduct();

            OrderWord identifier = new OrderWord();
            identifier.setIndexOfWord(0);
            identifier.setDocId(order.getId());
            identifier.setWord(product.getName());
            identifier.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier);

            OrderWord identifier1 = new OrderWord();
            identifier1.setIndexOfWord(1);
            identifier1.setDocId(order.getId());
            identifier1.setWord(product.getBrandName());
            identifier1.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier1);

            OrderWord identifier2 = new OrderWord();
            identifier2.setIndexOfWord(2);
            identifier2.setDocId(order.getId());
            identifier2.setWord(product.getCategoryName());
            identifier2.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier2);

            OrderWord identifier3 = new OrderWord();
            identifier3.setIndexOfWord(3);
            identifier3.setDocId(order.getId());
            identifier3.setWord(order.getCity());
            identifier3.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier3);

            OrderWord identifier4 = new OrderWord();
            identifier4.setIndexOfWord(4);
            identifier4.setDocId(order.getId());
            identifier4.setWord(userService.getCurrentUser().getEmail());
            identifier4.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier4);

            OrderWord identifier5 = new OrderWord();
            identifier5.setIndexOfWord(5);
            identifier5.setDocId(order.getId());
            identifier5.setWord(order.getId().toString());
            identifier5.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier5);
        });
    }

    public OrdersResponses getOrders(OrderRequest request, String query, OrderTypeEnum orderType, int pageNumber) {
        log.info("Retrieving orders");
        long numberOfDocs = orderRepository.count();
        String[] words = query.split(" ");

        Map<Long, Double> hashMap = new HashMap<>();
        for (String s : words) {
            List<Long> docs = ordersWordsRepository.findDocIdContainingWord(s);

            double inverseDocumentFrequency;
            if ( Math.log((double) numberOfDocs / docs.size()) > 0) {
                inverseDocumentFrequency = Math.log((double) numberOfDocs / docs.size());
            } else {
                inverseDocumentFrequency = 0.01;
            }

            double termFrequency;
            for (long doc : docs) {
                double docLengthAvr;
                int wordCount = ordersWordsRepository.getWordCountInDoc(s, doc);
                List<Integer> length = ordersWordsRepository.getProductDescriptionLength(s, doc);
                docLengthAvr = length.stream()
                        .mapToDouble(Integer::doubleValue)
                        .average().orElse(1.0);
                termFrequency = wordCount / docLengthAvr;
                double tfidf = termFrequency * inverseDocumentFrequency;

                hashMap.put(doc, hashMap.getOrDefault(doc, (double) 0) + tfidf);
            }
        }

        Map<Long, Double> sortedMap = sortByValueReversed(hashMap);
        log.info(sortedMap + " sortedmap");

        Set<Long> ids = new LinkedHashSet<>(sortedMap.keySet());
        List<Order> result = new ArrayList<>();
        if(!ids.isEmpty()) {
            for (Long orderId : ids) {
                Optional<Order> order = orderRepository.findById(orderId);
                order.ifPresent(result::add);
            }if(result.isEmpty()){
                throw new ResourceNotFoundException("empty list");
            }
        }else {
            throw new ResourceNotFoundException("ids not found");
        }

        return getOrdersResponses(request, orderType, pageNumber, ids, result);
    }

    private OrdersResponses getOrdersResponses(OrderRequest request, OrderTypeEnum orderType,
                                               int pageNumber, Set<Long> ids,
                                               List<Order> result
    ) {
        if (orderType == OrderTypeEnum.BETWEEN) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Order> searchResult = searchSession.search(Order.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.id()
                                .matchingAny(ids));
                        if (request != null && request.getYear() != null && request.getMonth() != null && request.getDay() != null &&
                                request.get_year() != null && request.get_month() != null && request.get_day() != null) {
                            b.must(f.range()
                                    .field("createdDate")
                                    .between(LocalDateTime.of(request.getYear(), request.getMonth(), request.getDay(), 0, 0),
                                            LocalDateTime.of(request.get_year(), request.get_month(), request.get_day(), 0, 0)));
                        }
                    })).fetch(pageNumber * 4, 4);

            List<Order> orders = searchResult.hits();
            long totalHitCount = searchResult.total().hitCount();
            int lastPage = (int) (totalHitCount / 4);
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }

            List<OrderResponse> response = orderMapper.entitiesToDTOs(orders);

            return new OrdersResponses(Optional.of(response).orElseThrow(() -> new ResourceNotFoundException("OrderType.BETWEEN issue")));
        }

        if (orderType == OrderTypeEnum.TEST) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Order> searchResult = searchSession.search(Order.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.id()
                                .matchingAny(ids));
                        b.must(f.match()
                                .field("status")
                                .matching(OrderStatusEnum.TEST));

                    })).fetch(4 * pageNumber, 4);

            List<Order> orders = searchResult.hits();
            long totalHitCount = searchResult.total().hitCount();
            int lastPage = (int) (totalHitCount / 4);
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }
            List<OrderResponse> response = orderMapper.entitiesToDTOs(orders);

            return new OrdersResponses(Optional.of(response).orElseThrow(() -> new ResourceNotFoundException("status issue")));
        }

        return lastStep(pageNumber, result);
    }

    private OrdersResponses lastStep(int pageNumber, List<Order> result) {
        List<OrderResponse> response = orderMapper.entitiesToDTOs(result);

        int pageSize = 2;
        int lastPage = response.size() / pageSize;
        if(pageNumber > lastPage){
            throw new ResourceNotFoundException("");
        }
        AtomicInteger atomicInteger = new AtomicInteger(pageNumber);
        return new OrdersResponses(Optional.of(response.subList(pageNumber * pageSize, Math.min(atomicInteger.incrementAndGet() * pageSize, response.size())))
                .orElseThrow(() -> new ResourceNotFoundException("lastStep issue")));
    }

    private Map<Long, Double> sortByValueReversed(Map<Long, Double> map) {

        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double> comparingByValue().reversed())
                .collect(Collectors.toMap
                        (Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new));
    }

    public OrderResponse getOrderId(long id) {
        log.info("Retrieving order {}", id);
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + id + "not found"));
        return orderMapper.mapToOrderResponse(order);
    }

    public Order getOrder(long id){
        log.info("Retrieving order {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order" + id + "not found"));
    }

}
