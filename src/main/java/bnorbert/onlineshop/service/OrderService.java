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
        //Order order = orderMapper.map( request, userService.getCurrentUser());

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
            //throw new ResourceNotFoundException("Not so much quantity in stock");
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

            OrderWord identifier_1 = new OrderWord();
            identifier_1.setIndexOfWord(1);
            identifier_1.setDocId(order.getId());
            identifier_1.setWord(product.getBrandName());
            identifier_1.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_1);

            OrderWord identifier_2 = new OrderWord();
            identifier_2.setIndexOfWord(2);
            identifier_2.setDocId(order.getId());
            identifier_2.setWord(product.getCategoryName());
            identifier_2.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_2);

            OrderWord identifier_3 = new OrderWord();
            identifier_3.setIndexOfWord(3);
            identifier_3.setDocId(order.getId());
            identifier_3.setWord(order.getCity());
            identifier_3.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_3);

            OrderWord identifier_4 = new OrderWord();
            identifier_4.setIndexOfWord(4);
            identifier_4.setDocId(order.getId());
            identifier_4.setWord(userService.getCurrentUser().getEmail());
            identifier_4.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_4);

            OrderWord _identifier = new OrderWord();
            _identifier.setIndexOfWord(5);
            _identifier.setDocId(order.getId());
            _identifier.setWord(order.getId().toString());
            _identifier.setLength(product.getDescription().length());
            ordersWordsRepository.save(_identifier);
        });
    }

    public OrdersResponses getOrders(OrderRequest request, String query, Pageable pageable, OrderTypeEnum orderType, int pageNumber) {
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
                double TF_IDF = termFrequency * inverseDocumentFrequency;

                hashMap.put(doc, hashMap.getOrDefault(doc, (double) 0) + TF_IDF);
            }
        }

        Map<Long, Double> sortedMap = sortByValueReversed(hashMap);
        log.info(sortedMap + " sortedmap");

        Set<Long> idSet = new LinkedHashSet<>(sortedMap.keySet());
        List<Order> result = new ArrayList<>();
        if(!idSet.isEmpty()) {
            for (Long orderId : idSet) {
                Order order = getOrder(orderId);
                result.add(order);
            }
        }else {
            throw new ResourceNotFoundException("empty list" );
        }

        return getOrdersResponses(request, pageable, orderType, pageNumber, idSet, result);
    }

    private OrdersResponses getOrdersResponses(OrderRequest request, Pageable pageable,
                                               OrderTypeEnum orderType, int pageNumber,
                                               Set<Long> idSet, List<Order> result) {
        if (orderType == OrderTypeEnum.BETWEEN) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Order> searchResult = searchSession.search(Order.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.id()
                                .matchingAny(idSet));
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

            return new OrdersResponses(Optional.of(response)
                    .map(search -> new PageImpl<>(response, pageable, totalHitCount))
                    .orElseThrow(() -> new ResourceNotFoundException("")));
        }

        if (orderType == OrderTypeEnum.TEST) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Order> searchResult = searchSession.search(Order.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.id()
                                .matchingAny(idSet));
                        b.must(f.match()
                                .field("status")
                                .matching(OrderStatusEnum.COMPLETED));

                    })).fetch(4 * pageNumber, 4);

            List<Order> orders = searchResult.hits();
            long totalHitCount = searchResult.total().hitCount();
            int lastPage = (int) (totalHitCount / 4);
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }
            List<OrderResponse> response = orderMapper.entitiesToDTOs(orders);

            return new OrdersResponses(Optional.of(response)
                    .map(search -> new PageImpl<>(response, pageable, totalHitCount))
                    .orElseThrow(() -> new ResourceNotFoundException("")));
        }

        //SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        //SearchResult<OrderStatusEnum> status = searchSession.search(Order.class)
        //        .select(f-> f.field("status", OrderStatusEnum.class))
        //        //.where(f -> f.match().field("status").matching(OrderStatusEnum.TEST))
        //        .where(f -> f.id().matchingAny(idSet))
        //        .fetch(idSet.size());
        //System.err.println(status.hits());

        List<OrderResponse> response = orderMapper.entitiesToDTOs(result);

        int pageSize = 2;
        int lastPage = response.size() / pageSize;
        if(pageNumber > lastPage){
            throw new ResourceNotFoundException("");
        }
        AtomicInteger atomicInteger = new AtomicInteger(pageNumber);
        return new OrdersResponses(Optional.of(response)
                .map(search -> new PageImpl<>
                        (response.subList(pageNumber * pageSize, Math.min(atomicInteger.incrementAndGet() * pageSize, response.size())), pageable, response.size()))
                .orElseThrow(() -> new ResourceNotFoundException("")));
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
