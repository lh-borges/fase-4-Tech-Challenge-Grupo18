# Começa por aqui

Essa é a parte de feedback do sistema (avaliação + relatório). É um Spring Boot com Postgres.  

## Rodando na sua máquina

Precisa de um Postgres de pé. As credenciais estão no `SysFeedback/src/main/resources/application.properties` (banco `grupo18`, user `postgres`, senha `123456`). Se o seu for diferente, muda lá antes de subir.
  
Depois, dentro da pasta `SysFeedback`:
  
```
./mvnw spring-boot:run
```

Sobe na porta 8080.

## O banco já vem populado

Na primeira vez que a aplicação sobe ela popula o banco sozinha. O código disso está em `infrastructure/config/DataSeeder`. Ele só roda quando o banco está vazio, então pode reiniciar a aplicação quantas vezes quiser que não duplica nada. Se quiser gerar os dados de novo do zero, limpa as tabelas (ou dropa o banco) e sobe outra vez.

O seed cria três usuários, um de cada tipo:

- admin@fiap.com / 123456 (ADMIN)
- professor@fiap.com / 123456 (PROFESSOR)
- aluno@fiap.com / 123456 (ALUNO)

E umas 18 avaliações espalhadas pelos últimos ~20 dias, com notas de 0 a 10, pro relatório ter dado de verdade e não vir vazio.

## Testando

Tem uma collection do Postman pronta na pasta `POSTMAN_FEEDBACK`. Importa ela no Postman, roda o Login primeiro (o token fica salvo sozinho) e vai chamando o resto.

A ordem que faz sentido:

1. Login com o aluno e manda uma avaliação (POST /avaliacoes). Quem cria avaliação é o aluno.
2. Login com o professor ou o admin pra ler as avaliações (GET /avaliacoes) e gerar o feedback do período (POST /feedback, passando `inicio` e `fim`).

O feedback é sempre por período. Como o seed jogou avaliação nos últimos ~20 dias, usa um intervalo tipo o mês atual inteiro que vem tudo.

## Produção

No Cloud Run o seed roda igual, na primeira subida. Como lá o banco começa vazio, ele popula uma vez e segue o jogo.
