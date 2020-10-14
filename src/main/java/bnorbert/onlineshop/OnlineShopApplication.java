package bnorbert.onlineshop;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class OnlineShopApplication {

	public static void main(String[] args) throws SQLException, TasteException {
		SpringApplication.run(OnlineShopApplication.class, args);


		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerTimezone("UTC");
		dataSource.setServerName("localhost");
		dataSource.setUser("shop");
		dataSource.setPassword("shop");
		dataSource.setDatabaseName("shop");
		JDBCDataModel model = new MySQLJDBCDataModel(dataSource,
				"review", "user_id", "product_id", "rating", null);


		System.out.println("TanimotoCoefficientSimilarity " + LocalDateTime.now());
		ItemSimilarity similarity = new TanimotoCoefficientSimilarity(model);
		GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(model, similarity);

		for (LongPrimitiveIterator items = model.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = genericItemBasedRecommender.mostSimilarItems(itemId, 4);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}


		System.out.println("EuclideanDistanceSimilarity");
		ItemSimilarity sim = new EuclideanDistanceSimilarity(model);
		GenericItemBasedRecommender generic = new GenericItemBasedRecommender(model, sim);

		for (LongPrimitiveIterator items = model.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = generic.mostSimilarItems(itemId, 4);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}


		System.out.println("Manhattan distance");
		ItemSimilarity itemSimilarity = new CityBlockSimilarity(model);
		GenericItemBasedRecommender genericRecommender = new GenericItemBasedRecommender(model, itemSimilarity);

		for (LongPrimitiveIterator items = model.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = genericRecommender.mostSimilarItems(itemId, 4);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}



		MysqlDataSource dataSource2 = new MysqlDataSource();
		dataSource2.setServerTimezone("UTC");
		dataSource2.setServerName("localhost");
		dataSource2.setUser("shop");
		dataSource2.setPassword("shop");
		dataSource2.setDatabaseName("shop");
		MySQLBooleanPrefJDBCDataModel dataModel = new MySQLBooleanPrefJDBCDataModel(dataSource2, "view", "user_id", "product_id","last_modified_date" );

		//discounts for the future
		System.out.println("TanimotoCoefficientSimilarity #2 " + LocalDateTime.now());
		ItemSimilarity itemSim = new TanimotoCoefficientSimilarity(dataModel);
		GenericItemBasedRecommender genericItemBasedRec = new GenericItemBasedRecommender(dataModel, itemSim);

		for (LongPrimitiveIterator items = dataModel.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = genericItemBasedRec.mostSimilarItems(itemId, 2);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}



		MysqlDataSource dataSource3 = new MysqlDataSource();
		dataSource3.setServerTimezone("UTC");
		dataSource3.setServerName("localhost");
		dataSource3.setUser("shop");
		dataSource3.setPassword("shop");
		dataSource3.setDatabaseName("shop");
		MySQLBooleanPrefJDBCDataModel data = new MySQLBooleanPrefJDBCDataModel(dataSource3, "cart_item", "user_id", "product_id","created_date" );

		//Customers who bought this product also bought these products
		System.out.println("TanimotoCoefficientSimilarity #3 " + LocalDateTime.now());
		ItemSimilarity itemSim2 = new TanimotoCoefficientSimilarity(data);
		GenericItemBasedRecommender genRecommender = new GenericItemBasedRecommender(data, itemSim2);

		for (LongPrimitiveIterator items = data.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = genRecommender.mostSimilarItems(itemId, 4);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}




	}


}
