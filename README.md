# Conversor de Vídeos para formatos WEB [MP4]

## O que é?

Este projeto faz parte do processo seletivo da Sambatech. Esta atividade prática foi proposta da seguinte forma: Disponibilizar uma aplicação WEB capaz de converter formatos de vídeo não compatíveis com a WEB. 

## Como foi feito?

A aplicação foi construída utilizando a linguagem JAVA. O Framework Java utilizado foi o Jersey. Jersey é uma das implementações RESTful para a linguagem de programação Java. Para isso foi criado utilizando o eclipse um projeto maven web. O Maven é o framework utilizado para controle de dependências, as dependências utilizadas podem ser checadas no arquivo POM.xml. Para a hospedagem foram utilizados os serviços da Amazon. O Amazon EC2 foi utilizado para hospedar o servidor, e o Amazon S3 é utilizado para armazenar os vídeos de entrada e saída. Para hospedar a aplicação foi utilizado o apache tomcat, e para hospedar a parte estática da aplicação foi utilizado o apache. 

O Serviço de codificação utilizado foi o Zencode. A sua API REST utilizando JSON é simplificada e bastante eficiente. Contudo, os vídeos decodificados possuem apenas 5 segundos de duração pois foi utilizada uma conta de testes. Os vídeos são decodificados para MP4 independente do formato padrão utilizado. 


## Como funciona a aplicacão?

### Ponto de vista do usuário:

A aplicação pode ser acessada pelo endereço X:

1 - O usuário seleciona um vídeo presente em seu computador e faz o upload no sistema
2 - É necessário aguardar, que o upload seja feito e a decodificação seja concluída
3 - Por, fim o usuário é redirecionado automaticamente para a página onde o vídeo pode ser assistido

### Ponto de vista técnico:

O Pipeline da aplicação funciona da seguinte forma:

1 - O servidor é inicializado com apenas um endpoint para receber o upload dos videos
2 - O servidor recebe o video e salva em seu disco local
3 - O vídeo é exportado para o servidor da Amazon S3
4 - O vídeo é enviado para a decodificação utilizando o serviço Zencode
5 - O vídeo é decodificado e armazenado na Amazon S3
6 - O Servidor checa se o vídeo já está disponível para donwload no serviço S3
7 - O servidor realiza o download
8 - O cliente é redirecionado para a página do player do vídeo

## Front End

O Front End foi construído de maneira simples e a parte do projeto da aplicação. Foi utilizado o player de vídeo para HTML5, disponível nos navegadores Safari versão 4.0, Chrome versão 4.0, Firefox versão 3.5 e Opera versão 10.5. 

Exemplo:

```html
<video width="320" height="240" controls>
  <source src="movie.mp4" type="video/mp4">
  <source src="movie.ogg" type="video/ogg">
Your browser does not support the video tag.
</video>
```

O design foi baseado no template de Yegor, o criador do framework Tacit. Tacit presa pela simplicidade e é não utiliza classes CSS, deixando de forma simples o deploy de novas páginas utilizando HTML5. O seu projeto pode ser conferido no seguinte link [Tacit](https://github.com/yegor256/tacit).


