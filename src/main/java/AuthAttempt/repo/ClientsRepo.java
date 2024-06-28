package AuthAttempt.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import AuthAttempt.entity.Client;

public interface ClientsRepo extends MongoRepository<Client, String> {

}
