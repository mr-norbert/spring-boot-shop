package bnorbert.onlineshop;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class OnlineShopApplication {

	public static void main(String[] args) throws SQLException, TasteException {
		SpringApplication.run(OnlineShopApplication.class, args);

/*
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerTimezone("UTC");
		dataSource.setServerName("localhost");
		dataSource.setUser("shop");
		dataSource.setPassword("shop");
		dataSource.setDatabaseName("shop");
		JDBCDataModel model = new MySQLJDBCDataModel(dataSource,
				"review", "user_id", "product_id", "rating", null);


		System.out.println("TanimotoCoefficientSimilarity");
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
		ItemSimilarity sim2 = new CityBlockSimilarity(model);
		GenericItemBasedRecommender generic2 = new GenericItemBasedRecommender(model, sim2);

		for (LongPrimitiveIterator items = model.getItemIDs(); items.hasNext(); ) {
			long itemId = items.nextLong();
			List<RecommendedItem> someRecommendations = generic2.mostSimilarItems(itemId, 4);

			for (RecommendedItem recommendation : someRecommendations) {
				System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
			}
		}

 */

	}


}
