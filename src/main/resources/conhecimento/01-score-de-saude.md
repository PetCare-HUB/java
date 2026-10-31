# Score de Saúde do PetCare Hub

> Documento da base de conhecimento do PetCare Hub. Conteúdo de orientação geral; não substitui avaliação de um médico-veterinário.

Este documento explica como o PetCare Hub calcula o Score de Saúde de um pet, o que cada parte do score significa e como interpretar a observação que acompanha cada cálculo.

## O que é o Score de Saúde

O Score de Saúde é uma nota de 0 a 100 que resume os sinais que o PetCare Hub consegue observar sobre um pet: atividade medida pela coleira, alimentação medida pelo comedouro, condições do ambiente, continuidade das consultas e alertas graves em aberto. O cálculo começa em 100 e cada problema encontrado desconta pontos.

O score é um indicador de acompanhamento, não um diagnóstico. Ele mostra onde os dados chamam atenção, mas não diz qual é a causa. Um score baixo é motivo para olhar os detalhes e, quando fizer sentido, conversar com o médico-veterinário.

## Quando o score é recalculado

O score é recalculado a cada nova leitura de sensor recebida (coleira, comedouro ou ambiente). Ao consultar o score atual, o sistema também confere se chegou alguma leitura mais nova do que o último cálculo e, se chegou, calcula de novo. Cada cálculo fica guardado no histórico do pet com a data em que foi feito.

## Categorias do score: verde, amarelo e vermelho

O score total é classificado em três categorias. VERDE vale para score de 80 ou mais e indica que os sinais monitorados estão dentro do esperado. AMARELO vale para score de 50 a 79 e indica pontos de atenção. VERMELHO vale para score abaixo de 50 e indica vários sinais desfavoráveis ao mesmo tempo.

Quando o score fica abaixo de 50, o sistema registra automaticamente um alerta do tipo SCORE_CRITICO, de nível CRÍTICO, recomendando contato preventivo com a clínica.

## Como a atividade entra no score

A atividade vem da coleira, que classifica cada leitura como ATIVO, MODERADO ou SEDENTARIO. O sistema olha os últimos 7 dias, e não só a última leitura, porque uma leitura isolada de pet parado pode ser apenas um cochilo, enquanto uma semana inteira de pouca atividade é um sinal que merece atenção.

São necessárias pelo menos 5 leituras da coleira nesses 7 dias. Com menos do que isso, a atividade não é avaliada e a observação registra que há poucas leituras para avaliar. Com leituras suficientes, o sistema calcula o percentual de leituras SEDENTARIO. Até 40% é considerado normal, porque todo pet passa boa parte do dia descansando. Acima de 40%, o desconto cresce proporcionalmente ao excesso e pode chegar a 25 pontos quando todas as leituras são sedentárias. Por exemplo, com 70% de leituras sedentárias o desconto é de 12 pontos.

## Como a bateria da coleira entra no score

Se a última leitura da coleira mostra bateria abaixo de 20%, o score total perde 5 pontos. A bateria é saúde do dispositivo, não do pet: o desconto existe porque, sem coleira carregada, o monitoramento para de acontecer. Por isso ele não afeta o sub-score de atividade.

## Como a alimentação entra no score

A alimentação vem da última leitura do comedouro. Se o nível de ração estiver abaixo de 20%, o score perde 10 pontos, o que indica principalmente que o comedouro precisa ser reabastecido. Se o peso consumido estiver abaixo de 30 gramas, o score perde 20 pontos, porque o consumo ficou abaixo do esperado pelo sistema. Os dois descontos podem acontecer juntos.

## Como o ambiente entra no score

O ambiente vem da última leitura do sensor de ambiente. Temperatura abaixo de 10 °C ou acima de 32 °C desconta 15 pontos. Umidade abaixo de 30% ou acima de 75% desconta 10 pontos. Qualidade do ar acima de 1000 ppm desconta 15 pontos. Essas faixas são as referências que o PetCare Hub usa para o ambiente onde o pet fica.

## Como as consultas entram no score

O score também mede a continuidade do cuidado. Se o pet ainda não tem nenhuma consulta registrada, o score perde 15 pontos e o sub-score de consulta fica em 0. Se a última consulta foi há mais de 180 dias, o desconto cresce proporcionalmente aos dias além desse limite, até o máximo de 15 pontos, que é atingido por volta de 360 dias sem consulta. Consultas dentro de 180 dias não descontam nada.

## Como os alertas graves entram no score

Se o pet tem pelo menos um alerta de nível ALTO ou CRÍTICO ainda não resolvido, o score total perde 10 pontos. Esse desconto não altera os sub-scores, porque o alerta é consequência dos sensores e não de uma área específica. Resolver o alerta no sistema, depois de tratar a causa, remove esse desconto no próximo cálculo.

## Sub-scores: atividade, alimentação, ambiente, consulta e preventivo

Além do score total, cada cálculo guarda sub-scores de 0 a 100. O sub-score de atividade reflete só o sedentarismo da semana. O de alimentação reflete ração baixa e consumo baixo. O de ambiente reflete temperatura, umidade e qualidade do ar. O de consulta reflete o tempo desde a última consulta. O sub-score preventivo ainda não tem regra própria e fica neutro em 100.

Os sub-scores ajudam a localizar a origem de um score baixo. Por exemplo, score total amarelo com sub-score de ambiente baixo e os demais altos aponta para o ambiente, e não para a alimentação.

## Como ler a observação do histórico de score

Cada cálculo novo traz uma observação em texto com duas partes. A primeira parte diz como o score variou em relação ao cálculo anterior, com os números reais: "Primeiro cálculo do score registrado para o pet", "Score permaneceu estável em X pontos", "Score diminuiu de X para Y (-N pontos)" ou "Score aumentou de X para Y (+N pontos)". A segunda parte começa com "Indicadores do período:" e lista uma frase para cada problema encontrado naquele cálculo. As frases possíveis são: poucas leituras da coleira para avaliar a atividade; pet sedentário em determinado percentual das leituras dos últimos 7 dias; bateria da coleira abaixo de 20%; nível de ração baixo; consumo alimentar abaixo do esperado; temperatura ambiente fora da faixa segura; umidade ambiente fora da faixa ideal; qualidade do ar ruim; pet ainda sem consulta registrada; última consulta há determinado número de dias; e quantidade de alertas graves ativos.

Quando nenhum critério descontou pontos, a segunda parte diz "sem alterações relevantes nos indicadores disponíveis". Se o score mudou e nenhum indicador do cálculo atual explica a mudança (por exemplo, um alerta grave foi resolvido ou uma leitura voltou ao normal), a observação registra que "a variação não pôde ser atribuída a um indicador específico do cálculo atual", em vez de inventar uma causa. A observação descreve o que os dados mostraram, sem explicar a causa clínica.

Scores calculados antes da versão que passou a gravar a observação não têm esse texto: a API devolve o aviso "Sem observação registrada" e não existe explicação histórica para eles. Não se deve supor um motivo para essas variações antigas.

## Limites do Score de Saúde

O score só enxerga o que os sensores e os registros do sistema captam. Ele não vê sintomas como tosse, coceira, mancar, alterações nas fezes ou no comportamento, nem resultados de exames. Um score verde não garante que o pet está saudável, e um score vermelho não significa que ele está doente. Leituras de alimentação e ambiente usam a última leitura recebida, então um único valor fora da faixa já afeta o score até chegar uma leitura nova.

Revisão: conteúdo gerado para a Sprint 4; deve ser validado por médico-veterinário antes de uso real.
