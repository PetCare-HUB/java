package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.DocumentoConhecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentoConhecimentoRepository extends JpaRepository<DocumentoConhecimento, Long> {

    List<DocumentoConhecimento> findByDocIdIn(Collection<String> docIds);

    void deleteByDocIdIn(Collection<String> docIds);

    @Query("select distinct d.fonte from DocumentoConhecimento d order by d.fonte")
    List<String> findFontesDistintas();

    @Query("select distinct d.modeloEmbedding from DocumentoConhecimento d")
    List<String> findModelosEmbedding();

    @Query("select max(d.dataIndexacao) from DocumentoConhecimento d")
    Optional<LocalDateTime> findUltimaIndexacao();
}
