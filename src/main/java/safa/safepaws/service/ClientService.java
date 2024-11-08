package safa.safepaws.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import safa.safepaws.dto.Client.createClientRequest;
import safa.safepaws.dto.Client.editClientRequest;
import safa.safepaws.model.Client;
import safa.safepaws.model.User;
import safa.safepaws.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final User authenticatedUser;

    public Client createClient(createClientRequest createClientRequest) {
        Client client = new Client();
        client.setName(createClientRequest.getName());
        client.setSurname(createClientRequest.getSurname());
        client.setBirthdate(createClientRequest.getBirthdate());
        client.setDni(createClientRequest.getDni());
        client.setAddress(createClientRequest.getAddress());
        return clientRepository.save(client);
    }


    public Client modifyClient(editClientRequest editClientRequest) {
        if (authenticatedUser.getClient().getId() == authenticatedUser.getClient().getId()) {
            Client client = clientRepository.findById(editClientRequest.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            client.setName(editClientRequest.getName());
            client.setSurname(editClientRequest.getSurname());
            client.setBirthdate(editClientRequest.getBirthdate());
            client.setDni(editClientRequest.getDni());
            client.setAddress(editClientRequest.getAddress());
            return clientRepository.save(client);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not authorized to modify this client");
        }
    }


}