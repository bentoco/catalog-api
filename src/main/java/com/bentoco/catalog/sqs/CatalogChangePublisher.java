package com.bentoco.catalog.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogChangePublisher {

    @Value("${events.queues.catalog-emit}")
    private String catalogEmmitQueueName;

    private final SqsTemplate template;

    public void send(final String ownerId) {
        template.send(to ->
                to.queue(catalogEmmitQueueName)
                .payload(ownerId)
        );
    }
}
