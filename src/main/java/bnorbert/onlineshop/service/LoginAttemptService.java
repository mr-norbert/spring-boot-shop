package bnorbert.onlineshop.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {

            @Override
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            int maxAttempt = 11;
            return attemptsCache.get(key) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    public void testLoginAttempts(){
        int maxAttempt = 20;
        for (int iterate = 0; iterate < maxAttempt ; iterate++) {
            mockLoginAttempt();
        }
    }

    private void mockLoginAttempt() {
        HttpClient client = new HttpClient();
        String url = "http://localhost:8085/users/login?email=string%40gmail.com&password=test";

        try {

            GetMethod method = new GetMethod(url);
            method.getParams()
                    .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

            long start = System.nanoTime();
            client.executeMethod(method);
            method.getResponseBodyAsStream();
            long end = System.nanoTime();

            double elapsedTime = (end - start);
            method.releaseConnection();

            System.err.println("Elapsed time : " + elapsedTime);
        } catch(Exception e){
            e.printStackTrace();
        }


    }

}
