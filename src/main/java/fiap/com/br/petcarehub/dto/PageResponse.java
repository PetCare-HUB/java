package fiap.com.br.petcarehub.dto;


import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {

    private List<T> content;
    private int pagina;
    private int totalPaginas;
    private long totalElementos;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pagina = page.getNumber();
        this.totalPaginas = page.getTotalPages();
        this.totalElementos = page.getTotalElements();
    }
}
