package com.bentoco.productcatalog.sqs;

import com.bentoco.productcatalog.service.CatalogUpdater;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogChangeConsumer {

    private final CatalogUpdater catalogUpdater;

    @SqsListener("${events.queues.catalog-emit}")
    public void receiveChangeMessage(String ownerId) {
        catalogUpdater.execute(ownerId);
    }

}
