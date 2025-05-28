package persistence;

import java.util.Optional;

import domain.Url;
import io.smallrye.mutiny.Uni;

public interface DynamoDao {
	Uni<Void> save(Url url);
	Uni<Optional<Url>> findByShortCode(String shortCode);
    Uni<Void> update(Url shortUrl);
}
