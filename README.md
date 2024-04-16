Our product management service provides customers with catalogs, drawing inspiration from the Anota.ai challenge.

Reference: https://github.com/githubanotaai/new-test-backend-nodejs

## Accessing the OpenAI Swagger UI
To explore the API documentation and interact with the endpoints using Swagger UI, follow these steps:

1. Ensure that the application is running locally on your machine. If not, [start the application]().

2. Open your web browser and navigate to the Swagger UI URL:
```bash
http://localhost:8080/product-catalog/swagger-ui/index.html
```
3. You will be redirected to the Swagger UI interface, where you can browse the available endpoints, view their parameters, and test them interactively.

4. Feel free to explore the various endpoints, make requests, and examine the responses to familiarize yourself with the functionality provided by the API.
 
 
## DynamoDB Table Structure

**1. Product Table:**

- **Description:** The Product table stores information about various products registered by users. Each product has a unique identifier (ProductID) and is associated with an owner (OwnerID). Other attributes include title, description, price, and categoryTable.

    - **Table Name:** Product
    - **Primary Key:** ProductID (Partition Key)
    - **Attributes:**
        - ProductID (String): Unique identifier for the product.
        - Title (String): Title of the product.
        - Description (String): Description of the product.
        - Price (Number): Price of the product.
        - OwnerID (String): ID of the owner of the product.
        - CategoryID (String): ID of the categoryTable to which the product belongs.
    - **Global Secondary Indexes:**
        - OwnerIDIndex:
            - **Index Name:** OwnerIDIndex
            - **Partition Key:** OwnerID
            - **Projection:** All attributes
            - **Provisioned Throughput:** 5 Read Capacity Units, 5 Write Capacity Units

**2. Category Table:**

- **Description:** The Category table stores information about different category registered by users. Each categoryTable has a unique identifier (CategoryID) and is associated with an owner (OwnerID). Attributes include title and description.

    - **Table Name:** Category
    - **Primary Key:** CategoryID (Partition Key)
    - **Attributes:**
        - CategoryID (String): Unique identifier for the categoryTable.
        - Title (String): Title of the categoryTable.
        - Description (String): Description of the categoryTable.
        - OwnerID (String): ID of the owner of the categoryTable.
    - **Global Secondary Indexes:**
        - OwnerIDIndex:
            - **Index Name:** OwnerIDIndex
            - **Partition Key:** OwnerID
            - **Projection:** All attributes
            - **Provisioned Throughput:** 5 Read Capacity Units, 5 Write Capacity Units

**3. ProductCategory Mapping Table:**

- **Description:** The ProductCategory mapping table establishes the association between products and category. Each entry in this table represents a product-categoryTable relationship. The ProductID serves as the partition key, and the CategoryID serves as the sort key.

    - **Table Name:** ProductCategoryMapping
    - **Primary Key:**
        - Partition Key: ProductID
        - Sort Key: CategoryID
    - **Attributes:** None (The table serves as a mapping between products and category)
    - **Provisioned Throughput:** 5 Read Capacity Units, 5 Write Capacity Units

**Usage:**
- The Product and Category tables store detailed information about products and category respectively.
- The ProductCategory mapping table facilitates the association between products and category.
- Users can efficiently query products or category by their owner using the provided Global Secondary Indexes.

**Note:** Adjust the provisioned throughput (Read and Write Capacity Units) based on anticipated workload and access patterns. Additionally, consider implementing auto-scaling for more flexible capacity management. 