package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Pantry;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.repository.PantryRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class PantryService {

    private final PantryRepository pantryRepository;
    private final ProductRepository productRepository;

    public PantryService(PantryRepository pantryRepository, ProductRepository productRepository) {
        this.pantryRepository = pantryRepository;
        this.productRepository = productRepository;
    }

    public void generateIds(){
        log.info("Creating relationship");
        List<Product> products = productRepository.findAll();
        for(Product product : products){
            Pantry pantry = new Pantry();
            pantry.setProduct(product);
            pantryRepository.save(pantry);
        }
    }

    public void deleteAll(){
        log.info("Deleting recommended products");
        List<Pantry> pantries = pantryRepository.findAll();
        List<Product> products = productRepository.findAll();

        Set<Pantry> _pantries = new LinkedHashSet<>(pantries);
        Set<Product> _products = new LinkedHashSet<>(products);

        for(Pantry pantry : _pantries){
            for(Product product : _products){
                pantry.removeProduct(product);
                pantryRepository.save(pantry);
            }
        }
        pantryRepository.deleteAll();
    }

    public void findSimilarProducts() throws SQLException, TasteException {
        List<Pantry> pantries = pantryRepository.findAll();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerTimezone("UTC");
        dataSource.setServerName("localhost");
        dataSource.setUser("shop");
        dataSource.setPassword("shop");
        dataSource.setDatabaseName("shop");
        JDBCDataModel model = new MySQLJDBCDataModel(dataSource,
                "review", "user_id", "product_id", "rating", null);

        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(model);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(model, similarity);

        for (LongPrimitiveIterator iterator = model.getItemIDs(); iterator.hasNext(); ) {
            long itemId = iterator.nextLong();

            List<RecommendedItem> recommendations = genericItemBasedRecommender.mostSimilarItems(itemId, 5);

            int size = recommendations.size();
            for(int i = 0; i < size ; i++) {
                RecommendedItem recommendedItem = recommendations.get(i);
                log.info(itemId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue());

                Product product = productRepository.findById(recommendedItem.getItemID())
                        .orElseThrow(EntityNotFoundException::new);

                int s = pantries.size();
                for(int j = 0; j < s ; j++) {
                    Pantry pantry = pantries.get(j);
                    if (pantry.getId() == itemId) {
                        pantry.addProduct(product);
                    }
                    pantryRepository.save(pantry);
                }

            }
        }

    }


}
