package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.mappers.CategoryMapper;
import com.bentoco.productcatalog.utils.StringUtils;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;
import java.util.UUID;

import static com.bentoco.productcatalog.dynamodb.tables.CategoryTable.CATEGORY_PREFIX;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbTemplate dynamoDbTemplate;
    private final RequestContext requestContext;

    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(CategoryPersistence.class);

    @Override
    public UUID insert(final Category category) {

        Owner owner = new Owner(requestContext.getProfile().ownerId());
        category.setOwner(owner);

        CategoryTable categoryTable = categoryMapper.toTable(category);

        logger.info("inserting category item: {}", categoryTable);
        try {
            var result = dynamoDbTemplate.save(categoryTable);
            return StringUtils.removePrefix(result.getCategoryId(), CATEGORY_PREFIX);
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void update(final Category category) {
        Key key = getKey(category.getId());
        var entity = dynamoDbTemplate.load(key, CategoryTable.class);
        if (Objects.isNull(entity)) {
            throw new DynamoDbOperationsErrorException("item not found");
        }
        entity.setTitle(category.getTitle());
        entity.setDescription(category.getDescription());
        dynamoDbTemplate.save(entity);
    }

    @Override
    public void delete(final UUID categoryId) {
        Key key = getKey(categoryId);
        try {
            dynamoDbTemplate.delete(key, CategoryTable.class);
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private static Key getKey(UUID categoryId) {
        String partitionKey = StringUtils.prefixedId(categoryId.toString(), CATEGORY_PREFIX);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }
}
