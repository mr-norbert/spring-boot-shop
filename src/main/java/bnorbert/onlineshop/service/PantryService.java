package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Pantry;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.repository.PantryRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
public class PantryService {

    private final PantryRepository pantryRepository;
    private final ProductRepository productRepository;

    public void findSimilarItems() throws SQLException, TasteException {

        List<Pantry> pantries = Stream
                .generate(Pantry::new)
                .limit(100)
                .collect(Collectors.toList());

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

        MultiValuedMap<Long, Long> map = new ArrayListValuedHashMap<>();

        for (LongPrimitiveIterator iterator = model.getItemIDs(); iterator.hasNext(); ) {
            long itemId = iterator.nextLong();

            List<RecommendedItem> recommendedItemList = genericItemBasedRecommender.mostSimilarItems(itemId, 5);


            int size = recommendedItemList.size();
            for(int i = 0; i < size ; i++) {
                RecommendedItem recommendedItem = recommendedItemList.get(i);
                System.out.println(itemId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue());
                map.put(itemId, recommendedItem.getItemID());

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


    public void createPantry() throws SQLException, TasteException, IOException {

        MultiValuedMap<Long, Long> multiValuedMap = new ArrayListValuedHashMap<>();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerTimezone("UTC");
        dataSource.setServerName("localhost");
        dataSource.setUser("shop");
        dataSource.setPassword("shop");
        dataSource.setDatabaseName("shop");
        JDBCDataModel model = new MySQLJDBCDataModel(dataSource,
                "review", "user_id", "product_id", "rating", null);


        ItemSimilarity itemSimilarity = new EuclideanDistanceSimilarity(model);
        GenericItemBasedRecommender generic = new GenericItemBasedRecommender(model, itemSimilarity);

        for (LongPrimitiveIterator iterator = model.getItemIDs(); iterator.hasNext(); ) {
            long itemId = iterator.nextLong();
            List<RecommendedItem> recommendedItemList = generic.mostSimilarItems(itemId, 5);

            for (RecommendedItem recommendedItem : recommendedItemList) {

                multiValuedMap.put(itemId, recommendedItem.getItemID());
            }

        }

        String[] HEADERS = createCSVFile(multiValuedMap);

        readCSVFile(HEADERS);


    }

    private void readCSVFile(String[] HEADERS) throws IOException {
        Reader reader = new FileReader("src/main/resources/test.csv");

        List<Pantry> pantries = Stream
                .generate(Pantry::new)
                .limit(20000)
                .collect(Collectors.toList());

        Iterable<CSVRecord> records =
                CSVFormat.DEFAULT
                        .withHeader(HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(reader);

        for (CSVRecord record : records) {
            String columnOne = record.get(0);
            String columnTwo = record.get(1);

            long pantryId = Long.parseLong(columnOne);
            long recommendedId = Long.parseLong(columnTwo);

            Product product = productRepository.findById(recommendedId)
                    .orElseThrow(EntityNotFoundException::new);

            int size = pantries.size();
            for(int j = 0; j < size; j++) {
                Pantry pantry = pantries.get(j);
                if (pantry.getId() == pantryId) {
                    pantry.addProduct(product);
                }
                pantryRepository.save(pantry);
            }

        }

    }


    private String[] createCSVFile(MultiValuedMap<Long, Long> multiValuedMap) throws IOException {
        String[] HEADERS = {"id", "recommendedId"};

        FileWriter fileWriter = new FileWriter("src/main/resources/test.csv");

        try (CSVPrinter printer = new CSVPrinter(
                fileWriter,
                CSVFormat.DEFAULT
                        .withHeader(HEADERS) ))   {

            for (Map.Entry<Long, Long> entry : multiValuedMap.entries()){
                try {
                    printer.printRecord(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return HEADERS;
    }


}
