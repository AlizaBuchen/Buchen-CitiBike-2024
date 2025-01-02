package lambda;


import buchen.station.CitiBikeService;
import buchen.station.CitiBikeServiceFactory;
import buchen.station.Stations;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache {
    private static final String BUCKET_NAME = "buchen.citibike";
    private static final String KEY_NAME = "station_information.json";
    private Stations stations;
    private Instant lastModified;
    private final S3Client s3Client;
    private final CitiBikeService service;

    public StationsCache() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.service = new CitiBikeServiceFactory().getService();
    }


    public Stations getStations() {
        if (stations != null && lastModified != null && Duration.between(lastModified, Instant.now()).toHours() < 1) {
            return stations;
        }

        if ((stations == null && s3LastModified()) ||
                (lastModified == null || Duration.between(lastModified, Instant.now()).toHours() >= 1)) {
            stations = getStationsFromService();
            lastModified = Instant.now();
            writingS3(stations);
        } else if (stations == null) {
            stations = readingS3();
            lastModified = getLastModified();
        }
        return stations;
    }

    private Stations getStationsFromService() {
        return service.stationInfo().blockingGet();
    }
    private Instant getLastModified() {
        return lastModified;
    }

    public Stations readingS3() {

        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();

        try (InputStream in = s3Client.getObject(getObjectRequest)) {
            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(in), Stations.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void writingS3(Stations stations) {
        Gson gson = new Gson();
        String content = gson.toJson(stations);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }

    private boolean s3LastModified() {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_NAME)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            lastModified = headObjectResponse.lastModified();
            return Duration.between(lastModified, Instant.now()).toHours() >= 1;
        } catch (Exception e) {
            return false;
        }
    }
}