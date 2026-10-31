# Alertas do Sistema PetCare Hub

> Documento da base de conhecimento do PetCare Hub. Conteúdo de orientação geral; não substitui avaliação de um médico-veterinário.

Este documento descreve cada tipo de alerta que o PetCare Hub pode registrar para um pet, o que dispara cada um, o que ele significa e o que o tutor pode fazer.

## Como os alertas funcionam

Os alertas são gerados automaticamente quando uma leitura de sensor ou o cálculo do score passam de um limite. Cada alerta tem um tipo, um nível (BAIXO, MÉDIO, ALTO ou CRÍTICO), uma mensagem, o valor detectado e o limite de referência usado na comparação.

O sistema não empilha alertas repetidos: se já existe um alerta aberto do mesmo tipo para o pet, ele é atualizado com o valor, a mensagem e a data mais recentes, mantendo o mesmo registro. Depois que a causa é tratada, o alerta deve ser marcado como resolvido. Alertas de nível ALTO ou CRÍTICO que continuam abertos descontam 10 pontos do Score de Saúde.

## Alerta BATERIA_COLEIRA_BAIXA

Disparado quando uma leitura da coleira informa bateria abaixo de 20%. Nível MÉDIO. Significa que o dispositivo está perto de descarregar, não que o pet tenha algum problema. Sem bateria a coleira para de enviar leituras e a avaliação de atividade fica incompleta.

O que fazer: recarregar a coleira e conferir se ela voltou a enviar leituras. Depois disso, o alerta pode ser marcado como resolvido.

## Alerta RACAO_BAIXA

Disparado quando uma leitura do comedouro informa nível de ração abaixo de 20%. Nível MÉDIO. Indica que o comedouro está quase vazio. Por si só não diz nada sobre o apetite do pet.

O que fazer: reabastecer o comedouro. Se o comedouro esvazia muito mais rápido ou mais devagar que o habitual, vale observar se houve mudança no apetite, se outro animal está comendo a ração ou se há desperdício.

## Alerta BAIXA_ALIMENTACAO

Disparado quando uma leitura do comedouro informa peso consumido abaixo de 30 gramas. Nível ALTO. Indica que, naquela leitura, o pet consumiu menos do que o sistema espera. Uma refeição menor isolada pode ter explicações simples, como calor, agitação na casa, petiscos fora de hora ou troca de ração. Redução de apetite que se repete ou dura mais de um dia merece atenção.

O que fazer: observar se o pet volta a comer normalmente nas próximas refeições, se está bebendo água e se há outros sinais como apatia, vômito ou diarreia. Gato que fica sem comer por mais de 24 horas precisa de avaliação veterinária com urgência. Cão sem comer por mais de um dia, ou com outros sinais junto, também deve ser avaliado.

## Alerta ATIVIDADE_BAIXA

Disparado quando a coleira registra uma leitura com status SEDENTARIO. Esse alerta é criado diretamente pelo banco de dados, pela procedure de gravação das leituras da coleira, e não pelo cálculo do score. Uma única leitura sedentária costuma ser só um pet descansando ou dormindo.

O que fazer: olhar o conjunto da semana, e não a leitura isolada. O Score de Saúde avalia o percentual de leituras sedentárias nos últimos 7 dias, e só acima de 40% isso desconta pontos. Se a baixa atividade se repete por dias, ou vem junto de dor, dificuldade para se levantar, falta de apetite ou apatia, procure o veterinário.

## Alerta AMBIENTE_RUIM

Disparado quando o sensor de ambiente mede qualidade do ar acima de 1000 ppm. Nível ALTO. Indica ar pouco renovado ou com algum poluente no ambiente onde o pet fica, como ambiente fechado, fumaça, produtos de limpeza ou aerossóis.

O que fazer: ventilar o ambiente, afastar o pet de fumaça e de produtos químicos e verificar se a situação se repete. Se o pet apresentar tosse, espirros frequentes, olhos irritados ou dificuldade para respirar, procure o veterinário; dificuldade para respirar é emergência.

## Alerta TEMPERATURA_FORA_DA_FAIXA

Disparado quando o sensor de ambiente mede temperatura abaixo de 10 °C ou acima de 32 °C. Nível MÉDIO. O limite de referência registrado no alerta mostra qual lado foi ultrapassado: 10 °C para frio e 32 °C para calor.

O que fazer: no calor, garantir sombra, ventilação e água fresca à vontade e evitar exercícios nas horas mais quentes. No frio, oferecer abrigo protegido do vento e cama seca. Observe sinais de estresse térmico, descritos no documento sobre ambiente, e procure atendimento imediato se eles aparecerem.

## Alerta UMIDADE_FORA_DA_FAIXA

Disparado quando o sensor de ambiente mede umidade abaixo de 30% ou acima de 75%. Nível MÉDIO. O limite de referência mostra qual lado foi ultrapassado. Umidade muito alta somada ao calor dificulta a perda de calor do pet; ar muito seco pode incomodar as vias respiratórias.

O que fazer: melhorar a ventilação quando a umidade estiver alta e garantir água disponível. Em ar muito seco, manter o pet hidratado e evitar ambientes fechados e aquecidos por muito tempo.

## Alerta SCORE_CRITICO

Disparado pelo cálculo do Score de Saúde quando o score total fica abaixo de 50, ou seja, na categoria VERMELHO. Nível CRÍTICO. Significa que vários sinais monitorados estão desfavoráveis ao mesmo tempo. Não é um diagnóstico.

O que fazer: abrir o detalhe do score para ver quais sub-scores e quais frases da observação explicam a nota, tratar o que estiver ao alcance do tutor (como ambiente, comedouro e bateria) e entrar em contato com a clínica para uma avaliação preventiva.

## Alertas CONSULTA_ATRASADA e SCORE_BAIXO

Esses dois tipos fazem parte do catálogo de alertas do PetCare Hub, mas hoje não são gerados automaticamente pelas regras de sensores e de score descritas aqui. Quando aparecem, foram registrados manualmente ou por outro módulo da plataforma, e a mensagem do próprio alerta explica o motivo.

O tempo sem consulta já é considerado no Score de Saúde: sem consulta registrada, ou com a última consulta há mais de 180 dias, o score perde pontos. Nesses casos o que fazer é agendar uma consulta de rotina.

Revisão: conteúdo gerado para a Sprint 4; deve ser validado por médico-veterinário antes de uso real.
