---
page_type: sample
languages:
- java
products:
- azure
description: "Azure CosmosDB is a globally distributed multi-model database."
urlFragment: "azure-cosmos-java-getting-started"
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

* From a command prompt or shell, run the following command to compile and resolve dependencies.

```bash
cd cosmosdb-read-many-items-java
mvn clean package
```

* Em um prompt de comando ou shell, execute o seguinte comando para executar o aplicativo.

```bash
mvn exec:java@sync -DACCOUNT_HOST=YOUR_COSMOS_DB_HOSTNAME -DACCOUNT_KEY=YOUR_COSMOS_DB_MASTER_KEY
```

## About the code

The code included in this sample is intended to demonstrate the difference in performance between running many point operations in parallel with a multi-threaded application vs sending micro batches of point reads as tuple lists using the readMany() method.  

## Mais Informações

- [Azure Cosmos DB : Service introduction and SLA](https://docs.microsoft.com/azure/cosmos-db/sql-api-introduction)
- [Azure Cosmos DB : SQL API](https://docs.microsoft.com/en-us/azure/cosmos-db/sql-query-getting-started)
- [Java SDK Github for SQL API of Azure Cosmos DB](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/cosmos/azure-cosmos)
- [Java SDK JavaDoc for SQL API of Azure Cosmos DB](https://azuresdkdocs.blob.core.windows.net/$web/java/azure-cosmos/latest/index.html)

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
