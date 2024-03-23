package com.bentoco.productcatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CatalogUpdater {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    public void execute(final String ownerId) {
        logger.info("Message to update data has received: {}", ownerId);
    }
}
