package safa.safepaws.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import safa.safepaws.dto.post.CheckPostResponse;
import safa.safepaws.dto.request.GetAdoptionsResponse;
import safa.safepaws.dto.request.RequestCreateDTO;
import safa.safepaws.dto.requestAnswer.CreateRequestAnswerRequest;
import safa.safepaws.enums.RequestStatus;
import safa.safepaws.mapper.RequestMapper;
import safa.safepaws.model.Post;
import safa.safepaws.model.Request;
import safa.safepaws.model.RequestAnswer;
import safa.safepaws.model.User;
import safa.safepaws.repository.RequestAnswerRepository;
import safa.safepaws.repository.RequestRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RequestService {
    private RequestRepository requestRepository;
    private RequestMapper requestMapper;
    private final User authenticatedUser;
    private final RequestAnswerRepository requestAnswerRepository;
    private final PostService postService;
    private final RequestAnswerService requestAnswerService;
    private final PdfService pdfService;

    public String delete (Integer id){
        Request request = requestRepository.findById(id).orElse(null);
        if (Objects.requireNonNull(request).getId().equals(authenticatedUser.getClient().getId())) {
            request.setDeleted(true);
        }
        return "Request eliminada";
    }

    public Request findRequestByClientAndPost(Integer clientId, Integer postId){
        return requestRepository.findByClientIdAndPostIdAndDeletedIsFalse(clientId, postId).orElse(null);
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

    @Transactional
    public String createRequest(RequestCreateDTO requestCreateDTO){
        Request request = new Request();
        Post post = postService.findPost(requestCreateDTO.getPostId());

        request.setMessage(requestCreateDTO.getMessage());
        request.setCreationDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        request.setDeleted(false);
        request.setClient(authenticatedUser.getClient());
        request.setPost(post);
        request.setCode(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10).toUpperCase());

        request = requestRepository.save(request);

        for (CreateRequestAnswerRequest requestAnswer : requestCreateDTO.getAnswers()) {
            requestAnswerService.createRequestAnswer(requestAnswer, request);
        }

        return request.getCode();
    }

    public void generateRequestPdf(String requestCode, HttpServletResponse response) throws Exception {
        Request request = requestRepository.findByCode(requestCode).orElse(null);

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
        }

        Context context = new Context();

        LocalDate currentDate = LocalDate.now();
        String month = currentDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        month = month.substring(0, 1).toUpperCase() + month.substring(1);
        String formattedDate = month + " " + currentDate.getDayOfMonth() + ", " + currentDate.getYear();

        context.setVariable("date", formattedDate);

        List<RequestAnswer> answers = getAnswersForRequest(request.getId());
        context.setVariable("answers", answers);

        context.setVariable("request", request);
        context.setVariable("post", request.getPost());
        context.setVariable("client", request.getClient());

        byte[] pdfBytes = pdfService.generatePdf("adoption-request", context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte.pdf");
        response.getOutputStream().write(pdfBytes);
    }

    public CheckPostResponse checkRequest(Integer postId) {
        Request request = this.findRequestByClientAndPost(authenticatedUser.getClient().getId(), postId);
        String code = null;

        if (request != null) {
            code = request.getCode();
        }

        return new CheckPostResponse(request == null, code);
    }
}
