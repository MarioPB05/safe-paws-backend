package safa.safepaws.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import safa.safepaws.dto.request.GetAdoptionsResponse;
import safa.safepaws.mapper.RequestMapper;
import safa.safepaws.model.Request;
import safa.safepaws.model.RequestAnswer;
import safa.safepaws.model.User;
import safa.safepaws.repository.RequestAnswerRepository;
import safa.safepaws.repository.RequestRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RequestService {
    private RequestRepository requestRepository;
    private RequestMapper requestMapper;
    private final User authenticatedUser;
    private final RequestAnswerRepository requestAnswerRepository;

    /**
     * Safe a new request
     *
     * @param requestdto
     * @return
     */
//    public Request save(RequestCreateDTO requestdto) {
//        return requestRepository.save(requestMapper.toEntity(requestdto));
//    }

    /**
     * Edit a request
     *
     * @param requestdto
     * @return
     */
//    public Request edit(RequestEditDTO requestdto){
//        if (requestdto.getId().equals(authenticatedUser.getClient().getId())) {
//            Request request = requestMapper.toEntity(requestdto);
//            return requestRepository.save(request);
//        }else{
//            throw new RuntimeException("El usuario no tiene permisos para editar");
//        }
//    }

    /**
     * Delete a request
     *
     * @param id
     * @return
     */
    public String delete (Integer id){
        Request request = requestRepository.findById(id).orElse(null);
        if (Objects.requireNonNull(request).getId().equals(authenticatedUser.getClient().getId())) {
            request.setDeleted(true);
        }
        return "Request eliminada";
    }


    public List<RequestAnswer> getAnswersForRequest(Integer requestId) {
        return requestAnswerRepository.findByRequestId(requestId);
    }



    public List<GetAdoptionsResponse> getSentAdoptionsResponses(){
        List<Request> requestsList = requestRepository.findAllByClientIdSent(authenticatedUser.getClient().getId());
        return requestMapper.toAdoptionsResponseDTO(requestsList);
    }

    public List<GetAdoptionsResponse> getReceivedAdoptionsResponses(){
        List<Request> requestsList = requestRepository.findAllByClientIdReceived(authenticatedUser.getClient().getId());
        return requestMapper.toAdoptionsResponseDTO(requestsList);
    }

}
