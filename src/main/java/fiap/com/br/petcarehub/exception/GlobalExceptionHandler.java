package fiap.com.br.petcarehub.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatarErroCampo)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validação falhou", "Campos inválidos na requisição", request.getRequestURI(), detalhes);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return build(status, status.getReasonPhrase(), ex.getReason(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensagem = "Parâmetro inválido: " + ex.getName();
        String detalhe = "Valor recebido: " + ex.getValue();
        return build(HttpStatus.BAD_REQUEST, "Tipo de parâmetro inválido", mensagem, request.getRequestURI(), List.of(detalhe));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflito de dados", "Registro viola uma regra de integridade ou unicidade", request.getRequestURI(), List.of());
    }

    /**
     * O DaoAuthenticationProvider embrulha QUALQUER excecao vinda do
     * loadUserByUsername (menos UsernameNotFoundException) dentro de um
     * InternalAuthenticationServiceException. Sem este handler, as recusas
     * deliberadas de login - conta pendente de ativacao, clinica em análise,
     * acesso bloqueado - caiam no handleGeneric e viravam 500 com
     * "Ocorreu um erro inesperado", escondendo do usuario o motivo real e
     * poluindo o log como se fosse falha da aplicacao.
     *
     * Aqui desembrulhamos a causa: se foi uma recusa nossa, ela responde com
     * o proprio status e mensagem; qualquer outra coisa continua sendo 500.
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErroResponse> handleAuthServiceException(
            InternalAuthenticationServiceException ex,
            HttpServletRequest request
    ) {
        if (ex.getCause() instanceof ResponseStatusException causa) {
            return handleResponseStatus(causa, request);
        }
        return handleGeneric(ex, request);
    }

    /**
     * URL que não casa com nenhuma rota. Sem este handler caía no handleGeneric
     * e virava 500 "Ocorreu um erro inesperado" — um simples erro de digitação
     * na URL fazia a API parecer quebrada, e o log registrava como falha nossa.
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErroResponse> handleRotaInexistente(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "Rota não encontrada: " + request.getMethod() + " " + request.getRequestURI(),
                request.getRequestURI(), List.of());
    }

    /** Rota existe, mas não aceita esse verbo. Também caía como 500. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> handleMetodoNaoSuportado(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String suportados = ex.getSupportedHttpMethods() == null
                ? ""
                : " Métodos aceitos: " + ex.getSupportedHttpMethods() + ".";
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                "O método " + ex.getMethod() + " não é aceito nesta rota." + suportados,
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Ocorreu um erro inesperado", request.getRequestURI(), List.of(ex.getClass().getSimpleName()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Email ou senha inválidos.",
                request.getRequestURI(),
                List.of()
        );
    }


    private String formatarErroCampo(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ErroResponse> build(HttpStatus status, String erro, String mensagem, String path, List<String> detalhes) {
        return ResponseEntity.status(status).body(new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                path,
                detalhes
        ));
    }




}
