
# API Bancária - Desafio de Desenvolvimento

## Objetivo

O objetivo desse desafio é desenvolver uma API Bancária que realize operações básicas, incluindo:

- Criação de contas com usuário e senha
- Depósito de valores
- Saque de valores
- Transferência entre contas
- Exclusão de contas.

---

## Requisitos Técnicos

A API deve ser desenvolvida utilizando uma linguagem de sua escolha, seguindo boas práticas de desenvolvimento, arquitetura limpa e princípios de segurança. Será necessário a criação de uma imagem Docker para testes e exponde a porta HTTP 6543.

___(!) As mensagens de log devem estar na saida padrão___

---

## Endpoints da API

Abaixo estão os endpoints que essa API deve fernecer:

### 1. Criar Conta

- **POST** `/account`
- **Descrição**: Cria uma nova conta bancária, informando as dados pessoais do usuário.
A responsta será o número da conta e o nome completo do usuário.

- __Campos__:
- **firstName** (mandatório): Primeiro nome.
- **lastName** (mandatório): Último nome.
- **nickName** (opcional): Apelido
- **dob** (mandatório): Data de nascimento (formato: YYYY-MM-DD)
- **age** (mandatório): Idade
- **isMale** (mandatório): Se masculino o valor deve ser true
- **document** (mandatório): CPF do usuário
- **country** (mandatório): Nome do pais
- **state** (mandatório): Estado ou província
- **city** (mandatório): Cidade onde usuário reside
- **phone** (mandatório): Número de telefone do usuário
- **email** (mandatório): E-mail do usuário
- **account** (mandatório): Número da conta (dever ser UUID)
- **fullName** (mandatório): Nome completo de usuário se existir apelido adiciona apelido detro de parentesis.
- **Body (JSON)**:
  ```json
  {
    "firstName": "",
    "lastName": "",
    "nickName": "",
    "dob": "",
    "age": 0,
    "isMale": true,
    "document": "",
    "address": {
      "country": "",
      "state": "",
      "city": ""
    },
    "phone": "",
    "email":""
  }
  ```
- **Resposta**
  - Codigo de resposta HTTP de erro interno - 500 (ver item 2)
  - Codigo de resposta HTTP para usuário existente - 422 (ver item 2)
  - Código de resposta HTTP: 200 (OK)
  ``` json
  {
    "account": "123456789",
    "fullName": "firstName + lastName (nickName)",
  }
  ```

### 2. Atualizar Dados da Conta

- **PUT** `/account/{accountId}`
- **Descrição**: Atualizar dados da conta bancária.

- __Campos__:
- **firstName**: Primeiro nome.
- **lastName**: Último nome.
- **nickName**: Apelido
- **dob**: Data de nascimento (formato: YYYY-MM-DD)
- **age**: Idade
- **isMale**: Se masculino o valor deve ser true
- **document**: CPF do usuário
- **country**: Nome do pais
- **state**: Estado ou província
- **city**: Cidade onde usuário reside
- **phone**: Número de telefone do usuário
- **email**: E-mail do usuário
- **accountId**: Identificador unico da conta, retorna no momento da criação.apelido detro de parentesis.
- **Body (JSON)**:
  ```json
  {
    "firstName": "",
    "lastName": "",
    "nickName": "",
    "dob": "",
    "age": 0,
    "isMale": true,
    "document": "",
    "address": {
      "country": "",
      "state": "",
      "city": ""
    },
    "phone": "",
    "email":""
  }
  ```
- **Resposta**
  - Codigo de resposta HTTP de erro interno - 500 (ver item 2)
  - Codigo de resposta HTTP para usuário nao existente - 404 (ver item 2)
  - Código de resposta HTTP: 204 (No content)

### 3. Habilitar/desabilitar conta
- **GET** `/account/{id}/{state}`
- **Descrição**: Habilita/desabilita conta de acordo com o valor de **state**.
state: ***enable** para habilitar e qualquer outro valor desabilita.


### 4. Listar Contas
(!) Com suporte a query string
- **GET** `/account`
- **Descrição**: Lista todas as contas cadastradas.
- **Resposta (200 OK)**:
  ```json
  [
    {
      "account": "123456789",
      "fullname": "nome_de_usuario",
      "balance": 0
    }
  ]
  ```

### 5. Depósito

- **POST** `/account/{id}/deposit`
- **Descrição**: Realiza um depósito na conta especificada.
- **Body (JSON)**:
  ```json
  {
    "amount": 100.00,
    "currencyCode": "840"
  }
  ```
- **Resposta (200 OK)**:
  ```json
  {
    "balance": 100.00
  }
  ```

### 6. Saque

- **POST** `/account/{id}/withdrawal`
- **Descrição**: Realiza um saque da conta especificada.
- **Body (JSON)**:
  ```json
  {
    "amount": 50.00,
    "currencyCode": "840"
  }
  ```
- **Resposta (200 OK)**:
  ```json
  {
    "balance": 50.00
  }
  ```

### 7. Transferência

- **POST** `/account/{id}/money-transfer`
- **Descrição**: Transfere um valor da conta de origem (autenticada) para uma conta de destino.
- **Body (JSON)**:
  ```json
  {
    "accountReceiver": "",
    "amount": 25.00,
    "currencyCode":""
  }
  ```
- **Resposta (204 NO CONTENT)**

### 8. Consultar Saldo

- **GET** `/account/{id}/balance`
- **Descrição**: Consulta o saldo da conta.
- **Resposta (200 OK)**
  ```json
  {
    "balance": 25.00
  }
  ```

### 9. Report

- **GET** `/account/{id}`
- **Descrição**: Consulta de extrato.
- **Resposta (200 OK)**
  ```json
  [
    {
      "amount": 25.00,
      "extraInfo": "accountId",
      "operation":"money-transfer",
      "datetime":"YYYY-MM-DD hh:mm:ss"
    },
    {
      "amount": 25.00,
      "extraInfo": "",
      "operation":"withdarwal",
      "datetime":"YYYY-MM-DD hh:mm:ss"
    }
  ]
  ```

### 10. Apagar Conta

- **DELETE** `/account/{id}`
- **Descrição**: Apagar conta.
- **Resposta (204 NO CONTENT)**

---

## 2) Considerações Finais

- Senhas devem ser armazenadas de forma segura (ex: hash com bcrypt).
- Sinta-se livre para utilizar qualquer banco de dados relacional ou não-relacional.
- A API deve ter uma formatação de resposta padrão para erros e exceções:
  ```json
  {
    "message": "mensagem_de_erro"
  }
  ```
- Lembrando que o código de resposta HTTP 200, 201, 202 ... São códigos de respostas de sucesso, então para erros deve ser informado o código exato para o cenário de erro. **Ex:** 404 (Not found) quando uma conta não for encontrada.

---

## Como contribuir

1. Faça um fork do repositório
2. Crie sua branch: `git checkout -b seu_nome-dia_mes_ano-nome_do_seu_banco`
3. Desenvolva sua solução
4. Envie um PR com a sua proposta

---
