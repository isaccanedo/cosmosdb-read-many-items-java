---
tipo_de_página: amostra
línguas:
-java
produtos:
- azul
description: "Azure CosmosDB é um banco de dados multimodelo distribuído globalmente."
urlFragment: "azure-cosmos-java-primeiros passos"
---

# Leia muitos itens com o SDK Java do Azure Cosmos DB

O Azure Cosmos DB é um banco de dados multimodelo distribuído globalmente. Uma das APIs suportadas é a API SQL, que fornece um modelo de documento JSON com consulta SQL e lógica processual JavaScript. O exemplo demonstra o método [readMany](https://docs.microsoft.com/en-us/java/api/com.azure.cosmos.implementation.itemoperations.readmany?view=azure-java-stable) no [ SDK Java para Azure Cosmos DB](https://docs.microsoft.com/azure/cosmos-db/sql/sql-api-sdk-java-v4).

## Começando

### Pré-requisitos

* Antes de poder executar esta amostra, você deve ter os seguintes pré-requisitos:
* 
   * An active Azure account. If you don't have one, you can sign up for a [free account](https://azure.microsoft.com/free/). Alternatively, you can use the [Azure Cosmos DB Emulator](https://azure.microsoft.com/documentation/articles/documentdb-nosql-local-emulator) for this tutorial. As the emulator https certificate is self signed, you need to import its certificate to the java trusted certificate store as [explained here](https://docs.microsoft.com/azure/cosmos-db/local-emulator-export-ssl-certificates).

   * JDK 1.8+
   * Maven

### Quickstart

* Primeiro clone este repositório usando

```bash
git clone https://github.com/Azure-Samples/cosmosdb-read-many-items-java.git
```

* Em um prompt de comando ou shell, execute o seguinte comando para compilar e resolver dependências.

```bash
cd cosmosdb-read-many-items-java
mvn clean package
```

* Em um prompt de comando ou shell, execute o seguinte comando para executar o aplicativo.

```bash
mvn exec:java@sync -DACCOUNT_HOST=YOUR_COSMOS_DB_HOSTNAME -DACCOUNT_KEY=YOUR_COSMOS_DB_MASTER_KEY
```

## Sobre o código

O código incluído neste exemplo tem como objetivo demonstrar a diferença de desempenho entre a execução de muitas operações de pontos em paralelo com um aplicativo multithread e o envio de microlotes de leituras de pontos como listas de tuplas usando o método readMany(). 

## Mais Informações

- [Azure Cosmos DB : Service introduction and SLA](https://docs.microsoft.com/azure/cosmos-db/sql-api-introduction)
- [Azure Cosmos DB : SQL API](https://docs.microsoft.com/en-us/azure/cosmos-db/sql-query-getting-started)
- [Java SDK Github for SQL API of Azure Cosmos DB](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/cosmos/azure-cosmos)
- [Java SDK JavaDoc for SQL API of Azure Cosmos DB](https://azuresdkdocs.blob.core.windows.net/$web/java/azure-cosmos/latest/index.html)

## Contribuindo

Este projeto aceita contribuições e sugestões. A maioria das contribuições exige que você concorde com um
Contrato de Licença de Colaborador (CLA) declarando que você tem o direito, e realmente o faz, de nos conceder
os direitos de usar sua contribuição. Para obter detalhes, visite https://cla.opensource.microsoft.com.

Quando você envia uma solicitação pull, um bot CLA determinará automaticamente se você precisa fornecer
um CLA e decorar o PR adequadamente (por exemplo, verificação de status, comentário). Basta seguir as instruções
fornecido pelo bot. Você só precisará fazer isso uma vez em todos os repositórios que usam nosso CLA.

Este projeto adotou o [Código de Conduta de Código Aberto da Microsoft](https://opensource.microsoft.com/codeofconduct/).
Para obter mais informações, consulte as [Perguntas frequentes sobre o Código de Conduta](https://opensource.microsoft.com/codeofconduct/faq/) ou
entre em contato com [opencode@microsoft.com](mailto:opencode@microsoft.com) se tiver perguntas ou comentários adicionais.
