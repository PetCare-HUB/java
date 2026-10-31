# Ambiente: Temperatura, Umidade e Qualidade do Ar

> Documento da base de conhecimento do PetCare Hub. Conteúdo de orientação geral; não substitui avaliação de um médico-veterinário.

Este documento explica o que o sensor de ambiente do PetCare Hub mede, as faixas de referência usadas pelo sistema e os cuidados com calor, frio e qualidade do ar.

## O que o sensor de ambiente mede

Cada leitura do sensor de ambiente informa a temperatura em graus Celsius, a umidade relativa do ar em percentual, a qualidade do ar em ppm e se o pet estava presente no ambiente naquele momento.

O PetCare Hub usa estas faixas de referência: temperatura entre 10 °C e 32 °C, umidade entre 30% e 75% e qualidade do ar até 1000 ppm. Fora delas, o sistema gera o alerta correspondente e desconta pontos do Score de Saúde. As faixas são referências gerais do sistema; a tolerância real varia com espécie, raça, idade, pelagem e estado de saúde de cada pet.

## Calor e estresse térmico

Cães e gatos perdem calor com mais dificuldade que as pessoas, principalmente pela respiração ofegante. Em dias quentes, e mais ainda com umidade alta, essa perda de calor fica menos eficiente e o pet pode desenvolver estresse térmico e, em casos graves, hipertermia (insolação), que é uma emergência.

Cuidados no calor: manter sombra e ventilação, oferecer água fresca à vontade, evitar passeios e brincadeiras intensas nas horas mais quentes, evitar asfalto quente e nunca deixar o pet dentro de carro fechado, nem por pouco tempo.

## Sinais de hipertermia

Sinais que podem indicar superaquecimento incluem respiração muito ofegante e difícil, salivação excessiva, gengivas e língua muito vermelhas ou arroxeadas, fraqueza, cambalear, vômito, diarreia, confusão, desmaio e convulsão.

Diante desses sinais, leve o pet para um local fresco e procure atendimento veterinário imediato. A caminho, é possível refrescá-lo com água em temperatura ambiente; não use água gelada nem gelo. Mesmo que o pet pareça melhorar, a avaliação veterinária é necessária.

## Raças braquicefálicas

Raças de focinho curto, chamadas braquicefálicas, como buldogue francês, buldogue inglês, pug, shih-tzu, boxer e gatos persas, têm vias respiratórias mais estreitas e dissipam calor com mais dificuldade. Para elas o calor e o esforço físico são mais arriscados, e o estresse térmico pode surgir em temperaturas que outros pets toleram.

Para esses pets, vale ser ainda mais cuidadoso nos dias quentes e úmidos, limitar o exercício e observar com atenção ronco intenso, respiração ruidosa ou cansaço rápido. Dificuldade para respirar é emergência.

## Frio e hipotermia

Pets filhotes, idosos, de pelo curto, de pequeno porte ou com doenças crônicas sentem mais o frio. Abaixo da faixa de referência, ofereça abrigo protegido do vento e da umidade, cama seca e elevada do chão e evite deixar o pet molhado.

Sinais que podem indicar hipotermia incluem tremores intensos, fraqueza, rigidez muscular, apatia, respiração lenta e extremidades frias. Nesses casos aqueça o pet aos poucos, com cobertores, e procure atendimento veterinário, que é urgente quando há apatia intensa ou o pet não responde.

## Umidade do ar

Umidade alta, acima de 75% na referência do sistema, dificulta a perda de calor e aumenta o risco de estresse térmico quando combinada com calor. Ambientes úmidos também favorecem mofo e fungos, que podem irritar a pele e as vias respiratórias.

Umidade baixa, abaixo de 30%, pode ressecar as mucosas e incomodar pets com problemas respiratórios. Nesses períodos mantenha o pet bem hidratado e o ambiente ventilado.

## Qualidade do ar

O valor em ppm medido pelo sensor indica o quanto o ar está carregado. Acima de 1000 ppm o sistema considera o ar ruim e gera o alerta AMBIENTE_RUIM. Causas comuns são ambiente fechado e pouco ventilado, fumaça de cigarro, fumaça de cozinha ou de queimadas, produtos de limpeza, aerossóis, incensos e tintas.

Pets são sensíveis a fumaça e produtos químicos, e animais com problemas respiratórios sofrem mais. Ventile o ambiente, evite fumar perto do pet e use produtos de limpeza com o pet fora do cômodo. Tosse persistente, espirros frequentes ou olhos irritados devem ser avaliados pelo veterinário; dificuldade para respirar é emergência.

## Presença do pet no ambiente

A leitura indica se o pet estava presente no momento da medição. Uma leitura fora da faixa com o pet ausente mostra um risco do ambiente, mas não necessariamente uma exposição do pet. Mesmo assim, como o sistema usa a última leitura do ambiente, o score é afetado até chegar uma nova leitura.

Revisão: conteúdo gerado para a Sprint 4; deve ser validado por médico-veterinário antes de uso real.
