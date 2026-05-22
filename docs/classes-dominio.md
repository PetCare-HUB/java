# Classes de Domínio

## Responsavel
Representa o tutor/responsável pelo pet. Possui nome, email, telefone e CPF.

## Clinica
Representa uma clínica parceira. No Java é usada principalmente como vínculo de domínio para pets e consultas. O dashboard clínico completo é responsabilidade da API .NET.

## Pet
Entidade central do domínio. Contém espécie, raça, peso, sexo, condições crônicas, responsável, clínica e score atual.

## Consulta
Representa eventos clínicos, como check-up, vacina, exame, retorno ou emergência.

## LeituraColeira
Representa dados vindos da coleira smart: atividade e bateria.

## LeituraComedouro
Representa dados do comedouro inteligente: nível de ração e consumo em gramas.

## LeituraAmbiente
Representa dados ambientais: temperatura, umidade, qualidade do ar e presença do pet.

## AlertaSaude
Representa alertas automáticos ou manuais gerados por regra de negócio.

## ScoreSaude
Representa o resultado do cálculo de saúde do pet, com categoria VERDE, AMARELO ou VERMELHO.

## EventoPreventivo
Representa próximas ações recomendadas para o cuidado preventivo.

## ProtocoloPreventivo
Representa protocolos por espécie e raça. Possui cache, pois é dado de baixa alteração.
